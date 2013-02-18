#!/bin/bash

if [ -z "$1" -o -z "$2" ]; then
  echo
  echo "Usage:"
  echo "  $0: archivefile privatekey"
  echo
  exit
fi

if [ -z "$(which openssl)" ]; then
  echo "You need 'openssl' installed to use this script"
  exit
fi

openssl dgst -sha1 -binary < "$1" | openssl dgst -dss1 -sign "$2" | openssl enc -base64

