FROM maven:3.6.2-jdk-8

ENV PROJECT_DIR=/opt/project

RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR

ADD pom.xml $PROJECT_DIR
RUN mvn dependency:resolve

ADD ./src/ $PROJECT_DIR/src
RUN mvn package -Dmaven.test.skip=true

FROM openjdk:8-jdk
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY --from=0 $PROJECT_DIR/target/library* $PROJECT_DIR/

RUN ln -sf /dev/stdout /$PROJECT_DIR/library.log

CMD ["java","-jar","/opt/project/library-0.0.1-SNAPSHOT.jar"]