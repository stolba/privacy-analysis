#!/bin/bash


OUT_FOLDER=$1

mkdir -p $OUT_FOLDER

echo "analyze"
cat std.out | grep "VAR" | sed 's/VAR://g' > $OUT_FOLDER/variables.out
cat std.out | grep "OP" | sed 's/OP://g' > $OUT_FOLDER/operators.out

for outfile in $OUT_FOLDER/out_*
	do
		cat $outfile | grep "STATE" | sed 's/STATE://g'>> $OUT_FOLDER/states.out
	done

filenamelist=(out_*)
count=${#filenamelist[@]}
count=$((count-1))
echo "agents 0 - $count"


for agent in `seq 0 $count`
	do
		echo "AGENT $agent"
		
		OUT="$OUT_FOLDER/agent${agent}.json"	
		echo "output=$OUT"

		echo $'{\n' > $OUT
	
		echo $'\"variables\":[' >> $OUT
		cat $OUT_FOLDER/variables.out | grep "\"agentID\":$agent" | head -n -1 > $OUT_FOLDER/temp  

		while read LINE
		do echo "$LINE," >> $OUT
		done < $OUT_FOLDER/temp
		
		cat $OUT_FOLDER/variables.out | grep "\"agentID\":$agent" | tail -n 1 >> $OUT
		

		echo $'],\n' >> $OUT

		echo $'\"operators\":[' >> $OUT
		cat $OUT_FOLDER/operators.out | grep "\"agentID\":$agent" | head -n -1 > $OUT_FOLDER/temp  

		while read LINE
		do echo "$LINE," >> $OUT
		done < $OUT_FOLDER/temp
		
		cat $OUT_FOLDER/operators.out | grep "\"agentID\":$agent" | tail -n 1 >> $OUT
		

		echo $'],\n' >> $OUT
		echo $'\"states\":[' >> $OUT

		cat $OUT_FOLDER/states.out | grep "\"agentID\":$agent"  | head -n -1 > $OUT_FOLDER/temp  

		while read LINE
		do echo "$LINE," >> $OUT
		done < $OUT_FOLDER/temp
		
		cat $OUT_FOLDER/states.out | grep "\"agentID\":$agent" | tail -n 1 >> $OUT

		
		echo $'],\n' >> $OUT
		echo $'\"plan\":[' >> $OUT

		
		#cat plan.out
		cat plan.out | tr '()' '\"\"' | head -n -1 > $OUT_FOLDER/temp  

		while read LINE
		do echo "$LINE," >> $OUT
		done < $OUT_FOLDER/temp

		#cat $OUT_FOLDER/temp

		cat plan.out | tr '()' '\"\"' | tail -n 1 >> $OUT 
		
		echo $']\n}' >> $OUT

		rm $OUT_FOLDER/temp 
	done




