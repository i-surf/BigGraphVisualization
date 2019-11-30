#!/bin/bash

for num in index*;
do

	echo "${num}"

	for f in .\/${num}\/*.txt;
	do
		echo "${f}"
        (( cnt = "${cnt}" + 1 ))
        curl -XPOST "localhost:9200/${num}/page/${cnt}?pretty" -H 'Content-Type: application/json' -d @$f;
	done

done