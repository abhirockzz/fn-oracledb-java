FROM fnproject/fn-java-fdk-build:jdk9-1.0.63
#temporary workaround to copy the oracle driver JAR
COPY ojdbc7.jar .
#deploy it to /function/repo. This location is referenced in <repository> in the pom.xml
RUN mvn deploy:deploy-file -Durl=file:///function/repo -Dfile=ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.1 -Dpackaging=jar