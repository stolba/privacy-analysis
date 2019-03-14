# privacy-analysis
Code for the "Privacy Leakage of Search-based Multi-Agent Planning Algorithms" paper to appear at ICAPS 2019. Relevant papers are stored in the doc folder.

# requirements
* python3 with pulp for LP computation
* graphviz for vizualization (optional)
* jupyter notebook/lab to open the interactive .ipnb files (optional, the pure python scripts can be run without it)
* sympy to compute and cache the number of transition systems (optional)

# running
1. install dependencies
2. open leakage_v2_simple.ipnb to analyze example traces

# creating new traces
* You can checkout and build the MAPlan planner from https://gitlab.com/danfis/maplan.git branch icaps19-privacy-leakage-priv-id-quantification which is modified to output the search traces. You can run it using the included bash scripts (you will need to modify the paths in run_maplan.sh.
* You can modify your planner to output traces in JSON format according to the example traces in traces/uav/p01/1.

