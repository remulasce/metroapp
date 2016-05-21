from gtfsParseUtils import *

def parseGtfsDictionary(filename):
	print "Extracting dictionry from "+filename

	ret = []

	with open(filename) as gtfs_file:
		format = cleanFormatString(gtfs_file.readline())
		print format

		for line in gtfs_file:
			split = [s.strip('"').strip('\n') for s in line.split(",")]
			dictionary = dict()

			for item in split:
				if item:
					dictionary[
						format[split.index(item)]] = item

			print str(dictionary)+"\n"
			ret.append(dictionary)
	return ret

parseGtfsDictionary("gtfs_dirs/gtfs_vta/stop_times.txt")