import requests
import xml.etree.ElementTree as ET
import sqlite3
from collections import defaultdict
import sys
import gtfs_auto_db
from pathlib import Path
import contextlib


BAY_511_FEED_URL = "http://api.511.org/transit/"
API_KEY = "f036cd72-4465-425d-9ce2-df2478c7f804&"
# Stupid XML namespaces make things not work
XMLNS_NAMESPACE_PREFIX = "{http://www.netex.org.uk/netex}"

OUTPUT_DIR = "_out/"

print("Bay Area 511.org scraper")
print("Specify which agencies to scrape as command-line arguments")
print("Or leave none to scrape all agencies")
print("GTFS stops.txt must be supplied to _gtfs/<agencyname>/stops.txt for each agency")



def get_operator_names_dict():
    all_agencies = dict()  # raw, displayname
    agency_list_response = requests.get(get_all_agencies_query())

    tree_root = ET.fromstring(agency_list_response.text)
    for tree_child in tree_root.iter(XMLNS_NAMESPACE_PREFIX + "Operator"):
        tag = tree_child.attrib['id']
        display_name = tree_child.find(XMLNS_NAMESPACE_PREFIX + "Name").text
        all_agencies[tag] = display_name
    if not len(all_agencies):
        raise Exception("Couldn't create agencies mapping")
    print("Created agencies map: " + str(all_agencies))
    return all_agencies


def main():
    operators_to_scrape = get_operators_to_scrape()

    operator_names = get_operator_names_dict()
    print("Scraping " + str(len(operators_to_scrape)) + " operators")

    for operator_id in operators_to_scrape:
        operator_display_name = operator_names[operator_id]
        scrape_operator(operator_display_name, operator_id)

# Probably a list of tuples, [stop_id, route_id]
def get_routes_for_stopid(gtfs_db, stopid):
    cursor = gtfs_db.execute("SELECT DISTINCT stop_id, route_id FROM stop_times" +
                                 " WHERE stopid == " + stopid +
                                 " INNER JOIN trips" +
                                 " ON stop_times.trip_id == trips.trip_id" +
                                 " ORDER BY stop_id")
    results = cursor.fetchall()
    return results


# Probably a list of tuples, [stop_id, route_id]
def get_all_stopid_routes(gtfs_db):
    cursor = gtfs_db.execute("SELECT DISTINCT stop_id, route_id FROM stop_times" +
                                 " INNER JOIN trips" +
                                 " ON stop_times.trip_id == trips.trip_id" +
                                 " ORDER BY stop_id")
    results = cursor.fetchall()
    return results


def scrape_operator(operator_display_name, operator_id):
    print("Now scraping: " + operator_id + ", " + operator_display_name)

    gtfs_db = get_gtfs_database(operator_id)

    # [AGENCYID, AGENCYTITLE, LATMIN, LATMAX, LONMIN, LONMAX]
    agencyInfo = []

    # I think uniqueId was a SQL misunderstanding.
    # I think this should be sufficient, if the provider gives it to you in a recognizable way.
    # [UNIQUEID, STOPID, STOPNAME, LAT, LONG]
    stopnames_list = []

    # If the provider does things weirdly, then these are necessary to query arrivals. However, stopid directly should
    # be sufficient
    # [STOPNAME, ROUTE, STOPTAG]
    stopnameroutetagList = []

    # Necessary for stop special colors, which are hardcoded at the route level in-app.
    # [STOPID, ROUTE]
    stoproutesList = []

    # Initial, kinda-close values, but married to the API side, to be lightly converted forward.
    print("Parsing all stops in one go. This could take a while with no progress report.")
    raw_stops = get_all_stops_from_network(operator_id)  # stopid, stopname, lat, lon
    # [ stopid, routeid ]
    raw_stop_routes = get_all_stopid_routes(gtfs_db)


    stopnames_list = raw_stops
    stoproutesList = raw_stop_routes
    stopnameroutetagList = [(stop, route, stop) for stop, route in raw_stop_routes]
    agencyLatMin = 0
    agencyLatMax = 0
    agencyLonMin = 0
    agencyLonMax = 0

    print("stopnamest " + str(stopnames_list))
    print("stoproutes " + str(stoproutesList))
    print("snrt: " + str(stopnameroutetagList))

    print("Finished scraping " + operator_id + ", found " + str(len(stopnames_list)) + ", saving to SQL...");
    commit_to_database(agencyLatMax, agencyLatMin, agencyLonMax, agencyLonMin, operator_display_name, operator_id,
                       stopnameroutetagList, stopnames_list, stoproutesList)
    print("Done with " + operator_id)
    gtfs_db.close()


