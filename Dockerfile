FROM maven:3.6.3-jdk-11
COPY target/*.jar /tmp                                                                                                         
RUN cd /tmp ; find -type f -iname "*.jar" -exec mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile={} \;
