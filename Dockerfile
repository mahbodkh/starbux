FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine as builder
WORKDIR /Users/starbux
COPY pom.xml .
COPY src ./src

RUN mvn clean install spring-boot:repackage -DskipTests

FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine
COPY --from=builder /Users/starbux/target/starbux-0.0.1-SNAPSHOT.jar /starbux.jar
CMD ["java","-Dserver.port=8080", "-jar","/starbux.jar"]
EXPOSE 8080