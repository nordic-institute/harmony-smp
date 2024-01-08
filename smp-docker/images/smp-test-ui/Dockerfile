# create builder to install maven which is needed
FROM eclipse-temurin:11-jdk as builder

ARG MAVEN_VERSION=3.9.6
ARG USER_HOME_DIR="/root"
ARG SHA=706f01b20dec0305a822ab614d51f32b07ee11d0218175e55450242e49d2156386483b506b3a4e8a03ac8611bae96395fd5eec15f50d3013d5deed6d1ee18224
ARG BASE_URL=https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

RUN apt-get update \
  && apt-get install -y ca-certificates curl git gnupg dirmngr --no-install-recommends \
  && rm -rf /var/lib/apt/lists/*
RUN set -eux; curl -fsSLO --compressed ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA} *apache-maven-${MAVEN_VERSION}-bin.tar.gz" | sha512sum -c - \
  && curl -fsSLO --compressed ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz.asc \
  && export GNUPGHOME="$(mktemp -d)"; \
  for key in \
  6A814B1F869C2BBEAB7CB7271A2A1C94BDE89688 \
  29BEA2A645F2D6CED7FB12E02B172E3E156466E8 \
  ; do \
  gpg --batch --keyserver hkps://keyserver.ubuntu.com --recv-keys "$key" ; \
  done; \
  gpg --batch --verify apache-maven-${MAVEN_VERSION}-bin.tar.gz.asc apache-maven-${MAVEN_VERSION}-bin.tar.gz
RUN mkdir -p ${MAVEN_HOME} ${MAVEN_HOME}/ref \
  && tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C ${MAVEN_HOME} --strip-components=1 \
  && ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

FROM selenium/standalone-firefox:120.0

ENV MAVEN_HOME /usr/share/maven
COPY --from=builder ${MAVEN_HOME} ${MAVEN_HOME}
COPY ./artefacts/domiSMP-ui-tests /home/${SEL_USER}/domiSMP-ui-tests
COPY ./entrypoint.sh /sbin/entrypoint.sh
USER root
RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn \
    && chmod 755 /sbin/entrypoint.sh \
    && mkdir /results \
    && chown -R ${SEL_USER}:${SEL_USER} /results \
    && chown -R ${SEL_USER}:${SEL_USER} /home/${SEL_USER}/domiSMP-ui-tests
# Switch back to selenium user
USER ${SEL_USER}
RUN cd /home/${SEL_USER}/domiSMP-ui-tests  \
    && mvn clean verify -DskipITs -DskipTests
ENV MAVEN_CONFIG "/home/${SEL_USER}/.m2"

ENTRYPOINT ["/sbin/entrypoint.sh"]
