# flavorpush
An application for automatically deploying Android applications with multiple product flavors to multiple developer accounts.

## Prerequisites

- A Google Play Developer Account
- A Google Play Publisher API service account and corresponding key file.  You can find instructions for this process [here.](https://developers.google.com/android-publisher/getting_started)
- Flavorpush configuration file (a sample config.json can be found in the root directory)

## Usage

Import the Flavorpush project into your IDE of choice and create a configuration to run it as an application.  You can
also build a jar file and run the application through the Java console.

The application will prompt you for information on which track to deploy to, as well as release notes for the app.

## Attributions

Flavorpush uses code adapted from the Google Play Publisher API samples, found [here.](https://github.com/googlesamples/android-play-publisher-api)
