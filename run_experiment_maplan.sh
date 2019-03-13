#!/bin/bash

mv ./results.csv ./results-backup.csv

ulimit -Sv 2000000

 for domain in uav-factored logistics-small-factored blocksworld-factored elevators08-factored
 do
 	for folder in ./benchmarks/$domain/*
 	do
 		echo "processing $folder"
 		for nTo1Mapping in 1 5 10 100 1000
 		do
		
			./run_maplan.sh $folder $nTo1Mapping
 		done

		./run_maplan_proj.sh $folder 1
	
 	done
 done

