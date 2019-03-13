
# coding: utf-8

# In[1]:


import sys
import getopt
import os
#from sets import Set
import json
import pprint
import itertools
from graphviz import Digraph
from math import log
import src.searchtree_v2 as st
import src.utils as utils
import csv   
from pathlib import Path


# In[2]:


#agent is the agent we are trying to compromise. We are the adversary. All other agents are the adversary.
agent=0
domain="uav-factored"#"logistics-small-factored"#"uav-factored"#"logistics-small-factored"#"uav-factored"#"logistics-small-factored"#"logistics-small-factored" #
problem="example"#"test"#"example-big"#"example-big" #"test"#"test"#"probLOGISTICS-4-0"

st.configuration["privateActions"]=False 
st.configuration["nTo1Mapping"]=1
st.configuration["useStateIDs"]=False
st.configuration["SecureMAFS"]=False
st.configuration["projectedHeuristic"]=False
st.configuration["debug"]=False
st.configuration["console"]=True

if st.configuration["console"]:
    #read options
    params = ["domain=","problem=","agent=","n-to-1-mapping=","secure-mafs","debug","proj"]
    #print(str(sys.argv))
    try:
        opts, args = getopt.getopt(sys.argv[1:],'',params)
        #print("opts:"+str(opts))
        #print("args:"+str(args))
    except getopt.GetoptError:
        print ('bad leakage.py params: ' + str(params))
        sys.exit(2)

    for opt, arg in opts:
        print("opt="+str(opt)+",arg="+str(arg))
        if opt == "--domain":
            domain = arg
        elif opt == "--problem":
            problem = arg
        elif opt == "--agent":
            agent = int(arg)
        elif opt == "--n-to-1-mapping":
            st.configuration["nTo1Mapping"] = int(arg)
        elif opt == "--secure-mafs":
            st.configuration["SecureMAFS"]=True
        elif opt == "--debug":
            st.configuration["debug"]=True
        elif opt == "--proj":
            st.configuration["projectedHeuristic"]=True

st.configuration["useStateIDs"] = False        
if st.configuration["nTo1Mapping"] >= 1000:
    st.configuration["useStateIDs"] = True

        
root="traces/"+domain+"/"+problem+"/"+str(st.configuration["nTo1Mapping"])

if st.configuration["projectedHeuristic"]:
    root=root+"-proj"

agentFile=root+"/agent"+str(agent)+".json"
#adversaryFile=root+"/agent"+str(adversary)+".json"

if st.configuration["SecureMAFS"]:
    outputFile=root+"-SecureMAFS"
    
print(root)

    
outputFile=root+"/agent"+str(agent)

outputCSVFile = "./results_simple.csv"


# In[3]:


#load data
varMap = {}
stateMap = {}
#states = {}
opMap = {}
operators = set()

advers = set()
states = []

plan = []


for fileName in os.listdir(root):
    agentID = -1
    if fileName.find("agent")!= -1 and fileName.find(".json")!= -1:
        #print("next: "+fileName[fileName.find("agent")+5:fileName.find(".json")])
        agentID=int(fileName[fileName.find("agent")+5:fileName.find(".json")])
        #print(agentID)
    if agentID != -1 and agentID != agent:
        print("processing " + fileName)
        
        advers.add(agentID)

        f = open(root+"/"+fileName)
        data = json.load(f)
        f.close()

        #load variables
        for v in data["variables"]:
            #print(v)
            var = st.Variable(v)
            varMap[var.hash] = var
        
        if st.configuration["debug"]:
            print("variables:")
            pprint.pprint(varMap)
            
        #load states
        order = 0
        secureMAFSStates = set()
        for s in data["states"]:
            state = st.State(s,varMap,order)
            
            #ignore states not sent by Secure-MAFS 
            #TODO: verify correctness
            if st.configuration["SecureMAFS"] and state.senderID == agent:
                if state.getSecureMAFSSignature() in secureMAFSStates:
                    continue
                else:
                    secureMAFSStates.add(state.getSecureMAFSSignature())
            
            if not state.hash in stateMap:
                stateMap[state.hash] = state
            else:
                print("WARNING: " + str(state) + " already in stateMap: " + str(stateMap[state.hash]))
            #states[agentID].append(state)
            order += 1
            states.append(state)
            
        #states = [s for s in stateMap.values()]

        #load operators (and convert to label non-preserving projection)
        allOps = filter(lambda x: x["ownerID"]==agent,data["operators"])
        
        for op in allOps:
            #print(op)
            operator = st.Operator(op)
            if operator.hash in opMap:
                opMap[operator.hash].process(operator)
            else:
                opMap[operator.hash] = operator

        operators = operators | set(opMap.values())
        
        plan = data["plan"]
        
