
# cp ../target/UserRegistration-1.0.0.jar UserRegistration-1.0.0.jar
# docker build -t userregistration .

FROM java:8
VOLUME /tmp/userregistration
ADD UserRegistration-1.0.0.jar UserRegistration-1.0.0.jar
RUN bash -c 'touch /UserRegistration-1.0.0.war'
ENTRYPOINT ["java","-jar","/UserRegistration-1.0.0.jar"]