# You can't actually call this because the api is limited by call, to 60 per hour.
def scrape_stoproutes_one_at_a_time(operator_id):
    raw_stop_routes = []

    i = 0.0
    uniquetag = 1
    routeList = get_routes_list(operator_id)
    print("Found " + str(len(routeList)) + " routes for agency " + operator_id)
    for route in routeList:
        i = i + 1  # For percentage
        print(operator_id + " " + str(int(100 * i / len(routeList))) + "%")

        # Stops on this route. [stop_id, stop_name, ...] for all stops on the route.
        raw = scrape_route_stops(operator_id, route)
        raw_stop_routes.append((route, raw[0]))  # [route, stopid]
    return raw_stop_routes


# Add to the tuple tables here
   #      # And update the agency region here as well.
   #      # We should do this _after_ we have all the stop-per-route details.
   #      if (agencyLatMin == None):
   #          agencyLatMin = routeLatMin
   #          agencyLatMax = routeLatMax
   #          agencyLonMin = routeLonMin
   #          agencyLonMax = routeLonMax
   #      else:
   #          agencyLatMin = min(routeLatMin, agencyLatMin)
   #          agencyLatMax = max(routeLatMax, agencyLatMax)
   #          agencyLonMin = min(routeLonMin, agencyLonMin)
   #          agencyLonMax = max(routeLonMax, agencyLonMax)


# Returns list of [stop_id, stop_name, lat, lon]
def scrape_route_stops(operator_id, route):
    route_request_string = stops_on_route_query(operator_id, route)
    route_response = requests.get(route_request_string)
    print(route_request_string)

    route_stops = [] # [stop_id, stop_name, lat, lon]
    root = ET.fromstring(route_response.text)
    for scheduled_stop_points in root.iter(XMLNS_NAMESPACE_PREFIX + "scheduledStopPoints"):
        # Dunno what we do if there's more than one element here, but leaving the element for posterity.
        for stop_point_element in scheduled_stop_points.iter(XMLNS_NAMESPACE_PREFIX + "ScheduledStopPoint"):
            route_stops.append(parse_stop_element(stop_point_element))
    return route_stops

# Returns individual tuple details of the stop.
# [stop_name, stop_id, lat, lon]
def parse_stop_element(stop_point_element):
    stop_id = stop_point_element.attrib["id"]
    stop_name = stop_point_element.find(XMLNS_NAMESPACE_PREFIX + "Name").text
    lat = next(stop_point_element.iter(XMLNS_NAMESPACE_PREFIX + "Latitude")).text
    lon = next(stop_point_element.iter(XMLNS_NAMESPACE_PREFIX + "Longitude")).text
    return stop_id, stop_name, lat, lon


def commit_to_database(agencyLatMax, agencyLatMin, agencyLonMax, agencyLonMin, operator_display_name, operator_id,
                       stopnameroutetagList, stopnamesList, stoproutesList):
    conn = sqlite3.connect(OUTPUT_DIR + operator_id + '.db')
    c = conn.cursor()
    # Delete old contents, if any
    c.execute('''DROP TABLE IF EXISTS stopnames''')
    c.execute('''DROP TABLE IF EXISTS stoproutes''')
    c.execute('''DROP TABLE IF EXISTS stopnameroutetags''')
    c.execute('''DROP TABLE IF EXISTS agencyinfo''')
    # Create table
    c.execute('''CREATE TABLE stopnames
                         ( stopid text, stopname text, latitude real, longitude real)''')
    # Correct way to make primary keys, Cat
    c.execute('''CREATE TABLE stoproutes
                         ( stopid text, route text)''')
    # ...Is to ignore them. Sqlite will add one for you anyway, and increment it itself.
    c.execute('''CREATE TABLE stopnameroutetags
                         ( stopname text, route text, stoptag text)''')
    # Overall info about the agency. Agencyid should be internal id, like lametro-rail, with the name being user-facing
    c.execute('''CREATE TABLE agencyinfo
                         ( agencyid text, agencyname text, latMin real, latMax real, lonMin real, lonMax real )''')
    c.executemany('INSERT INTO stopnames VALUES (?,?,?,?)', stopnamesList)
    c.executemany('INSERT INTO stoproutes VALUES (?,?)', stoproutesList)
    c.executemany('INSERT INTO stopnameroutetags VALUES (?,?,?)', stopnameroutetagList)
    c.execute('INSERT INTO agencyinfo VALUES (?,?,?,?,?,?)',
              ((operator_id), operator_display_name, agencyLatMin, agencyLatMax, agencyLonMin, agencyLonMax))
    # Save (commit) the changes
    conn.commit()
    # We can also close the connection if we are done with it.
    # Just be sure any changes have been committed or they will be lost.
    conn.close()