received = list(filter(lambda x: x.isReceivedFromAgent(agent) and x.iparentID != -1, states))
sent = list(filter(lambda x: x.isSent() or x.isInit(), states))

        
print("done!")  
print("variables:" + str(len(varMap)))
print("operators:" + str(len(operators)))
print("states:" + str(len(states)))
print("received:" + str(len(received)))
print("sent:" + str(len(sent)))

if st.configuration["debug"]:
    print("varMap:")
    pprint.pprint(varMap)
    
    if len(states) < 25:
        print("stateMap:")
        pprint.pprint(stateMap)
        print("states:")
        pprint.pprint(states)
        
    if len(received) < 25:
        print("received:")
        pprint.pprint(received)
        
    if len(sent) < 25:
        print("sent:")
        pprint.pprint(sent)
        
    print("opMap:")
    pprint.pprint(opMap)
    print("operators:")
    pprint.pprint(operators)
    print("plan:")
    pprint.pprint(plan)
            



# In[4]:


#load agent data (instead of estimates)
f = open(agentFile)
agentData = json.load(f)
f.close()

agentPrivateVariables = 0
agentDomainSize = 0

for var in agentData["variables"]:
    if var["isPrivate"]:
        agentPrivateVariables += 1
        agentDomainSize = max(agentDomainSize,len(var["vals"]))
        
for op in agentData["operators"]:
    if op["isPrivate"]:
        st.configuration["privateActions"]=True
        break
        
print("agent private variables: " + str(agentPrivateVariables))
print("agent domain size: " + str(agentDomainSize))
print("private actions: " + str(st.configuration["privateActions"]))


# In[5]:


#find actions responsible for the i-parent transitions
#compute maximum g received from adversary
#only states with secondMaxG can be considered

maxg = 0
for state in received: 
    iparentHash = state.getIParentHash()
        
    if iparentHash in stateMap:
        iparent = stateMap[iparentHash]
        
        for op in operators:
            state.isIParent(op,iparent,agent)
            
    else:
        print("WARNING!: i-parent state " + iparentHash + " not found!")
        print(state)
        
    if st.configuration["debug"]:
        print(iparentHash+" -> " + state.hash + ": " + str(state.getIParentTransition()))
    
    if state.cost>=maxg:
        maxg = state.cost

secmaxg = 0
for state in received:
    if state.cost>=secmaxg and state.cost<maxg:
        secmaxg=state.cost

print("maxg="+str(maxg))
print("second maxg="+str(secmaxg))

st.configuration["maxG"]=secmaxg  


# In[6]:


#pebd states

def findPEBDStates(s1,states):
    pebd = set()
    
    for s2 in states:
        #publicly equivalent?
        if s1.publicValues == s2.publicValues:
            #distinct?
            if s1.isDistinct(s2,agent):
                pebd.add(s2.hash)
                
    return pebd

def isPEBD(s1,s2):
    #publicly equivalent?
    if s1.publicValues == s2.publicValues:
        #distinct?
        if s1.isDistinct(s2,agent):
            return True
                
    return False

def isPEBDHash(s1h,s2h):
    if s1h in stateMap and s2h in stateMap:
        s1 = stateMap[s1h]
        s2 = stateMap[s2h]
        #publicly equivalent?
        if s1.publicValues == s2.publicValues:
            #distinct?
            if s1.isDistinct(s2,agent):
                return True
                
    return False
                


        


# In[7]:



if st.configuration["debug"] and len(states) < 25:
    print("publicly equivalent but distinct states:")
    for s in received:
        pebd = findPEBDStates(s,received)
        print(str(s.hash)+": "+str(pebd))
        


