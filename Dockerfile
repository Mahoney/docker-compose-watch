# syntax=docker/dockerfile:1.3.1-labs

ARG username=worker
ARG work_dir=/home/$username/work

FROM eclipse-temurin:17.0.1_12-jdk-focal as worker
ARG username
ARG work_dir
ARG gid=1000
ARG uid=1001

RUN apt-get update && \
    apt-get install -y libncurses5

RUN addgroup --system $username --gid $gid && \
    adduser --system $username --ingroup $username --uid $uid

USER $username
RUN mkdir -p $work_dir
WORKDIR $work_dir
# The single use daemon will be unavoidable in future so don't waste time trying to prevent it
ENV GRADLE_OPTS='-Dorg.gradle.daemon=false'

# Download gradle in a separate step to benefit from layer caching
COPY --chown=$username gradle/wrapper gradle/wrapper
COPY --chown=$username gradlew gradlew
RUN ./gradlew --version

COPY --chown=$username . .
RUN --mount=type=cache,target=/home/$username/.gradle/caches,gid=$gid,uid=$uid \
    --mount=type=cache,target=/home/$username/.konan,gid=$gid,uid=$uid \
    ./gradlew --no-watch-fs --stacktrace build
RUN ls -lAh $work_dir/build/bin/native/releaseExecutable

FROM scratch
ARG work_dir

COPY --from=worker $work_dir/build/bin/native/releaseExecutable/docker-compose-watch.kexe /docker-compose
