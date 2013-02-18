#!/bin/bash

if [ -z "$(which openssl)" ]; then
	echo "You need openssl installed to generate your DSA keys."
	exit 1
fi

DSTDIR="."

if [ -n "$1" ]; then
	DSTDIR="$1"
fi

OLDDIR=$PWD

cd $DSTDIR

openssl dsaparam -out dsaparam.pem 2048
openssl gendsa -des3 -out dsa_priv.pem dsaparam.pem
openssl dsa -in dsa_priv.pem -pubout -out dsa_pub.pem
rm -f dsaparam.pem

cd $OLDDIR
