# LA Metro Companion Scraper

This directory contains the offline scraping tools to generate stop files for the LA Metro Companion App.

## Notes:
Bay Area 511 no longer offers the API. This probably means most of the dbs in here are broken.
Recently migrated to py3. Good luck.
Don't run Nextrip without any agency params, rate-limiting kicks in when scraping all agencies.


## Basic usage:

Just run scrapeAllSupportedAgencies.bat

Output .db files go to _out.
They will need to be moved to lametroapp/app/src/main/assets/databases to get them into the Android app.

## Advanced Usage:

There are three scrapers, each for a different online prediction service:

NexTrip
Bart
Bay Area 511

That order is the order of preference for which prediction service to use if agencies have more than one prediction service.

NexTrip and Bart are fire-and-forget scrapers you can optionally specify which agency names to scrape.

Bay Area 511 doesn't include stop location information. For each agency you must provide the corresponding gtfs stop files in a child directory.
