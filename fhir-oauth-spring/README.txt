This is currently a work in progress

It is based on camel 2.16 examples of spring security. Currently returns FHIR Patient and Conditions from a mongo db database 
Security is Http basic and is in the progress of being converted to oauth.

Idea is to use:

AngularJS for client
Spring Security for locking down resources (OAuth2)
Apache Camel for the routing and REST interface
HL7 FHIR as the resource API (both json and xml)
MongoDB for backend (this is a quick win to get the system running quickly)


