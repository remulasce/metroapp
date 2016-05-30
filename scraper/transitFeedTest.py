import transitfeed
from gtfsParseUtils import *
from collections import defaultdict

agency = "vta"

"""
Calculate and write the stop-location table.
This will be used to display the map and calculate the nearest stops
to the user.

Table 'stops' will contain columns
[stopid, lat, lon]
"""
def extractStopsTable(schedule):
	stopList = schedule.GetStopList()

	# [stopid, lat, lon]
	stops = []

	for stop in stopList:
		stops.append( (stop.stop_id, stop.stop_lat, stop.stop_lon) )

	writeToDb(
	        'routelines/'+agency+'-routelines.db',
	         'stops',
	         'stopid text, latitude real, longitude real',
	         stops)

"""
Calculate and write the stop-shapes table.
Given a stop determined from the stop-location table, this will be used to
determine what shapes route through the stop.

Note that a 'route' is not a real thing in GTFS. It's a wrapper used to present
many individual trips to the rider as a single service, even though they may go
different places.

This means that each shape is one way only. Therefore we can index how far into
the shape each stop is, which will be helpful later.

Table 'stopshapes' will contain columns
[stopid, shapeid, distanceintoshapeft]
(we assume feet, though this could change per agency)
"""
def extractStopShapesTable(schedule):
	stopList = schedule.GetStopList()

	# [stopid, shapeid, distanceintoshapeft]
	stopshapes = []

	# We'll use this set-default-dict to prevent the (many) duplicate mappings.
	# Maps stop to a list of shapes.
	stopshapesdict = defaultdict(set)

	for stop in stopList:
		for trip in stop.GetTrips():
			for stoptime in trip.GetStopTimes():
				stopshapesdict[stoptime.stop_id].add(
					# Add the stopid to the value to, for convenience later.
					(stoptime.stop_id, trip.shape_id, stoptime.shape_dist_traveled) )

	for keysets in stopshapesdict.values():
		for value in keysets:
			stopshapes.append(value)


	writeToDb(
	        'routelines/'+agency+'-routelines.db',
	         'stopshapes',
	         'stopid text, shapeid text, distanceintoshapeft real',
	         stopshapes)

"""
Calculate and writethe shope-points table.
Given a shape and distance determiend by the stop-shapes table, this will be
used to get the points that actually represent the shape, and display it.

Table 'shapepoints' will contain columns
[shapeid, distanceintoshapeft, lat, lon]
"""
def extractShapePointsTable(schedule):
	shapeList = schedule.GetShapeList()

	# [shapeid, distanceintoshapeft, lat, lon]
	shapepoints = []

	for shape in shapeList:
		# shapepoint comes in [lat, lng, shape_distance_traveled]
		for shapepoint in shape.points:
			shapepoints.append( (
					shape.shape_id,
					shapepoint[2],
					shapepoint[0],
					shapepoint[1]) )
	writeToDb(
	        'routelines/'+agency+'-routelines.db',
	         'shapepoints',
	         'shapeid text, distanceintoshapeft real, latitude real, longitude real',
	         shapepoints)

loader = transitfeed.Loader("gtfs_dirs/gtfs_vta")
schedule = loader.Load()

extractStopsTable(schedule)
extractStopShapesTable(schedule)
extractShapePointsTable(schedule)
