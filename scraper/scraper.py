import requests
import xml.etree.ElementTree as ET
import sqlite3
import sys


print "Specify which agencies to scrape as command-line arguments"
print "Or leave none to scrape all agencies"

agencyList = []


for arg in sys.argv[1:]:
        print arg
        agencyList.append(arg)

if len(agencyList) == 0:
        print "No arguments given; scraping all available agencies"
        
        agencyListRequestString = "http://webservices.nextbus.com/service/publicXMLFeed?command=agencyList"
        agencyListResponse = requests.get(agencyListRequestString)
        root = ET.fromstring(agencyListResponse.text)
        for child in root:
                if child.tag == "agency":
                        agencyList.append(child.attrib['tag'])

print "Scraping " + str(len(agencyList)) + " agencies"		
		
for agencyName in agencyList:
	print "Now scraping: " + agencyName
	searchString = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=" + agencyName

	routeListResponse = requests.get(searchString)

	root = ET.fromstring(routeListResponse.text)

	routeList = []

	#get all the routes so we can scrape the database

	for child in root:
		routeList.append(child.attrib['tag'])

	print "Found "+str(len(routeList))+" routes for agency "+agencyName
	#routelist contains a list of all routes

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
        
	i=0.0
	uniquetag = 1
	for routeTag in routeList:
		i = i + 1
		print agencyName + " " + str(int(100*i/len(routeList))) + "%"
		routeRequestString = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a="+agencyName+"&r="+routeTag
		routeResponse = requests.get(routeRequestString)
	
		print routeRequestString
	
		root = ET.fromstring(routeResponse.text)
		for routeObject in root:
			if routeObject.tag == "route":
				for child in routeObject:
					if child.tag == "stop":
						if "stopId" in child.attrib.keys():
							#print child.attrib
							#uniquetag = child.attrib["tag"]
							stopid = child.attrib["stopId"]
							stopid = stopid.lstrip("0");
							stopname = child.attrib["title"]
							lat = child.attrib["lat"]
							lon = child.attrib["lon"]
							newstop = 1
							for el in stopnamesList:
								if el[1] == stopid:
									newstop = 0
								#print "Match: " + str(el[0]) + ", " + str(stopid)
							if newstop==1:
								stopnamesList.append( (uniquetag,stopid,stopname,lat,lon) )
								uniquetag = uniquetag + 1
								#print stopnamesList

                                                        stoprouteTuple = (stopid, routeTag)
                                                        if not stoprouteTuple in stoproutesList:
                                                                stoproutesList.append( stoprouteTuple )
                                                                #print stoproutesList

	print "Finished scraping "+agencyName+", saving to SQL...";
	conn = sqlite3.connect(agencyName + '.db')

	c = conn.cursor()
	# Delete old contents, if any
	c.execute('''DROP TABLE stopnames''')
	c.execute('''DROP TABLE stoproutes''')

	# Create table
	c.execute('''CREATE TABLE stopnames
	             (uniqueid integer, stopid text, stopname text, latitude real, longitude real)''')
	# Correct way to make primary keys, Nighelles
	c.execute('''CREATE TABLE stoproutes
	             ( stopid text, route text)''')
	# ...Is to ignore them. Sqlite will add one for you anyway, and increment it itself.

	c.executemany('INSERT INTO stopnames VALUES (?,?,?,?,?)', stopnamesList)
	c.executemany('INSERT INTO stoproutes VALUES (?,?)', stoproutesList)

	# Save (commit) the changes
	conn.commit()

	# We can also close the connection if we are done with it.
	# Just be sure any changes have been committed or they will be lost.
	conn.close()

	print "Done with "+agencyName

print "Complete."
	
