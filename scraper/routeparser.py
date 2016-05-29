# Parses static GTFS files into a format suitable for map route overlay
import sys
from parseGtfsStops import *
from gtfsParseUtils import *
from gtfsDictionaryUtils import *

def getDir(agencyName):
    return "gtfs_dirs/gtfs_"+agencyName


def parseRoutesDb(agency):
    print "Now parsing: "+agency

    gtfsDir = getDir(agency)
    stops = getStops(gtfsDir+"/stops.txt")

    stopTimesDict = parseGtfsDictionary(gtfsDir+"/stop_times.txt")

    writeToDb(
        'routelines/'+agency+'.db',
         'stops',
         'stopid text, stopcode text, stopname text, latitude real, longitude real',
         stops)

    print('complete')

agencyList = []
print "Parsing routes for agencies: "
for arg in sys.argv[1:]:
    print arg
    agencyList.append(arg)

for agency in agencyList:
    parseRoutesDb(agency)

