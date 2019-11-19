#!/bin/bash

for f in *.txt;
do

        (( cnt = "${cnt}" + 1 ))
        echo "${f}" 
        curl -XPOST "localhost:9200/${f}/type?pretty" -H 'Content-Type: application/json' -d @$f;

done
 