rm -rf **/node_modules
rm -rf **/gen
rm -rf **/mta_archives

mbt build && cf deploy mta_archives/bs-recap-ws_1.0.0.mtar

cf l --origin avpxtt84j-platform -a https://api.cf.eu12.hana.ondemand.com
mbt build
cf deploy mta_archives/bs-recap-ws_1.0.0.mtar



TODOs:
- setup ias for auth in cloud logging, such that basic creds from binding don't need to be used?

## Overview

![alt text](<assets/overview.png>)

## OpenTelemetry

Go to the Cloud Logging dashboard. You will find the link as well as user and password information in the credentials of the cloud logging instance. (In SAP BTP Cockpit, go to your deployed app "XXX-bs-bupa-srv-otel", click on Service Bindings, select the Cloud Logging instance, and press Show sensitive data.)

Open the launchpad (In SAP BTP Cockpit, go to your deployed app "XXX-bs-app-otel", and open the application route in a new tab.), go to app Manage Orders, and press Create. Enter an order number, select a shipping address (The value help for the shipping address is populated by fetching data from the CAP Node.js app "bupa".), and press Create.

Go to Cloud Logging and navigate to the list of traces. (Open the main menu. In section OpenSearch Plugins, click on Observability. There, click on Trace analytics &rarr; Traces.) In order to narrow down which is the distributed trace, add a filter for `serviceName: bupa` (see screenshot below).

![alt text](<assets/serviceName.png>)

Click on trace XXX. Have a look at the distributed traces for your app. Does anything look strange?
