#!/bin/bash

benchmark="$1"
nTo1="$2"
proj="$3"

#$1 path to problem

[[ "$1" == */* ]] || p="./$1"
rest="${1%/*}" 
problem="${1##*/}"

echo $rest
echo $problem

domain="${rest/\.\/benchmarks\//}"

echo $domain

tracesdir="./traces/$domain/$problem/$nTo1"

echo "RUN MAPLAN"

if [ $3 = "proj" ]; then
	./run_maplan_proj.sh $benchmark $nTo1
	tracesdir="${tracesdir}-proj"
else
	./run_maplan.sh $benchmark $nTo1
fi

echo $tracesdir

rm $tracesdir/*.out

echo "ANALYZE"
for agent in 0 1
do

	if [ $proj = "proj" ]; then
		echo "ANALYZE proj" 
		python3 ./leakage.py --domain $domain --problem $problem --agent $agent --n-to-1-mapping $nTo1 --proj > $tracesdir/analysis-proj.log	

		if [ $nTo1 = "1" ]; then
			echo "ANALYZE Secure-MAFS" 
			python3 ./leakage.py --domain $domain --problem $problem --agent $agent --n-to-1-mapping $nTo1 --secure-mafs --proj > $tracesdir/analysis-secure-mafs.log
		fi


	else
		echo "ANALYZE normal" 
		python3 ./leakage.py --domain $domain --problem $problem --agent $agent --n-to-1-mapping $nTo1 #> $tracesdir/analysis.log

		if [ $nTo1 = "1" ]; then
			echo "ANALYZE Secure-MAFS" 
			python3 ./leakage.py --domain $domain --problem $problem --agent $agent --n-to-1-mapping $nTo1 --secure-mafs > $tracesdir/analysis-secure-mafs.log
		fi
	fi

	

	
	
done

rm $tracesdir/*.json

