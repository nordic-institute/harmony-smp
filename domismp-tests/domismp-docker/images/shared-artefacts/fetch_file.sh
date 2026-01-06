#!/bin/sh

# fetch_file src_file url dest_file
# If src_file exists, copy it to dest_file
# Otherwise, download from url to dest_file

echo "Fetching file from '$1' or '$2' to '$3'";
src="$1"; url="$2"; dest="$3";
if [ -f "/tmp/artefacts/$src" ]; then
  cp "/tmp/artefacts/$src" "$dest";
else
  echo "File '/tmp/artefacts/$src' not found. Downloading from '$url'...";
  curl -fSL -o "$dest" "$url";
fi;
