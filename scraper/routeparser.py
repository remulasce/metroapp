# Parses static GTFS files into a format suitable for map route overlay
import sys


def cleanFormatString(format):
    return [s.strip('"') for s in format.split(",")]

def formatIndex(format, name):
    if (not name in format):
        return -1
    return format.index(name)

def getItem(split, index):
    if (index == -1):
        return -1
    return split[index]

# Return
# [stopid, stopcode, stopname, stoplat, stoplon]
# from
# "stop_id","stop_code","stop_name","stop_desc","stop_lat","stop_lon","zone_id"
def getStops(stopFilePath):
    print "Getting stops table "+stopFilePath
    stops = []

    with open(stopFilePath) as stops_gtfs:
        format = cleanFormatString(stops_gtfs.readline())

        stop_id_index = formatIndex(format, "stop_id")
        stop_code_index = formatIndex(format, "stop_code")
        stop_name_index = formatIndex(format, "stop_name")
        lat_index = formatIndex(format, "stop_lat")
        lon_index = formatIndex(format, "stop_lon")

        for line in stops_gtfs:
            split = [s.strip('"') for s in line.split(",")]

            stop = ( getItem(split, stop_id_index),
                getItem(split, stop_code_index),
                getItem(split, stop_name_index),
                getItem(split, lat_index),
                getItem(split, lon_index) )

            print "Adding stop: "+str(stop)
            stops.append(stop)
    return stops
        

def getDir(agencyName):
    return "gtfs_dirs/gtfs_"+agencyName


def parseRoutesDb(agency):
    print "Now parsing: "+agency

    gtfsDir = getDir(agency)
    stops = getStops(gtfsDir+"/stops.txt")





agencyList = []
print "Parsing routes for agencies: "
for arg in sys.argv[1:]:
    print arg
    agencyList.append(arg)

for agency in agencyList:
    parseRoutesDb(agency)

