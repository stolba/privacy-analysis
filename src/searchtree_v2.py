import functools


configuration = {
    "privateActions":True,
    "maxG":10000,
    "nTo1Mapping":1,
    "useStateIDs":False,
    "projectedHeuristic":False,
    "debug":False,
    "console":True
}
    

### Variable ###

def getVarHash(var):
    return composeVarHash(var.agentID,var.varID,var.isPrivate)
    
def composeVarHash(agent,varID,private):
    if private:
        return str(agent)+":"+str(varID)
    else:
        return "P:"+str(varID)

class Variable:
    def __init__(self,data):
        self.agentID = data["agentID"]
        self.varID = data["varID"]
        self.varName = data["varName"]
        self.isPrivate = data["isPrivate"]
        self.range = data["range"]
        self.vals = data["vals"]
        
        self.hash = getVarHash(self)
        
    def __repr__(self):
        return str(self.hash)+":"+str(self.isPrivate)+":"+str(self.vals)
    
    
    
### Operator ###

def getOpHash(op):
    return op.label
        
class Operator:
    def __init__(self,data):
        self.agentID = data["agentID"] #WARNINNG:does not make sense with multiple agents
        self.ownerID = data["ownerID"]
        self.opID = data["opID"]
        self.cost = data["cost"]
        self.opNames = [data["opName"]]
         
        self.applicableInIParent = {}
        self.transitions = set()
        
        #already public projections
        self.pre = {}
        if "pre" in data:
            for var in data["pre"]:
                self.pre[int(var)] = data["pre"][var]
            
        self.eff = {}
        if "eff" in data:
            for var in data["eff"]:
                self.eff[int(var)] = data["eff"][var]
            
        self.label = str(self.pre)+"->"+str(self.eff)
        self.representative = data["opName"][:data["opName"].find(" ")] +"-"+ self.label
        self.hash = getOpHash(self)
        
    def process(self,op):
        self.opNames += op.opNames
        self.cost = min(self.cost,op.cost)
        
    def isApplicable(self,state):
        for var in self.pre:
            #print "   " + self.representative + " in " + state.short()
            #print "   pre:" + str(var) + ":" + str(self.pre[var])
            #print "   state:" + str(state.publicValues[var])
            if state.publicValues[var] != self.pre[var]:
                return False
        
        return True
    
    def setApplicableInIParent(self,fromID,toID):
        if not fromID in self.applicableInIParent:
            self.applicableInIParent[fromID] = set()
        self.applicableInIParent[fromID].add(toID)
    
    def isApplicableInIParent(self,stateHash):
        if stateHash in self.applicableInIParent:
            return True
        else:
            return False
        
    def getIParentStates(self):
        return self.applicableInIParent.keys()
    
    def getIChildrenStates(self,iparent):
        return self.applicableInIParent.get(iparent)
        
    def isInitApplicable(self,initStateIDs):
        ia = len(self.getIParentStates() & initStateIDs) != 0
        if ia and configuration["debug"]==True:
            print( str(self) + " is init-applicable because " + str(self.getIParentStates() & initStateIDs) + " are initial states")
        return ia
    
    def addTransition(self,transition):
        self.transitions.add(transition)
        
    def __repr__(self):
        return self.representative
    
    def short(self):
        return printSignature(self)
    
    def printSignature(self):
        return str(self.pre)+"->"+str(self.eff)+":"+str(self.cost)
    
    def printNames(self):
        return self.representative+":"+str(self.opNames)

    
    
### State ###
    
def getStateHash(state):
    return str(state.agentID)+":"+str(state.stateID)

