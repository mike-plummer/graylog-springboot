# graylog-stockmarket
Example of using GrayLog with Spring Boot

## Infrastructure
GrayLog uses MongoDB and ElasticSearch behind the scenes. To make this example as simple as possible I've configured the SpringBoot application to fire up embedded versions of both of these. Obviously you shouldn't use this code in a Production environment.

## URLs
Graylog: http://localhost:9000
Graylog Rest API: http://localhost:12900/api-browser

## License
This code is provided under the terms of the MIT license: basically you're free to do whatever you want with it, but no guarantees are made to its validity, stability, or safety. All works referenced by or utilized by this project are the property of their respective copyright holders and retain licensing that may be more restrictive.