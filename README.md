# graylog-springboot
Example of using Graylog with Spring Boot

Check out my [blog post](https://objectpartners.com/2016/05/04/graylog-with-spring-boot-open-source-log-and-event-analysis/) describing the goals and process (and compromises) behind this code.

## Infrastructure
Graylog uses MongoDB and ElasticSearch behind the scenes. To make this example as simple as possible I've configured the SpringBoot application to fire up embedded versions of both of these. I highly recommend not using this setup in a Production environment.

## URLs
Graylog: http://localhost:9000

Graylog Rest API: http://localhost:12900/api-browser

App Rest API: http://localhost:8080/swagger-ui.html

## Instructions
1. Make sure you have Java and Maven installed
2. Run `mvn spring-boot:run` from project root
3. The first time you run this a LOT of stuff will download. This is a one-time occurrence.
    - Maven dependencies
    - MongoDB binaries to launch an embedded Mongo instance
4. You'll see a lot of logging - this is the Spring application initializing itself and booting up an ElasticSearch cluster, MongoDB database, and kicking off the Graylog instance
5. After about 30 seconds (depending on your machine's horsepower) you should start seeing logging like `Generated quote - VKL@59.0`
6. Open `http://localhost:9000` to open the Graylog GUI, login with username/password `admin/admin`
7. Explore! The application sets up some default GUI elements - start with the 'Stock Market' dashboard from the top menu.
8. When you're done just end the Maven process with `Ctrl-C`

## Cleanup
### MongoDB
- To delete persisted Mongo data you can delete the 'data/mongodb/data' directory - the next time you launch the application a fresh database will get set up
- Mongo executables get downloaded into the project directory - they can be deleted by removing the 'data/mongodb' directory. This will delete the persisted data as well. If you do this and try to re-launch the application it will re-download the executables.

### ElasticSearch
If you delete the MongoDB data you'll need to fix the ElasticSearch indices or else they'll be out of sync. This can be done from the Graylog GUI or you can just delete `data/graylog/nodes` to rebuild them on next launch.

## Configuration
If you're interested in tweaking the application's config you can look in three main places:

1. The code! Lots of setup and defaults are being set in the code since this is just an example and not really intended for extension/reuse.
2. application.yml: Spring Boot configs are set here (including ElasticSearch which is launched by Spring Boot)
3. graylog.conf: Config file for Graylog - lots of values in here you can mess with. Take a look at the Graylog docs for info on what they all do.

## Versions
You'll need versions of Java and Maven available. I've tested with the specified versions.

Java: 1.8.0_74

Maven: 3.3.9

Application has been verified on OSX 10.11.4 and Ubuntu 15.10. Your mileage may vary on other operating systems.

## License
This code is provided under the terms of the MIT license: basically you're free to do whatever you want with it, but no guarantees are made to its validity, stability, or safety. All works referenced by or utilized by this project are the property of their respective copyright holders and retain licensing that may be more restrictive.
