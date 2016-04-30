# graylog-springboot
Example of using GrayLog with Spring Boot

## Infrastructure
GrayLog uses MongoDB and ElasticSearch behind the scenes. To make this example as simple as possible I've configured the SpringBoot application to fire up embedded versions of both of these. I highly recommend not using this setup in a Production environment.

## URLs
Graylog: http://localhost:9000
Graylog Rest API: http://localhost:12900/api-browser
App Rest API: http://localhost:8080/swagger-ui.html

## Instructions
1. Make sure you have Java and Maven installed
2. Run `mvn spring-boot:run` from project root
3. The first time you run this a LOT of stuff will download
    - Maven dependencies
    - MongoDB binaries to launch an embedded Mongo instance
4. You'll see a lot of logging - this is the Spring application initializing itself and booting up an ElasticSearch cluster, MongoDB database, and kicking off the Graylog instance
5. After about 30 seconds (depending on your machine's horsepower) you should start seeing logging like `Generated quote - VKL@59.0`
6. Open `http://localhost:9000` to open the Graylog GUI
7. Explore! The application sets up some default GUI elements - check out the 'Stock Market' dashboard.
8. When you're done just end the Maven process

## Cleanup
### MongoDB
- To delete persisted Mongo data you can delete the 'data/mongodb/data' directory - the next time you launch the application a fresh database will get set up
- Mongo executables get downloaded into the project directory - they can be deleted by removing the 'data/mongodb' directory. This will delete the persisted data as well. If you do this and try to re-launch the application it will re-download the executables.

## Versions
Java: 1.8.0_74
Maven: 3.3.9

## License
This code is provided under the terms of the MIT license: basically you're free to do whatever you want with it, but no guarantees are made to its validity, stability, or safety. All works referenced by or utilized by this project are the property of their respective copyright holders and retain licensing that may be more restrictive.