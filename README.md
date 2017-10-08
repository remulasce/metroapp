# Los Angeles Metro Companion App
Realtime arrival notification utility for LA Metro on Android
Also works in Bay Area, for obvious reasons.

![Promo Banner](/promotional_images/promo_banner.png?raw=true "Promo Banner")

Metro Companion App provides realtime arrival information and notification for the Los Angeles Metro system.
Users input the names of Metro bus and rail stops, and the App will display arrival data to those stops.

In addition, each arrival to a stop can be tracked with a notification in the phone's drawer, and a sound will be played when the vehicle is almost arrived.

![Demo Sequence](/promotional_images/sequence_banner.png?raw=true "Sequence Banner")

Metro Companion App has no map view and uses intelligent autocomplete to suggest what stops to track.
It has one main screen, and one "About" pane. All arrival predictions are listed directly on that one main screen.
This makes the app extremely quick to open and check, unlike many other bloated apps.
Combined with the arrival notification feature, Metro Companion aims to put as little between the user and the information they want as possible.

## Developers Guide

lametroapp/ has the Android source.
scraper/ has the offline scraping utility, which generates the stopname databases.

Due to bad history, there's some random directories on the root level, including an iOS port and older Android project.