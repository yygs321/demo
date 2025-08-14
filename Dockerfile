# 베이스 이미지로 OpenJDK 17 JDK 슬림 버전 사용
FROM openjdk:17-jdk-slim

# 타임존 설정 (옵션)
ENV TZ=Asia/Seoul

# 컨테이너 작업 디렉토리 지정
WORKDIR /usr/src/app

# CI/CD 빌드 결과물인 JAR 파일을 도커 컨테이너에 복사 -> 도커 이미지에 넣음
COPY ./demo-boot/build/libs/demo-boot-0.0.1-SNAPSHOT.jar ./app.jar

# 컨테이너가 외부와 통신할 포트 지정
EXPOSE 8080

# 컨테이너 시작 시 JAR 파일 실행
CMD ["java", "-jar", "app.jar"]