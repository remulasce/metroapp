echo running Nextrip scraper...

python scraper_nextrip.py lametro lametro-rail actransit sf-muni

echo running Bart scraper...

python scraper_BART.py

echo running Bay 511 scraper...

python scraper_bayarea_511.py Caltrain SamTrans VTA

echo Finished scraping all supported agencies.

pause