# In[8]:


#show search tree in graphviz
if st.configuration["debug"] and len(states) < 50:
    from graphviz import Digraph


    def getNodeID(state):
        return str(state.agentID)+str(state.stateID)

    dot = Digraph(comment=root,engine="dot")

    with dot.subgraph(name='agents') as dotA:
        dotA.node("agent" + str(agent),"agent" + str(agent))

    with dot.subgraph(name='states') as dotS:
        #dotS.attr(rankdir='LR')
        #dotS.attr(rank='same')
        dotS.attr(ordering='out')

        x = 10
        y = 10
        for state in states:
            label = state.printStateDotLabel()

            position = str(x)+","+str(y)+"!"

            #add state, special case for initial state
            id = getNodeID(state)
            if state.isInit():
                dotS.node(id, label, shape='invhouse',pos=position)

            elif state.isGoal():
                dotS.node(id, label, shape='house', style='bold',pos=position)
            else:
                if state.isReceivedFromAgent(agent):
                    dotS.node(id, label, shape='box',color='red',pos=position)
                else:
                    dotS.node(id, label, shape='box',pos=position)

            y += 1
            x = 8+(x+1)%4

        prev = -1
        done = set()
        for state in states:
            #add received edge if not initial state
            if state.isReceived() or state.isGoal():
                if state.senderID==agent:
                    dotS.edge("agent"+str(state.senderID), getNodeID(state),color='red',constraint='false')
                
                if state.senderID==-1:
                    #dotS.edge(str(state["stateID"]),"agent"+str(adversary))
                    dotS.edge(str(state.agentID)+str(state.parentID),getNodeID(state),style='bold',color='grey',constraint='false')

            #add i-parent edge if received from the agent
            if state.isReceivedFromAgent(agent):
                iparent = stateMap[state.getIParentHash()]
                label="\n".join(str(op) for op in state.getIParentTransition())
                dotS.edge(getNodeID(iparent), getNodeID(state),style='dashed',label=label,constraint='false')

            #invisible link from previous state
            if prev != -1 and prev.agentID == state.agentID:
                dotS.edge(getNodeID(prev), getNodeID(state),style='invis')

            prev=state

            #publicly equivalent but distinct edges
            if state.isReceivedFromAgent(agent):
                pebd = findPEBDStates(state,received)
                for ps in pebd:
                    fromS = getNodeID(state)
                    toS = ps.replace(":","")
                    code = str({fromS,toS})
                    if not code in done:
                        dotS.edge(fromS, toS,color='orange',style='dashed',dir='none',constraint='false')
                        done.add(code)

        #print(done)




    #print variables
    for var in varMap:
        print(str(var)+" (private=" + str(varMap[var].isPrivate) + "):")
        for val in varMap[var].vals:
            print( "   " + val + " = "+ varMap[var].vals[val])

    #print(dot.source)

    print(secureMAFSStates)

    dot.render(outputFile)
    
#dot



# In[9]:


#debug
if st.configuration["debug"]:
    for op in operators:
        print(op.representative + ":"+str(op.applicableInIParent))


# In[10]:


#find all direct child states of init to determine init applicability
initStates = set()
for adv in advers:
    for s in list(filter(lambda x: x.agentID == adv, states)):
        if s.senderID != -1:
            break
        initStates.add(s.hash)

print("initial states:")
print(initStates)


# In[11]:


#debug print
if st.configuration["debug"]:
    #pprint.pprint(transitions)
#     pprint.pprint(pe)
    for op in operators:
        print(str(op)+": "+ str(op.transitions))


# In[12]:


#determine RHS based on combinations of properties

