import requests
import xml.etree.ElementTree as ET
import sqlite3
import sys

OUTPUT_DIR = "_out/"

print("Specify which agencies to scrape as command-line arguments")
print("Or leave none to scrape only supported agencies")
print("Command-line agencies won't have their proper titles scraped")

agencies_to_scrape = []

for arg in sys.argv[1:]:
    print(arg)
    agencies_to_scrape.append((arg, arg))

if len(agencies_to_scrape) == 0:
    print("No arguments given; scraping sane defaults")

    agencies_to_scrape = ["lametro", "lametro-rail", "sf-muni"]


# This likely trips their ratelimiting
def get_all_agencies_dict():
    all_agencies = dict()  # raw, displayname
    agency_list_request_string = "http://retro.umoiq.com/service/publicXMLFeed?command=agencyList"
    agency_list_response = requests.get(agency_list_request_string)
    tree_root = ET.fromstring(agency_list_response.text)
    for tree_child in tree_root:
        if tree_child.tag == "agency":
            tag = tree_child.attrib['tag']
            display_name = tree_child.attrib['title']
            all_agencies[tag] = display_name
    if not len(all_agencies):
        raise Error("Couldn't create agencies mapping")
    return all_agencies


print("Scraping " + str(len(agencies_to_scrape)) + " agencies")


