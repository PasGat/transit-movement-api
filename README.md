transit-movement-api 
=============================================
This service exposes endpoints via the API platform to allow the submission of transit movement details that are subsequently processed by the Office of Transit backend services, so that the UK can be part of the Common Transit Convention.
The main consumers of this API are the third party carriers that are able to provide this data.

API
---
See `/resources/public/api/conf/1.0/application.raml` for details of the API endpoints that are exposed via the API platform.

Please note it is mandatory to supply the Accept header ```application/vnd.hmrc.1.0+json``` with all requests.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
