FROM openjdk:17
EXPOSE 8080
ADD build/libs/app.jar app.jar
ENV SPRING_PROFILES_ACTIVE=local
ENTRYPOINT ["java","-jar","/app.jar"]