def main():
    agency_names_dict = get_all_agencies_dict()
    
    for agency_tag in agencies_to_scrape:
        agency_display_name = agency_names_dict[agency_tag]
        print("Now scraping: " + agency_tag + ", " + agency_display_name)
        search_string = "http://retro.umoiq.com/service/publicXMLFeed?command=routeList&a=" + agency_tag

        route_list_response = requests.get(search_string)

        root = ET.fromstring(route_list_response.text)

        routeList = []

        # get all the routes so we can scrape the database

        for child in root:
            routeList.append(child.attrib['tag'])

        print("Found " + str(len(routeList)) + " routes for agency " + agency_tag)
        # routelist contains a list of all routes

        agencyid = agency_tag
        agencyLatMin = None;
        agencyLatMax = None;
        agencyLonMin = None;
        agencyLonMax = None;

        # [AGENCYID, AGENCYTITLE, LATMIN, LATMAX, LONMIN, LONMAX]
        agencyInfo = []

        # [UNIQUEID, STOPID, STOPNAME, LAT, LONG]
        stopnamesList = []

        # I think this is the right way to do relational databases:
        # Each cell should basically be one datum.
        # So, to store a mapping of stop->routes visiting that stop,
        # you add multiple rows("records") mapping the stop to each route.
        # So each row ROWn says "Stop s has ROUTEn"
        #
        # To get all the routes to a stop, you query sqlite for all the rows
        # with stopid matching.
        #
        # Obviously the Internet won't tell you outright, but this makes sense to me
        # because to add new information, you don't need to access any previous info-
        # you just add a new row.
        #
        # Therefore, the stop-routes table for each agency simply has two columns:
        # [STOPID, ROUTE]
        stoproutesList = []

        # For future conversion from stopid to route + stopTag
        # This will avoid some issues with agencies not assigning stopids to some stops
        # Technically they're optional. It's just some agencies fuck up, like, 2 of them.
        #
        # Note that this isn't just an alternate way of calling stopid. Some stopid stops
        # will be split into multiple route + stopTags.
        # [STOPNAME, ROUTE, STOPTAG]
        stopnameroutetagList = []

        i = 0.0
        uniquetag = 1
        for routeTag in routeList:
            i = i + 1
            print(agency_tag + " " + str(int(100 * i / len(routeList))) + "%")
            routeRequestString = "http://retro.umoiq.com/service/publicXMLFeed?command=routeConfig&a=" + agency_tag + "&r=" + routeTag
            routeResponse = requests.get(routeRequestString)

            print(routeRequestString)

            root = ET.fromstring(routeResponse.text)
            for routeObject in root:
                if routeObject.tag == "route":
                    # Collect the bounds of each route to calculate the agency's total service area
                    # Add .5 lon / lat to show where we could possibly want data. This is pretty far away from the agency.

                    routeLatMin = float(routeObject.attrib["latMin"]) - .5;
                    routeLonMin = float(routeObject.attrib["lonMin"]) - .5;
                    routeLatMax = float(routeObject.attrib["latMax"]) + .5;
                    routeLonMax = float(routeObject.attrib["lonMax"]) + .5;
                    # print "Found area bounds for route "+routeObject.attrib["title"]+": "+routeLatMin+" "+routeLatMax+" "+routeLonMin+" "+routeLonMax

                    if (agencyLatMin == None):
                        agencyLatMin = routeLatMin
                        agencyLatMax = routeLatMax
                        agencyLonMin = routeLonMin
                        agencyLonMax = routeLonMax
                    else:
                        agencyLatMin = min(routeLatMin, agencyLatMin)
                        agencyLatMax = max(routeLatMax, agencyLatMax)
                        agencyLonMin = min(routeLonMin, agencyLonMin)
                        agencyLonMax = max(routeLonMax, agencyLonMax)

                    for child in routeObject:
                        if child.tag == "stop":
                            # Don't know if stoptag needs some cleaning like stopid
                            stopname = child.attrib["title"]
                            stoptag = child.attrib["tag"]
                            stopnameroutetagTuple = (stopname, routeTag, stoptag)
                            if not stopnameroutetagTuple in stopnameroutetagList:
                                stopnameroutetagList.append(stopnameroutetagTuple)
                            # print stopnameroutetagTuple

                            if "stopId" in list(child.attrib.keys()):
                                # print child.attrib
                                # uniquetag = child.attrib["tag"]
                                stopid = child.attrib["stopId"]
                                stopid = stopid.lstrip("0");

                                lat = child.attrib["lat"]
                                lon = child.attrib["lon"]
                                newstop = 1
                                for el in stopnamesList:
                                    if el[1] == stopid:
                                        newstop = 0
                                # print "Match: " + str(el[0]) + ", " + str(stopid)
                                if newstop == 1:
                                    stopnamesList.append((uniquetag, stopid, stopname, lat, lon))
                                    uniquetag = uniquetag + 1
                                # print stopnamesList

                                stoprouteTuple = (stopid, routeTag)
                                if not stoprouteTuple in stoproutesList:
                                    stoproutesList.append(stoprouteTuple)
                                # print stoproutesList

        print("Finished scraping " + agency_tag + ", saving to SQL...");
        conn = sqlite3.connect(OUTPUT_DIR + agency_tag + '.db')

        c = conn.cursor()
        # Delete old contents, if any
        c.execute('''DROP TABLE IF EXISTS stopnames''')
        c.execute('''DROP TABLE IF EXISTS stoproutes''')
        c.execute('''DROP TABLE IF EXISTS stopnameroutetags''')
        c.execute('''DROP TABLE IF EXISTS agencyinfo''')

        # Create table
        c.execute('''CREATE TABLE stopnames
                     (uniqueid integer, stopid text, stopname text, latitude real, longitude real)''')
        # Correct way to make primary keys, Nighelles
        c.execute('''CREATE TABLE stoproutes
                     ( stopid text, route text)''')
        # ...Is to ignore them. Sqlite will add one for you anyway, and increment it itself.
        c.execute('''CREATE TABLE stopnameroutetags
                     ( stopname text, route text, stoptag text)''')
        # Overall info about the agency. Agencyid should be internal id, like lametro-rail, with the name being user-facing
        c.execute('''CREATE TABLE agencyinfo
                         ( agencyid text, agencyname text, latMin real, latMax real, lonMin real, lonMax real )''')

        c.executemany('INSERT INTO stopnames VALUES (?,?,?,?,?)', stopnamesList)
        c.executemany('INSERT INTO stoproutes VALUES (?,?)', stoproutesList)
        c.executemany('INSERT INTO stopnameroutetags VALUES (?,?,?)', stopnameroutetagList)
        c.execute('INSERT INTO agencyinfo VALUES (?,?,?,?,?,?)',
                  (agencyid, agency_display_name, agencyLatMin, agencyLatMax, agencyLonMin, agencyLonMax))

        # Save (commit) the changes
        conn.commit()

        # We can also close the connection if we are done with it.
        # Just be sure any changes have been committed or they will be lost.
        conn.close()

        print("Done with " + agency_tag)


main()

print("Complete.")
