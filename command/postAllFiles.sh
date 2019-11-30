#!/bin/bash

for f in *.txt;
do
        (( cnt = "${cnt}" + 1 ))
        # echo "${cnt}" "${f}" 

        curl -XPOST "localhost:9200/test2/page/${f}?pretty" -H 'Content-Type: application/json' -d @$f;

done