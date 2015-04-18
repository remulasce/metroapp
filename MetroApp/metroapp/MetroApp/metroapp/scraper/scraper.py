import requests
import xml.etree.ElementTree as ET
import sqlite3

agencyName = raw_input("name of agency: ");
searchString = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=" + agencyName

routeListResponse = requests.get(searchString)

root = ET.fromstring(routeListResponse.text)

routeList = []

#get all the routes so we can scrape the database

for child in root:
	routeList.append(child.attrib['tag'])
	
#routelist contains a list of all routes

# [STOPID, STOPNAME, LAT, LONG]

databaselist = []

i=0.0
print len(routeList)
for route in routeList:
	print "%: " + str(i/len(routeList))
	routeRequestString = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a="+agencyName+"&r="+route
	routeResponse = requests.get(routeRequestString)
	
	print routeRequestString
	
	root = ET.fromstring(routeResponse.text)
	for route in root:
		if route.tag == "route":
			for child in route:
				if child.tag == "stop":
					if "stopId" in child.attrib.keys():
						print child.attrib
						uniquetag = child.attrib["tag"]
						stopid = child.attrib["stopId"]
						stopname = child.attrib["title"]
						lat = child.attrib["lat"]
						lon = child.attrib["lon"]
						newstop = 1
						for el in databaselist:
							if el[0] == stopid:
								newstop = 0
							#print "Match: " + str(el[0]) + ", " + str(stopid)
						if newstop==1:
							databaselist.append( (uniquetag,stopid,stopname,lat,lon) )
							#print databaselist
		
conn = sqlite3.connect(agencyName + '.db')

c = conn.cursor()

# Create table
c.execute('''CREATE TABLE stopnames
             (uniqueid integer, stopid text, stopname text, latitude real, longitude real)''')

c.executemany('INSERT INTO stopnames VALUES (?,?,?,?,?)', databaselist)

# Save (commit) the changes
conn.commit()

# We can also close the connection if we are done with it.
# Just be sure any changes have been committed or they will be lost.
conn.close()
	
	