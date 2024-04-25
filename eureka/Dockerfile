FROM openjdk:17-slim as builder

WORKDIR application

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} ./

RUN java -Djarmode=layertools -jar eureka-0.0.1-SNAPSHOT.jar extract

FROM openjdk:17-slim

WORKDIR application

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

#Set profile to 'prod'
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8761

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE", "org.springframework.boot.loader.launch.JarLauncher"]