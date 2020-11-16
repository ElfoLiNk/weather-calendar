FROM amazon/aws-eb-glassfish:5.0-al

EXPOSE      8080 4848 8181

COPY ./MeteoCal-ear/target/MeteoCal-ear-1.2-SNAPSHOT.ear $GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/