# stopid, stopname, lat, long
def get_all_stops_from_network(operator_id):
    stops = [] # stopid, stopname, lat, long
    route_response = requests.get(all_stops_query(operator_id))

    root = ET.fromstring(route_response.text)
    for scheduled_stop_points in root.iter(XMLNS_NAMESPACE_PREFIX + "scheduledStopPoints"):
        # Dunno what we do if there's more than one element here, but leaving the element for posterity.
        for stop_point_element in scheduled_stop_points.iter(XMLNS_NAMESPACE_PREFIX + "ScheduledStopPoint"):
            stops.append(parse_stop_element(stop_point_element))
    return stops


def get_routes_list(operator_id):
    routeList = []
    routesSearchString = all_routes_query(operator_id)
    print("scraping searchString: " + routesSearchString)
    routeListResponse = requests.get(routesSearchString)
    root = ET.fromstring(routeListResponse.text)
    line_elements = root.iter(XMLNS_NAMESPACE_PREFIX + "Line")
    # get all the routes so we can scrape the database
    for child in line_elements:
        routeList.append(child.attrib['id'])
    return routeList


def get_gtfs_stops_dict(operator_id):
    stops_dict = dict()
    with open("_gtfs/" + operator_id + "/stops.txt") as stops_gtfs:
        format = stops_gtfs.readline()
        format = format.split(",")
        format = [s.strip('"') for s in format]

        stop_id_index = format.index("stop_id")
        if ("stop_code" in format):
            stop_id_index = format.index("stop_code")
        lat_index = format.index("stop_lat")
        lon_index = format.index("stop_lon")

        for line in stops_gtfs:
            split = line.split(",")
            # stop_code, (lat, lon)
            stops_dict[split[stop_id_index].strip('"')] = (
                float(split[lat_index].strip('"')), float(split[lon_index].strip('"')))
    return stops_dict


def get_gtfs_database(operator_id):
    subfolder = Path("./_gtfs/")
    match operator_id:
        case "SC":
            subfolder = subfolder / "sc" / "GTFSTransitData_sc"
        case _:
            raise Exception("oh noes")

    dbconn = gtfs_auto_db.load_gtfs_as_database(subfolder)
    print ("Loaded sql: " + str(dbconn))
    return dbconn


def get_operators_to_scrape():
    agency_tags_to_scrape = []
    print("Scraper Bay Area 511, api.511.org edition")
    for arg in sys.argv[1:]:
        print(arg)
        agency_tags_to_scrape.append(arg)
    if len(agency_tags_to_scrape) == 0:
        # agency_tags_to_scrape = ["CT", "SM", "SC", "AC"] # FIXME: This is the real version.
        agency_tags_to_scrape = ["SC"]
        print("No arguments given; scraping sane agencies: " + str(agency_tags_to_scrape))
    return agency_tags_to_scrape


def get_all_agencies_query():
    return BAY_511_FEED_URL + "operators?api_key=" + API_KEY + "&format=xml"


def all_stops_query(operator_id):
    return BAY_511_FEED_URL + "stops?api_key=" + API_KEY + "&format=xml&operator_id=" + operator_id


def stops_on_route_query(operator_id, route_tag):
    return all_stops_query(operator_id) + "&line_id=" + route_tag


def all_routes_query(agency_tag):
    return BAY_511_FEED_URL + "lines?api_key=" + API_KEY + "operator_id=" + agency_tag.replace(
        ' ', '%20') + "&format=xml"


main()
print("Complete.")
