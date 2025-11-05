#!/bin/bash
: "${1:?Missing groupId:artifactId:version!}"
: "${2:?Missing output path!}"

# Parse the string into variables
echo "Parsing input: $1"
echo "save to folder: $2"

IFS=':' read -r GROUP_ID ARTIFACT_ID VERSION <<< "$1"
# replace dots with slashes in GROUP_ID
GROUP_ID=$(echo "$GROUP_ID" | sed 's/\./\//g')

REPO_URL="https://ec.europa.eu/digital-building-blocks/artifact/repository/eDelivery-snapshots"
METADATA_URL="$REPO_URL/$GROUP_ID/$ARTIFACT_ID/$VERSION/maven-metadata.xml"

# Download maven-metadata.xml
wget -q -O maven-metadata.xml "$METADATA_URL"
# Parse the latest snapshot version
LATEST=$(grep -oPm1 "(?<=<value>)[^<]+" maven-metadata.xml)

# Construct the URL for the latest snapshot artifact
ARTIFACT_URL="$REPO_URL/$GROUP_ID/$ARTIFACT_ID/$VERSION/$ARTIFACT_ID-$LATEST-only-spring-vault.jar"
echo "Downloading artifact from: $ARTIFACT_URL"
# Download the latest snapshot artifact
wget --tries=1 --retry-connrefused -O ${2}/edelivery-vault.jar "$ARTIFACT_URL"

# Clean up
rm maven-metadata.xml