#check cache:
cacheCSVFile = "ts_cache.csv"
outCSV = Path(cacheCSVFile)
exists = outCSV.is_file()
found = False
if exists:
    with open("ts_cache.csv") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            if row['agentDomainSize']==str(agentDomainSize) and  row['agentPrivateVariables']==str(agentPrivateVariables):
                print("Reading from cache file!")
                print(row)
                found = True
                
                rhs_t = float(row['ts'])
                rhs_ia = float(row['ia'])
                rhs_nia = float(row['nia'])
                rhs_pi = float(row['pi'])
                rhs_pd = float(row['pd'])
                rhs_pn = float(row['pn'])
                rhs_ia_pi = float(row['ia_pi'])
                rhs_ia_pd = float(row['ia_pd'])
                rhs_ia_pn = float(row['ia_pn'])
                rhs_nia_pi = float(row['nia_pi'])
                rhs_nia_pd = float(row['nia_pd'])
                rhs_nia_pn = float(row['nia_pn'])
                rhs_pi_pn = float(row['pi_pn'])
                rhs_pd_pn = float(row['pd_pn'])
                rhs_ia_pi_pn = float(row['ia_pi_pn'])
                rhs_ia_pd_pn = float(row['ia_pd_pn'])
                rhs_nia_pi_pn = float(row['nia_pi_pn'])
                rhs_nia_pd_pn = float(row['nia_pd_pn'])

if not found:
    print("Extrapolate:")
    
    print("ts")
    ts = (2**(agentDomainSize**2) - 1)**agentPrivateVariables 
    print(ts)
    rhs_t = log(ts,2)

    rhs_ia = rhs_t * 0.9623365224
    rhs_nia = rhs_t * 0.7608990865
    rhs_pi = rhs_t * 0.9079967542
    rhs_pd = rhs_t * 1
    rhs_pn = rhs_t * 0.9079967542
    rhs_ia_pi = rhs_t * 0.9079967542
    rhs_ia_pd = rhs_t * 0.9623365224
    rhs_ia_pn = rhs_t * 0.8873803107
    rhs_nia_pi = rhs_t * 0.7416588191
    rhs_nia_pd = rhs_t * 0.7608990865
    rhs_nia_pn = rhs_t * 0.6776340665
    rhs_pi_pn = rhs_t * 0.8590151103
    rhs_pd_pn = rhs_t * 0.9079967542
    rhs_ia_pi_pn = rhs_t * 0.8590151103
    rhs_ia_pd_pn = rhs_t * 0.8873803107
    rhs_nia_pi_pn = rhs_t * 0.607375089
    rhs_nia_pd_pn = rhs_t * 0.6776340665



# In[13]:


#initialize LP
LP = st.LP({})

#basic operators
for op in operators:   
    LP.addDisjunctiveConstraint([op.hash],rhs_t)
    
    
#only for statistics and correctness test, actions for which we know the property holds
ia = set()
nia = set()
pd = set()
pi = set()
pn = set()


# In[14]:


#load and process plan

print(plan)

apr_ia = "none"

opNames = [op.opNames for op in operators]
if st.configuration["debug"]:
    print(opNames)

for opName in plan:
    print(opName)
    for op in operators:
        if opName in op.opNames:
            apr_ia = op
            print("apr_ia = " + opName)
            break
            
    if apr_ia != "none":
        break

if st.configuration["debug"]:
    print( str(apr_ia) + " is init-applicable because it is the first action of agent "+str(agent)+" in the plan")

if apr_ia != "none":
    ia.add(apr_ia.hash) #also learned from the plan in the post
    LP.addDisjunctiveConstraint([apr_ia.hash],rhs_ia)
       
print ("apriori ia: " + str(apr_ia))
print(ia)


# In[15]:



#init applicable operators
print(initStates)
print(ia)

for s in received:
#     print(str(s)+" - " + s.getIParentHash())
    
    if s.getIParentHash() in initStates:
        
        if st.configuration["debug"]:
            print( "one of " + str(s.getIParentTransition()) + " is init-applicable because " + s.getIParentHash() + " is an initial state")
           
        if len(s.getIParentTransition()) > 0:
            
            LP.addDisjunctiveConstraint(s.getIParentTransition(),rhs_ia)
            
            for opHash in s.getIParentTransition():
                ia.add(opHash)
                

#init-unapplicable operators
for op in operators:
    if not op.hash in ia:
        nia.add(op.hash)
        LP.addDisjunctiveConstraint(s.getIParentTransition(),rhs_nia)
        
print(ia)

            


# In[16]:


