import requests
import xml.etree.ElementTree as ET
import sqlite3
import sys


print "Bay Area 511.org scraper"
print "Specify which agencies to scrape as command-line arguments"
print "Or leave none to scrape all agencies"
print "Command-line agencies won't have their proper titles scraped"

agencyList = []


for arg in sys.argv[1:]:
        print arg
        agencyList.append( (arg, arg) )

if len(agencyList) == 0:
        print "No arguments given; scraping all available agencies"
        
        agencyListRequestString = "http://services.my511.org/Transit2.0/GetAgencies.aspx?token=7a0b4b7b-6a70-46d7-85aa-8a202fc44471"
        agencyListResponse = requests.get(agencyListRequestString)
        root = ET.fromstring(agencyListResponse.text).find("AgencyList")
        agencyElement = root.find("AgencyList")
        for child in root:
                if child.tag == "Agency":
                        agencyList.append( (child.attrib['Name'], child.attrib['Name']) )

print "Scraping " + str(len(agencyList)) + " agencies"          
                
for agencyParam in agencyList:
        agencyName = agencyParam[0]
        print "Now scraping: " + agencyName

        # Bay area online service does not provide stop locations.
        # So we must parse them ourselves from individual agency gtfs files.
        stops_dict = dict()
        with open(agencyName+"_gtfs/stops.txt") as stops_gtfs:
            stops_gtfs.readline()
            for line in stops_gtfs:
                split = line.split(",")
                #stop_code, (lat, lon)
                stops_dict[split[1].strip('"')] = (float(split[4].strip('"')), float(split[5].strip('"')))
        

        searchString = "http://services.my511.org/Transit2.0/GetRoutesForAgency.aspx?token=7a0b4b7b-6a70-46d7-85aa-8a202fc44471&agencyName=" + agencyName.replace(' ', '%20')

        routeListResponse = requests.get(searchString)

        root = ET.fromstring(routeListResponse.text)
        root = root.find("AgencyList").find("Agency")

        hasDirection = root.attrib['HasDirection']

        root = root.find("RouteList")

        routeList = []

        #get all the routes so we can scrape the database

        for child in root:
                if (hasDirection == "True"):
                        for directionChild in child.find("RouteDirectionList"):
                                #Append each route+direction as its own route, because that's how norcal does things.
                                routeList.append( (child.attrib['Code'], directionChild.attrib['Code']) )
                else:
                        routeList.append( (child.attrib['Code'], None) )

        print "Found "+str(len(routeList))+" routes for agency "+agencyName
        #routelist contains a list of all routes

        agencyid = agencyName
        agencytitle = agencyParam[1]
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
        #[STOPNAME, ROUTE, STOPTAG]
        stopnameroutetagList = []
        
        i=0.0
        uniquetag = 1
        for route in routeList:
                routeTag = route[0]
                routeDir = route[1]
                i = i + 1
                print agencyName + " " + str(int(100*i/len(routeList))) + "%"
                routeRequestString = "http://services.my511.org/Transit2.0/GetStopsForRoute.aspx?token=7a0b4b7b-6a70-46d7-85aa-8a202fc44471&routeIDF="+agencyName+"~"+routeTag
                if routeDir != None:
                        routeRequestString = routeRequestString + "~" + routeDir
                routeResponse = requests.get(routeRequestString)
        
                print routeRequestString

                root = ET.fromstring(routeResponse.text).find("AgencyList").find("Agency").find("RouteList")
                for routeObject in root:
                        if routeObject.tag == "Route":
                                # Collect the bounds of each route to calculate the agency's total service area
                                # Add .5 lon / lat to show where we could possibly want data. This is pretty far away from the agency.
                                """
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
                                """
                                for child in routeObject.find("RouteDirectionList").find("RouteDirection").find("StopList"):
                                        if child.tag == "Stop":
                                                # Don't know if stoptag needs some cleaning like stopid
                                                stopname = child.attrib["name"]
                                                stoptag = child.attrib["StopCode"]

                                                # Bay area 511 is generally fucked. So just don't bother.
                                                if not str(stoptag) in stops_dict:
                                                    print "Skipping "+stoptag+" on "+routeTag+" for "+agencytitle+" because we couldn't find the stop in stops.txt"
                                                    continue

                                                stopnameroutetagTuple = (stopname, routeTag, stoptag)
                                                if not stopnameroutetagTuple in stopnameroutetagList:
                                                        stopnameroutetagList.append( stopnameroutetagTuple )
                                                        #print stopnameroutetagTuple
                                                
                                                lat = stops_dict[str(stoptag)][0]
                                                lon = stops_dict[str(stoptag)][1]

                                                newstop = 1
                                                for el in stopnamesList:
                                                        if el[1] == stoptag:
                                                                newstop = 0
                                                        #print "Match: " + str(el[0]) + ", " + str(stopid)
                                                if newstop==1:
                                                        stopnamesList.append( (uniquetag,stoptag,stopname,lat,lon) )
                                                        uniquetag = uniquetag + 1
                                                        #print stopnamesList

                                                stoprouteTuple = (stoptag, routeTag)
                                                if not stoprouteTuple in stoproutesList:
                                                        stoproutesList.append( stoprouteTuple )
                                                        #print stoproutesList

        print "Finished scraping "+agencyName+", saving to SQL...";
        conn = sqlite3.connect(agencyName + '.db')

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
        c.execute('INSERT INTO agencyinfo VALUES (?,?,?,?,?,?)', (agencyid, agencytitle, agencyLatMin, agencyLatMax, agencyLonMin, agencyLonMax))

        # Save (commit) the changes
        conn.commit()

        # We can also close the connection if we are done with it.
        # Just be sure any changes have been committed or they will be lost.
        conn.close()

        print "Done with "+agencyName

print "Complete."
        
