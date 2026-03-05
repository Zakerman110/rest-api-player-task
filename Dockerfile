FROM gradle:8.7-jdk17 AS deps
WORKDIR /app

COPY build.gradle settings.gradle gradle.properties* /app/
COPY gradle /app/gradle

RUN gradle --no-daemon dependencies || true


FROM gradle:8.7-jdk17 AS test
WORKDIR /app

COPY . /app

RUN chown -R gradle:gradle /app
USER gradle

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

CMD ["gradle", "--no-daemon", "test", "allureReport"]