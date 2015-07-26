import requests
import xml.etree.ElementTree as ET
import sqlite3

stopListRequestString = "http://api.bart.gov/api/stn.aspx?cmd=stns&key=MW9S-E7SL-26DU-VV8V"
stopListResponse = requests.get(stopListRequestString)
root = ET.fromstring(stopListResponse.text)

routeList = []

for child in root:
	if child.tag == "station":
		newstopID = child[1].text
		newstopName = child[0].text
		newstopLat = child[2].text
		newstopLong = child[3].text

		routeList.append([newstopID,newstopName,newstopLat,newstopLong])

agencyName = "BART"

print "Finished scraping "+agencyName+", saving to SQL...";
conn = sqlite3.connect(agencyName + '.db')

c = conn.cursor()
# Delete old contents, if any
c.execute('''DROP TABLE IF EXISTS stopnames''')
c.execute('''DROP TABLE IF EXISTS stoproutes''')
c.execute('''DROP TABLE IF EXISTS stopnameroutetags''')

# Create table
c.execute('''CREATE TABLE stopnames (uniqueid integer, stopid text, stopname text, latitude real, longitude real)''')
# Correct way to make primary keys, Nighelles
c.execute('''CREATE TABLE stoproutes ( stopid text, route text)''')
# ...Is to ignore them. Sqlite will add one for you anyway, and increment it itself.
c.execute('''CREATE TABLE stopnameroutetags ( stopname text, route text, stoptag text)''')

c.executemany('INSERT INTO stopnames VALUES (?,?,?,?,?)', stopnamesList)
c.executemany('INSERT INTO stoproutes VALUES (?,?)', stoproutesList)
c.executemany('INSERT INTO stopnameroutetags VALUES (?,?,?)', stopnameroutetagList)

# Save (commit) the changes
conn.commit()

# We can also close the connection if we are done with it.
# Just be sure any changes have been committed or they will be lost.
conn.close()

print "Done with "+agencyName