#privately independent operators
for op in operators:
#     print(op)
    for pair in itertools.combinations(op.transitions,2):
        s1 = pair[0]
        s2 = pair[1]
        
        s1ip = stateMap[s1].getIParentHash()
        s2ip = stateMap[s2].getIParentHash()
        
        if stateMap[s1].isInit() and stateMap[s2].isInit():
            continue
    
        if isPEBDHash(s1ip,s2ip):
            ops = stateMap[s1].getIParentTransition() & stateMap[s2].getIParentTransition()

            if st.configuration["debug"]:
                print( "one of " + str(ops) + " is privately-independent because " + str(s1ip) + " is PEBD to " + str(s2ip))

            ops_ia = ops & ia
            ops_nia = ops & nia
            ops_unknown = ops - ops_ia - ops_nia
            LP.addDisjunctiveConstraint3(ops_ia,rhs_ia_pi,ops_nia,rhs_nia_pi,ops_unknown,rhs_pi)

            if len(ops) == 1:
                pi.update(ops)
    

                    


# In[17]:


# #privately dependent operators
# #if there are publicly equivalent (not necessarily distinct) states s1,s2 s.t. op is applied in one and not in the other, op is pd in at least one variable
# #TODO: this does not capture the situation where we know that one of the pebd iparents is the real iparent and the other is not and thus the op is pd

# #TODO: test!
# for op in operators:
# #     print("check " + op.hash)
#     for s1 in op.transitions:
#         s1ip = stateMap[s1].getIParentHash()
#         for s2ip in pebd[s1ip]:
#             for s2 in stateMap[s2ip].successors:
#                 if not op.hash in stateMap[s2].getIParentTransition():
#                     ops = stateMap[s1].getIParentTransition() - stateMap[s2].getIParentTransition()

#                     if st.configuration["debug"]:
#                         print( "some of " + str(ops) + " is privately-dependent because it is applicable in " + str(s1ip) + " but not in " + str(s2ip) + " which are PEBD")

#                     ops_ia = ops & ia
#                     ops_nia = ops & nia
#                     ops_unknown = ops - ops_ia - ops_nia
#                     LP.addDisjunctiveConstraint3(ops_ia,rhs_ia_pd,ops_nia,rhs_nia_pd,ops_unknown,rhs_pd)

#                     if len(ops) == 1:
#                         pd.update(ops)

            


# In[18]:


#privately nondeterministic operators
for op in operators:
#     print(op)
    for pair in itertools.combinations(op.transitions,2):
        s1 = pair[0]
        s2 = pair[1]
    
        if isPEBDHash(s1,s2):
            s1ip = stateMap[s1].getIParentHash()
            s2ip = stateMap[s2].getIParentHash()
            
            if s1ip != s2ip:
                continue
            
            ops = stateMap[s1].getIParentTransition() & stateMap[s2].getIParentTransition()

            if st.configuration["debug"]:
                print( "one of " + str(ops) + " is privately-nondeterministic because " + str(s1) + " is PEBD to " + str(s2))

            ops_ia = ops & ia
            ops_nia = ops & nia
            ops_unknown = ops - ops_ia - ops_nia
            LP.addDisjunctiveConstraint3(ops_ia,rhs_ia_pn,ops_nia,rhs_nia_pn,ops_unknown,rhs_pn)


            if len(ops) == 1:
                pn.update(ops)
                    


# In[19]:


#combinations
ops_pi_pn = pi & pn
LP.addDisjunctiveConstraint(ops_pi_pn,rhs_pi_pn)

ops_pd_pn = pd & pn
LP.addDisjunctiveConstraint(ops_pd_pn,rhs_pd_pn)

ops_ia_pi_pn = ia & pi & pn
LP.addDisjunctiveConstraint(ops_ia_pi_pn,rhs_ia_pi_pn)

ops_ia_pd_pn = ia & pd & pn
LP.addDisjunctiveConstraint(ops_ia_pd_pn,rhs_ia_pd_pn)

ops_nia_pi_pn = nia & pi & pn
LP.addDisjunctiveConstraint(ops_nia_pi_pn,rhs_nia_pi_pn)

