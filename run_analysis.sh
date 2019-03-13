#!/bin/bash
#$1 path to problem

[[ "$1" == */* ]] || p="./$1"
rest="${1%/*}" 
problem="${1##*/}"

echo $rest
echo $problem

domain="${rest/\.\/benchmarks\//}"

echo $domain

python3 ./leakage.py --domain $domain --problem $problem --agent $2 --use-full-states $3 --n-to-1-mapping $4 $5 $6 

#python3 ./reconstruct_global.py --domain $domain --problem $problem --agent $2 --use-full-states $3 --n-to-1-mapping $4 $5 $6 






