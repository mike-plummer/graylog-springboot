# graylog-stockmarket
Example of using GrayLog with Spring Boot

## Infrastructure
GrayLog uses MongoDB and ElasticSearch behind the scenes. To make this example as simple as possible I've configured the SpringBoot application to fire up embedded versions of both of these. Obviously you shouldn't use this code in a Production environment.

## URLs
Graylog: http://localhost:9000
Graylog Rest API: http://localhost:12900/api-browser

## Instructions
1. Make sure you have Java8+ and Gradle 2.12+ installed
2. Run `gradle spring-boot:bootRun` from project root
3. The first time you run this a LOT of stuff will download
    - Gradle dependencies
    - MongoDB binaries to launch an embedded Mongo instance
4. You'll see a lot of logging - this is the Spring application initializing itself and booting up an ElasticSearch cluster, MongoDB database, and kicking off the Graylog binary
5. After about 30 seconds (depending on your machine's horsepower) you should start seeing logging like `Generated quote - VKL@59.0`
6. Open `http://localhost:9000` to open the Graylog GUI
7. Explore! The application sets up some default GUI elements - check out the 'Stock Market' dashboard.
8. When you're done just end the Gradle process

## Cleanup
### Processes
I've tried to put in shutdown hooks to kill Graylog when the app stops, but sometimes it doesn't get everything. To be sure run `ps -ef | grep graylog` and run `kill` on any processes still running (other than your grep, of course)
### MongoDB
- To delete persisted Mongo data you can delete the 'data/mongodb/data' directory - the next time you launch the application a fresh database will get set up
- Mongo executables get downloaded into the project directory - they can be deleted by removing the 'data/mongodb' directory. This will delete the persisted data as well. If you do this and try to re-launch the application it will re-download the executables.

## License
This code is provided under the terms of the MIT license: basically you're free to do whatever you want with it, but no guarantees are made to its validity, stability, or safety. All works referenced by or utilized by this project are the property of their respective copyright holders and retain licensing that may be more restrictive.