ops_nia_pd_pn = nia & pd & pn
LP.addDisjunctiveConstraint(ops_nia_pd_pn,rhs_nia_pd_pn)


# In[20]:


#solve the LP

import pulp

model = pulp.LpProblem("Leakage", pulp.LpMaximize)
M = 1000


for var in LP.varMap:
    LP.addLPVar(var,pulp.LpVariable(str(var), lowBound=0, cat='Continuous'))
     
model += (
    pulp.lpSum([LP.lpVarMap[v] for v in LP.lpVarMap])
)

countC = 0
for dc in LP.constraints:
    if len(dc.constraints) > 1:
        binCs = []
        countB = 0
        for c in dc.constraints:
            binC = pulp.LpVariable("c"+str(countC)+"b"+str(countB), cat='Binary')
            binCs.append(binC)
            countB += 1

            model += (
                pulp.lpSum([coef * LP.lpVarMap[var] for coef in c.coefs for var in c.vars]) <= c.RS + M - M * binC
            )

        model += (
            pulp.lpSum(binCs) == 1
        )
    elif len(dc.constraints) == 1:
        model += (
            pulp.lpSum([coef * LP.lpVarMap[var] for coef in dc.constraints[0].coefs for var in dc.constraints[0].vars]) <= dc.constraints[0].RS
        )
    
    countC += 1

if st.configuration["debug"]:
    print(LP.varMap)
    print(model)

model.solve()
pulp.LpStatus[model.status]

if st.configuration["debug"]:
    for variable in model.variables():
        print(str(variable.name) + " = " + str(variable.varValue))

print ("objective value: " + str(pulp.value(model.objective)))
post = pulp.value(model.objective)


# In[21]:


#compute the leakage
print("apriori:")
apr = 0
for op in operators:
    if op == apr_ia:
        print(str(op) + " = " + str(rhs_ia))
        apr += rhs_ia
    else:
        print(str(op) + " = " + str(rhs_t))
        apr += rhs_t

leakage = apr - post
print(str(apr)+" - "+str(post)+" = " + str(leakage))


# In[22]:


#determine the ground truth leakage
#let's do this more efficiently! We can just evaluate the true model - no formula and logic inference

# gt_formula = True

gt_ia = set()
gt_pi = set()
gt_pd = set()
gt_pn = set()

if apr_ia != "none":
    gt_ia.add(apr_ia.hash) #the op applicable acoording to the plan

def getOpPubHash(op,pubVars):
    pubPre = {}
    pubEff = {}
    for v in pubVars:
        if v in op.pre:
            pubPre[v] = op.pre[v]
        if v in op.eff:
            pubEff[v] = op.eff[v]
    return str(pubPre)+"->"+str(pubEff)

#determine private vars
privateVars = set()
allPrivVarVals = {}
publicVars = set()
for varData in agentData["variables"]:
    if varData["isPrivate"]:
        privateVars.add(varData["varID"])
        allPrivVarVals[str(varData["varID"])] = set()
        for val in varData["vals"]:
            allPrivVarVals[str(varData["varID"])].add(str(varData["varID"])+":"+str(val))
    else:
        publicVars.add(varData["varID"])
            
#map ops to their private effects
gtOpMap = {}
gtOps = set()
            
for opData in agentData["operators"]:
    if opData["ownerID"] != agent or opData["isPrivate"]:
        continue
    
    #print(str(opData))
    op = st.Operator(opData)
    #print(str(op))
    
    gtOps.add(getOpPubHash(op,publicVars))
    
    #init applicable?
    for stateData in agentData["states"]:
        if stateData["context"] != "init" and stateData["senderID"]==-1:
            break
        
        applicable = True
        for var in op.pre:
            if stateData["values"][var] != op.pre[var]:
                applicable = False
                break
                
        if applicable:
            gt_ia.add(getOpPubHash(op,publicVars))
