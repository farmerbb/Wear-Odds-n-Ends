## Wear Odds & Ends

Just a few quick odds & ends for Wear OS that I wrote to improve the user experience:

* Complication that displays seconds of the minute, along with the day of week
* Empty complication with null data (prevent accidental taps on blank areas of the watch face)
* Service that will automatically detect screen off events and go back to the watch face
* Option to disable long-pressing button to activate Google Assistant

Please note that the seconds complication and screen off detection service may impact your watch's performance.

This code is partially based off of the [WearComplicationProvidersTestSuite](https://github.com/googlesamples/android-WearComplicationProvidersTestSuite) sample.