class State:
    def __init__(self,data,varMap,order):
        self.order = order                  #denotes the order in which the state was received
        self.agentID = data["agentID"]      #ID of the owning agent (i.e., the agent with open list in which the state is)
        self.senderID = data["senderID"]    #ID of the agent who sent the state, -1 if not received
        self.stateID = data["stateID"]      #ID of the state in the owner's open list
        self.parentID = data["parentID"]    #ID of the parent state, -1 if received
        self.iparentID = data["iparentID"]    #ID of the i-parent state, -1 if not received or not available
        self.cost = data["cost"]            #g-value of the state
        self.heuristic = data["heuristic"]  #h-value of the state
        self.privateIDs = data["privateIDs"]#IDs of the private parts of the state for the respective agents
        self.values = data["values"]        #variable values public + private of the owner agent)
        self.context = data["context"]      #String description of the context (e.g., sent, received,...)
        
        self.hash = getStateHash(self)      #id used in hash tables
        
        self.iparentHash = "-1"             #id of the iparent
        if self.iparentID != -1:
            self.iparentHash = str(self.agentID)+":"+str(self.iparentID)
        
        self.publicValues = []              #filtered only public values
        self.privateValues = []             #filtered only private values
        
        self.iparentTransition = set()      #operators responsible from the transition from i-parent to this state
        self.successors = set()             #all states for which this state is i-parent
        
        self.pebdStates = set()
        
        self.variables = varMap.values()    #reference to all variables in the problem
        
        for var,val in enumerate(self.values):
            if composeVarHash(self.agentID,var,True) in varMap:
                self.privateValues.append(val)
            else:
                self.publicValues.append(val)
    
    def getIParentHash(self):
        return self.iparentHash
        
    def getIParentTransition(self):
        return self.iparentTransition
    
    def isReceived(self):
        return self.context == "received" or self.context == "goal-verified"
    
    def isReceivedFromAgent(self,agent):
        return self.context == "received" and self.senderID == agent
    
    def isInit(self):
        return self.context == "init"
    
    def isGoal(self):
        return self.context == "goal-verified"
    
    def isSent(self):
        return self.context == "sending"
           
    def isIParent(self,op,state,agent):
        if not op.isApplicable(state):
            #print ("not i-parent based on pre")
            return False
        
        for var in range(len(self.publicValues)):
            #print var.varID
            affected = var in op.eff
        
            if affected and self.publicValues[var] != op.eff[var]:
                return False
            elif not affected and self.publicValues[var] != state.publicValues[var]:
                return False
            
        op.setApplicableInIParent(state.hash,self.hash)
        op.addTransition(self.hash)
        self.iparentTransition.add(op.hash)
        state.successors.add(self.hash)
        return True
    
    #Proposition 5, Proposition 6
    def isDistinctBasedOnHeuristic(self,state,agent):
        if configuration["projectedHeuristic"]==False and self.privateValues != state.privateValues:
            return False
        
        if self.heuristic != state.heuristic:
            return True
        
        return False
    
    #Proposition 7
    def isDistinctBasedOnSuccessors(self,state,agent):
        if(self.cost >= configuration["maxG"] or state.cost >= configuration["maxG"]):
            return False
        
        if len(self.successors) != len(state.successors):
            return True
        
        #TODO: can we do something more than just sizes?
#         if self.successors == state.successors:
#             return False
        
        return False
    
    #Proposition 8
    def isDistinctBasedOnPrivateIDs(self,state,agent):
        if configuration["useStateIDs"]==True or configuration["nTo1Mapping"] > 1:
            return False
        
        if self.privateIDs == state.privateIDs:
            return False
        
        return True
    
    #Proposition 9 (TODO:check if True)
    def isDistinctBasedOnCost(self,state,agent):
        if self.getIParentHash() == state.getIParentHash():
            if self.cost != state.cost:
                return True
        
        return False
    
    
    def isDistinct(self,state,agent):
        if self.hash == state.hash:
            return False
        
        if self.hash in state.pebdStates or state.hash in self.pebdStates:
            return True
        
        if self.isDistinctBasedOnHeuristic(state,agent):
            if configuration["debug"]:
                print(state.hash+" pebd " + self.hash + " based on heuristic")
            self.pebdStates.add(state.hash)
            state.pebdStates.add(self.hash)
            return True
        
        if self.isDistinctBasedOnSuccessors(state,agent):
            if configuration["debug"]:
                print(state.hash+" pebd " + self.hash + " based on successors")
            self.pebdStates.add(state.hash)
            state.pebdStates.add(self.hash)
            return True
        
        if self.isDistinctBasedOnPrivateIDs(state,agent):
            if configuration["debug"]:
                print(state.hash+" pebd " + self.hash + " based on private IDs")
            self.pebdStates.add(state.hash)
            state.pebdStates.add(self.hash)
            return True
        