#             gt_formula = gt_formula & opInitApplicableInState(getOpPubHash(op,publicVars))
            break
        
    #prepare for pi,pd,pn detection
    opph = getOpPubHash(op,publicVars)
    if not opph in gtOpMap:
        gtOpMap[opph] = {}
        gtOpMap[opph]["allPrivateEffSets"] = set()
        gtOpMap[opph]["allPrivatePre"] = set()
        gtOpMap[opph]["maxPrivatePreVars"] = 0
        
    allPrivateEff = set()
    for v in op.eff:
        if v in privateVars:
            allPrivateEff.add(str(v)+":"+str(op.eff[v]))
    
    gtOpMap[opph]["allPrivateEffSets"].add(str(allPrivateEff)) #for pn #WARNING: relies on the uniqueness of string representation of sets!
    
    privVars = 0
    for v in op.pre:
        if v in privateVars:
            gtOpMap[opph]["allPrivatePre"].add(str(v)+":"+str(op.pre[v])) #for pd
            privVars += 1 # for pi
            
    gtOpMap[opph]["maxPrivatePreVars"] = max (gtOpMap[opph]["maxPrivatePreVars"],privVars)

if st.configuration["debug"]:           
    print("allPrivVarVals:")
#     pprint.pprint(allPrivVarVals)
    

for opph in gtOpMap:
    if st.configuration["debug"]:
        print(str(opph)+":"+str(gtOpMap[opph]))
    
    #privately nondeterministic?
    if len(gtOpMap[opph]["allPrivateEffSets"]) > 1: #TODO: what if the private effects are the same? should be unique!
        gt_pn.add(opph)
#         gt_formula = gt_formula & opPrivatelyNondeterministic(opph,1)
    
    #the most benevolent definition of private independency:
    #privately independent?
    if len(gtOpMap[opph]["allPrivatePre"]) > 1:#and gtOpMap[opph]["maxPrivatePreVars"] < len(allPrivVarVals.keys()):
        #applicable in more than one private var-val
        gt_pi.add(opph)
#         gt_formula = gt_formula & opPrivatelyIndependent(opph,1)
    
    for var in allPrivVarVals:
        #privately dependent?
#         print(str(opph) + " pd for "+str(var)+"? " + str(allPrivVarVals[var] & gtOpMap[opph]["allPrivatePre"]))
        #if there is a private variable with at least one defined value in pre (the intersection is not empty)
        #but not with all values defined
        if allPrivVarVals[var] & gtOpMap[opph]["allPrivatePre"] and allPrivVarVals[var] - gtOpMap[opph]["allPrivatePre"]:
#             print("yes")
            gt_pd.add(opph)
#             gt_formula = gt_formula & opPrivatelyDependent(opph,1)
    

# print(gt_formula)
 

    


# In[23]:


#compute ground truth leakage
#compute leakage
gt_nia = gtOps - gt_ia
gt_ia_pi = gt_ia & gt_pi
gt_ia_pd = gt_ia & gt_pd
gt_ia_pn = gt_ia & gt_pn
gt_nia_pi = gt_nia & gt_pi
gt_nia_pd = gt_nia & gt_pd
gt_nia_pn = gt_nia & gt_pn
gt_pi_pn = gt_pi & gt_pn
gt_pd_pn = gt_pd & gt_pn
gt_ia_pi_pn = gt_ia & gt_pi & gt_pn
gt_ia_pd_pn = gt_ia & gt_pd & gt_pn
gt_nia_pi_pn = gt_nia & gt_pi & gt_pn
gt_nia_pd_pn = gt_nia & gt_pd & gt_pn

print(gt_ia)
print(gt_nia)

gt_post = 0
print("gtOps:"+str(gtOps))
for op in gtOps:
    t = rhs_t
    if op in gt_ia:
        t = min(t,rhs_ia)
    if op in gt_nia:
        t = min(t,rhs_nia)
    if op in gt_pi:
        t = min(t,rhs_pi)
    if op in gt_pd:
        t = min(t,rhs_pd)
    if op in gt_pn:
        t = min(t,rhs_pn)
    if op in gt_ia_pi:
        t = min(t,rhs_ia_pi)
    if op in gt_ia_pd:
        t = min(t,rhs_ia_pd)
    if op in gt_ia_pn:
        t = min(t,rhs_ia_pn)
    if op in gt_pi_pn:
        t = min(t,rhs_pi_pn)
    if op in gt_pd_pn:
        t = min(t,rhs_pd_pn)
    if op in gt_nia_pi:
        t = min(t,rhs_nia_pi)
    if op in gt_nia_pd:
        t = min(t,rhs_nia_pd)
    if op in gt_nia_pn:
        t = min(t,rhs_nia_pn)
    if op in gt_ia_pi_pn:
        t = min(t,rhs_ia_pi_pn)
    if op in gt_ia_pd_pn:
        t = min(t,rhs_ia_pd_pn)
    if op in gt_nia_pi_pn:
        t = min(t,rhs_nia_pi_pn)
    if op in gt_nia_pd_pn:
        t = min(t,rhs_nia_pd_pn)
            
    gt_post += t

