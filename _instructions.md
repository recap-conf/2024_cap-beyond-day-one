TODOs:
- make app etc. names unique before deploying? or at least their routes (or make random)?
- overall architecture diagram?
- setup ias for auth in cloud logging, such that basic creds from binding don't need to be used?
- otel creds configured?

## OpenTelemetry

Go to the Cloud Logging dashboard. You will find the link as well as user and password information in the credentials of the cloud logging instance. (In SAP BTP Cockpit, go to your deployed app, click on Service Bindings, select the Cloud Logging instance, and press Show sensitive data.)

Go to UI XXX and create a XXX. In the process, make sure to open the value help for business partner addresses that are served by the CAP Node.js app.

Go to Cloud Logging and navigate to the list of traces. (Open the main menu. In section OpenSearch Plugins, click on Observability. There, click on Trace analytics &rarr; Traces.) Click on trace XXX. Have a look at the distributed traces for your app. Does anything look strange?
