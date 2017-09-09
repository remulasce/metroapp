#!python2

import requests
import xml.etree.ElementTree as ET
import sqlite3

stopListRequestString = "http://api.bart.gov/api/stn.aspx?cmd=stns&key=MW9S-E7SL-26DU-VV8V"
stopListResponse = requests.get(stopListRequestString)
root = ET.fromstring(stopListResponse.text)

root = root[1]

stopnamesList = []
stoproutesList = []


agencyLatMin = None
agencyLatMax = None
agencyLonMin = None
agencyLonMax = None

for child in root:
	if child.tag == "station":
		newstopID = child[1].text
		newstopName = child[0].text
		newstopLat = child[2].text
		newstopLon = child[3].text
		
		if (agencyLatMin == None):
			agencyLatMin = float(newstopLat)-0.5
			agencyLatMax = float(newstopLat)+0.5
			agencyLonMin = float(newstopLon)-0.5
			agencyLonMax = float(newstopLon)+0.5
		else:
			agencyLatMin = min(float(newstopLat)-0.5,agencyLatMin)
			agencyLatMax = max(float(newstopLat)+0.5,agencyLatMax)
			agencyLonMin = min(float(newstopLon)-0.5,agencyLonMin)
			agencyLonMax = max(float(newstopLon)+0.5,agencyLonMax)

		stopnamesList.append([newstopID,newstopName,newstopLat,newstopLon])
		
		routesListRequestString = "http://api.bart.gov/api/stn.aspx?cmd=stninfo&orig="+newstopID+"&key=MW9S-E7SL-26DU-VV8V"
		routesListResponse = requests.get(routesListRequestString)
		routesRoot = ET.fromstring(routesListResponse.text)
		
		routesRoot = routesRoot[1][0]
		
		for routeChild in routesRoot:
			if routeChild.tag == "north_routes" or routeChild.tag == "south_roots":
				for route in routeChild:
					stoproutesList.append([newstopID,route.text])

print stopnamesList
print stoproutesList
		
agencyName = "BART"

agencyid = agencyName
agencytitle = "BART"

print "Finished scraping "+agencyName+", saving to SQL...";
conn = sqlite3.connect(agencyName + '.db')

c = conn.cursor()
# Delete old contents, if any
c.execute('''DROP TABLE IF EXISTS stopnames''')
c.execute('''DROP TABLE IF EXISTS stoproutes''')
c.execute('''DROP TABLE IF EXISTS agencyinfo''')

# Create table
c.execute('''CREATE TABLE stopnames (stopid text, stopname text, latitude real, longitude real)''')
# Correct way to make primary keys, Nighelles
c.execute('''CREATE TABLE stoproutes ( stopid text, route text)''')
c.execute('''CREATE TABLE agencyinfo
             ( agencyid text, agencyname text, latMin real, latMax real, lonMin real, lonMax real )''')

c.executemany('INSERT INTO stopnames VALUES (?,?,?,?)', stopnamesList)
c.executemany('INSERT INTO stoproutes VALUES (?,?)', stoproutesList)
c.execute('INSERT INTO agencyinfo VALUES (?,?,?,?,?,?)', (agencyid, agencytitle, agencyLatMin, agencyLatMax, agencyLonMin, agencyLonMax))


# Save (commit) the changes
conn.commit()

# We can also close the connection if we are done with it.
# Just be sure any changes have been committed or they will be lost.
conn.close()

print "Done with "+agencyName

