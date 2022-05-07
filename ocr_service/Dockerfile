#Stage 1
# initialize build and set base image for first stage
FROM maven:3.6.3-adoptopenjdk-11 as build
# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
# set working directory
WORKDIR /opt/app
# copy just pom.xml
COPY pom.xml .
# go-offline using the pom.xml
RUN mvn dependency:go-offline
# copy your other files
COPY ./src ./src
# compile the source code and package it in a jar file
RUN mvn  clean install -Dmaven.test.skip=true
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../../target/*.jar)

#Stage 2
# set base image for second stage
FROM adoptopenjdk/openjdk11:jre-11.0.9_11-alpine
RUN apk add tesseract-ocr
# set deployment directory
WORKDIR /opt/app
ARG DEPENDENCY=/opt/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /opt/app/lib
COPY --from=build ${DEPENDENCY}/META-INF /opt/app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /opt/app
ENTRYPOINT ["java","-cp","/opt/app:/opt/app/lib/*","com.capps.ocr.OCRApplication"]
