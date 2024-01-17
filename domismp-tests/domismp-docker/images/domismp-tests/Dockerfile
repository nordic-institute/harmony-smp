# create builder to install maven which is needed
FROM maven:3.9.6-eclipse-temurin-11

ARG TEST_USER=edeltest
ARG TEST_GROUP=${TEST_USER}
ARG UID=1700
ARG GID=1701
# Build artefacts
ARG FIREFOX_VERSION=121.0.1
ARG FIREFOX_SHA512=aedc6380e70fda8a9087e54089e8cd5d3b365336af157a7d58c2eaaf317395d0550f346a4e692ca244b2891803382a8f3a382c5054069728963c617a53a4a2b1
ARG FIREFOX_DOWNLOAD_URL="https://download-installer.cdn.mozilla.net/pub/firefox/releases/$FIREFOX_VERSION/linux-x86_64/eu/firefox-$FIREFOX_VERSION.tar.bz2"
ARG GECKODRIVER_VERSION=0.34.0
ARG GECKODRIVER_SHA512=98c4ace1c22698c2babec55269a82c98888c1a5b35a2f15aa1f1f077f1d92f80c46deaddd584aaf649aa654c4a69f6a320e3f1ce40e6918dc71dd6102446c125
ARG GECKODRIVER_DOWNLOAD_URL="https://github.com/mozilla/geckodriver/releases/download/v$GECKODRIVER_VERSION/geckodriver-v$GECKODRIVER_VERSION-linux64.tar.gz"

#===================
# Timezone settings set to UTC by default!
#===================
ENV TZ "UTC"


#======================================
# Configure environement
#======================================
ENV TEST_USER=${TEST_USER} \
    TEST_WEBDRIVER_PATH=/usr/bin/geckodriver \
    TEST_WEBDRIVER_TYPE=firefox \
    TEST_WEBDRIVER_HEADLESS=true \
    DATA=/data \
    TEST_URL=http://localhost:8080/smp/

ENV TEST_UI=${DATA}/test-ui \
    TEST_API=${DATA}/test-api \
    RESULT_FOLDER=${DATA}/results \
    TEST_PLAN=ui

USER root
# create test user, folders and configure timezone
RUN groupadd ${TEST_GROUP} \
         --gid ${GID} \
  && useradd ${TEST_USER} \
         --create-home \
         --gid ${GID} \
         --shell /bin/bash \
         --uid ${UID} \
  && mkdir -p "${DATA}" \
  && ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime \
  && dpkg-reconfigure -f noninteractive tzdata \
  && cat /etc/timezone


# install firefox dependencies libgtk-3-0 libasound2 x11-common libx11-xcb1
# install oher usefull tools bzip2 ca-certificates tzdata unzip wget
RUN apt-get update -qqy \
  && apt-get -qqy --no-install-recommends install  \
    libgtk-3-0 \
    libasound2 \
    x11-common \
    libx11-xcb1 \
    bzip2 \
    ca-certificates \
    tzdata \
    unzip \
    wget \
  && rm -rf /var/lib/apt/lists/* /var/cache/apt/* \
  && echo "Download firefox version: $FIREFOX_DOWNLOAD_URL"  \
  && wget --no-verbose -O /tmp/firefox.tar.bz2 $FIREFOX_DOWNLOAD_URL \
  && echo $(sha512sum /tmp/firefox.tar.bz2) \
  && sha512sum /tmp/firefox.tar.bz2 | grep $FIREFOX_SHA512 \
  && wget --no-verbose -O /tmp/geckodriver.tar.gz $GECKODRIVER_DOWNLOAD_URL \
  && sha512sum /tmp/geckodriver.tar.gz | grep $GECKODRIVER_SHA512 \
  # install firefox
  && echo "Install firefox version: $FIREFOX_VERSION" \
  && tar -C /opt -xjf /tmp/firefox.tar.bz2 \
  && rm /tmp/firefox.tar.bz2 \
  && mv /opt/firefox /opt/firefox-$FIREFOX_VERSION \
  && rm -rf  /usr/bin/firefox \
  && ln -fs /opt/firefox-$FIREFOX_VERSION/firefox /usr/bin/firefox \
  # install geckodriver
  && echo "Install GeckoDriver version: $GECKODRIVER_VERSION" \
  && tar -C /opt -zxf /tmp/geckodriver.tar.gz \
  && rm /tmp/geckodriver.tar.gz \
  && mv /opt/geckodriver /opt/geckodriver-$GECKODRIVER_VERSION \
  && chmod 755 /opt/geckodriver-$GECKODRIVER_VERSION \
  && ln -fs /opt/geckodriver-$GECKODRIVER_VERSION ${TEST_WEBDRIVER_PATH}


COPY ./artefacts/test-ui ${TEST_UI}
COPY ./artefacts/test-api ${TEST_API}
COPY ./entrypoint.sh /sbin/entrypoint.sh

RUN chmod 755 /sbin/entrypoint.sh \
    && mkdir -p /${DATA}/results \
    && chown -R ${TEST_USER}:${TEST_USER} /data

USER ${TEST_USER}
# dry run mvn and download all dependencies
RUN cd ${TEST_UI}  \
    && mvn clean verify -DskipITs -DskipTests \
    && cd ${TEST_API}  \
    && mvn dependency:resolve -P run-soapui -DskipITs -DskipTests \
    && mvn clean verify -DskipITs -DskipTests

ENTRYPOINT ["/sbin/entrypoint.sh"]
