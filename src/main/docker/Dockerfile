FROM azul/zulu-openjdk:11

ADD maven/${project.artifactId}-${project.version}.jar ${project.artifactId}.jar

ENV JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS \
    -jar /${project.artifactId}.jar