gt_leakage = apr - gt_post
print(str(apr)+" - "+str(gt_post)+" = " + str(gt_leakage))
        
        


# In[24]:


print("leakage: ")
for op in operators:
    out = str(op) + " : "
    if op.hash in ia:
        out += "ia "
    if op.hash in nia:
        out += "nia "
    if op.hash in pi:
        out += "pi "
    if op.hash in pd:
        out += "pd "
    if op.hash in pn:
        out += "pn "
    print(out)
    
print("leakage = " + str(leakage))

print("ground truth: ")
for op in operators:
    out = str(op) + " : "
    if op.hash in gt_ia:
        out += "ia "
    if op.hash in gt_nia:
        out += "nia "
    if op.hash in gt_pi:
        out += "pi "
    if op.hash in gt_pd:
        out += "pd "
    if op.hash in gt_pn:
        out += "pn "
    print(out)
    
print("gt leakage = " + str(gt_leakage))


# In[25]:


#test correctness
correct = leakage <= gt_leakage

print(ia)
print(nia)

if correct:
    correct = nia.issubset(gt_nia) and ia.issubset(gt_ia) and pi.issubset(gt_pi) and pd.issubset(gt_pd) and pn.issubset(gt_pn)
    print("correct: " + str(correct))

if not correct:
    if leakage > gt_leakage:
        print("WRONG:"+str(leakage)+" > "+str(gt_leakage))
    if ia & nia:
        print("WRONG ia & nia:"+str(ia & nia))
    if nia-gt_nia:
        print("WRONG nia:"+str(nia-gt_nia))
    if ia-gt_ia:
        print("WRONG ia:"+str(ia-gt_ia))
    if pi-gt_pi: 
        print("WRONG pi:"+str(pi-gt_pi))
    if pd-gt_pd:
        print("WRONG pi:"+str(pd-gt_pd))
    if pn-gt_pn:
        print("WRONG pn:"+str(pn-gt_pn))
    


# In[26]:


#write the leakage output and parameters

columns=[
    'domain',
    'problem',
    'agent',
    'privateActions',
    'useStateIDs',
    'nTo1Mapping',
    'SecureMAFS',
    'projectedHeuristic',
    'receivedStates',
    'nia',
    'ia',
    'pi',
    'pd',
    'pn',
    'apriori',
    'post',
    'leakage',
    'gt_ia',
    'gt_pi',
    'gt_pd',
    'gt_pn',
    'gt_post',
    'gt_leakage',
    'ratio',
    'correct'
]

outCSV = Path(outputCSVFile)
exists = outCSV.is_file()

ratio = 0
if gt_leakage != 0:
    ratio = leakage/gt_leakage

row = [
    domain,
    problem,
    agent,
    st.configuration["privateActions"],
    st.configuration["useStateIDs"],
    st.configuration["nTo1Mapping"],
    st.configuration["SecureMAFS"],
    st.configuration["projectedHeuristic"],
    len(received),
    len(nia),
    len(ia),
    len(pi),
    len(pd),
    len(pn),
    apr,
    post,
    leakage,
    len(gt_ia),
    len(gt_pi),
    len(gt_pd),
    len(gt_pn),
    gt_post,
    gt_leakage,
    ratio,
    correct
]

with open(outputCSVFile, 'a') as f:
    writer = csv.writer(f)
    if not exists:
        writer.writerow(columns)
    writer.writerow(row)
    
print("correct: " + str(correct))
print("all done!")