#         if self.isDistinctBasedOnCost(state,agent):
#             if configuration["debug"]:
#                 print(state.hash+" pebd " + self.hash + " based on cost")
#             self.pebdStates.add(state.hash)
#             state.pebdStates.add(self.hash)
#             return True
        
        return False
    
    def getSecureMAFSSignature(self):
        priv = list(self.privateIDs)
        if self.senderID != -1:
            del priv[self.senderID]
        return str(self.publicValues)+":"+str(priv)
        
    def __repr__(self):
        return self.short()
            
    def short(self):
        return str(self.hash)+":"+str(self.publicValues)+","+str(self.privateValues)+","+str(self.privateIDs)#+":"+str(self.cost)
    
    def printAll(self):
        return "{\n\tagentID="+str(self.agentID)+"\n" \
            + "\tsenderID="+str(self.senderID)+"\n" \
            + "\tstateID="+str(self.stateID)+"\n" \
            + "\tparentID="+str(self.parentID)+"\n" \
            + "\tiparentID="+str(self.iparentID)+"\n" \
            + "\tprivateIDs="+str(self.privateIDs)+"\n" \
            + "\tpublicValues="+str(self.publicValues)+"\n" \
            + "\tprivateValues="+str(self.privateValues)+"\n" \
            + "\tcontext="+str(self.context)+"\n}"
    
    def printStateDotLabel(self):
        return "<" \
            + str(self.hash) \
            + ":<font color = \"red\">" + str(self.publicValues) + "</font>," \
            + str(self.privateValues) +","\
            + "g="+str(self.cost)+","\
            + "h="+str(self.heuristic)+","\
            + "iparent="+str(self.iparentID)+","\
            + "privIDs="+str(self.privateIDs)\
            + ">"
            
            
            

        
    
### LP ###

class Constraint:
    def __init__(self,coefs,vars,sign,RS):
        self.coefs = coefs
        self.vars = vars
        self.sign = sign
        self.RS = RS
        
        self.text = self.printConstraint()
        
    def __repr__(self):
        return self.text
    
    def __hash__(self):
        return hash(self.text)
    
    def __eq__(self,other):
        return self.text == other.text
        
    def printConstraint(self):
        out = ""
        for i in range(len(self.coefs)):
            if i != 0:
                out += " + "
            if self.coefs[i] != 1:
                out += str(self.coefs[i])
            out += str(self.vars[i])
        out += " " + str(self.sign) + " " + str(self.RS)
        return out
    
class DisjunctiveConstraint:
    def __init__(self):
        self.constraints = []
        self.text = "Empty"
        
    def addConstraint(self,constr):
        self.constraints.append(constr)
        self.text = self.printConstraint()
   
    def __repr__(self):
        return self.text
    
    #WARNING: hash depends on mutable element, bad bad practice
    def __hash__(self):
        return hash(self.text)
    
    def __eq__(self,other):
        return self.text == other.text
        
    def printConstraint(self):
        out = str(self.constraints[0])
        for c in self.constraints[1:]:
            out += " v " + str(c)
        return out

class LP:
    def __init__(self,config):
        self.varID = 0
        self.varMap = {}
        self.origMap = {}
        self.lpVarMap = {}
        self.constraints = set()
        
        
    def getVar(self,obj):
        if obj in self.origMap:
            return self.origMap[obj]
        
        varName = "V"+str(self.varID)
        self.varID += 1
        
        self.varMap[varName] = obj
        self.origMap[obj] = varName
        
        return varName
    
    def addLPVar(self,varName,lpVar):
        self.lpVarMap[varName] = lpVar
        
        
    def addDisjunctiveConstraint(self,ops,RS):
        dc = DisjunctiveConstraint()
        
        for op in ops:
            var = self.getVar(op)
            c = Constraint([1],[var]," <= ",RS)
            dc.addConstraint(c)
        
        self.constraints.add(dc)
        
    def addDisjunctiveConstraint2(self,ops1,RS1,ops2,RS2):
        dc = DisjunctiveConstraint()
        
        for op in ops1:
            var = self.getVar(op)
            c = Constraint([1],[var]," <= ",RS1)
            dc.addConstraint(c)
            
        for op in ops2:
            var = self.getVar(op)
            c = Constraint([1],[var]," <= ",RS2)
            dc.addConstraint(c)
        
        self.constraints.add(dc)
        
    def addDisjunctiveConstraint3(self,ops1,RS1,ops2,RS2,ops3,RS3):
        dc = DisjunctiveConstraint()
        
        for op in ops1:
            var = self.getVar(op)
            c = Constraint([1],[var]," <= ",RS1)
            dc.addConstraint(c)
            
        for op in ops2:
            var = self.getVar(op)
            c = Constraint([1],[var]," <= ",RS2)
            dc.addConstraint(c)
            
        for op in ops3:
            var = self.getVar(op)
            c = Constraint([1],[var]," <= ",RS3)
            dc.addConstraint(c)
        
        self.constraints.add(dc)
        
    def __repr__(self):
        return str(self.constraints)