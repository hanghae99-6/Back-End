FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
#CMD (java \
#          -Dcom.sun.management.jmxremote \
#          -Dcom.sun.management.jmxremote.port=9090 \
#          -Dcom.sun.management.jmxremote.authenticate=false \
#          -Dcom.sun.management.jmxremote.ssl=false \
#          -Dcom.sun.management.jmxremote.rmi.port=9090 \
#          -Djava.rmi.server.hostname=localhost )

 #FROM openjdk:8-jdk-alpine
 #open jdk java8 버전의 환경을 구성합니다.
 #Amazon의 corretto 버전을 사용할 경우, 주석처리 한 부분으로 사용하면 됩니다.

 #ARG JAR_FILE=build/libs/*.jar
 #build가 되는 시점에 JAR_FILE 이라는 변수명에 build/libs/*.jar 표현식을 선언했다는 의미입니다.
 #build/libs 경로는 gradle로 빌드했을 때 jar 파일이 생성되는 경로입니다.
 #Maven의 경우 target/*.jar 로 설정해주시면 됩니다.

 #COPY ${JAR_FILE} app.jar
 #위에 선언한 JAR_FILE 을 app.jar 로 복사합니다.

 #ENTRYPOINT ["java","-jar","/app.jar"]
 #jar 파일을 실행하는 명령어(java -jar jar파일) 입니다.

 #ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]
 #운영 및 개발에서 사용되는 환경 설정을 분리해서 사용할 경우, 위와 같이 ENTRYPOINT를 설정할 수 있습니다.