LA Metro Companion Readme
This directory contains the offline scraping tools to generate stop files for the LA Metro Companion App.

There are three scrapers, each for a different online prediction service:

NexTrip
Bart
Bay Area 511

That order is the order of preference for which prediction service to use if agencies have more than one prediction service.

NexTrip and Bart are fire-and-forget scrapers you can optionally specify which agency names to scrape.

Bay Area 511 doesn't include stop location information. For each agency you must provide the corresponding gtfs stop files in a child directory.
