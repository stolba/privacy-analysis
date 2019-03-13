import functools


configuration = {
    "privateActions":True,
    "useFullStates":1,
    "nTo1Mapping":1,
    "truePrivIDs":False
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
        if ia:
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
        self.cost = data["cost"]            #g-value of the state
        self.heuristic = data["heuristic"]  #h-value of the state
        self.privateIDs = data["privateIDs"]#IDs of the private parts of the state for the respective agents
        self.values = data["values"]        #variable values public + private of the owner agent)
        self.context = data["context"]      #String description of the context (e.g., sent, received,...)
        
        self.hash = getStateHash(self)    #id used in hash tables
        
        self.publicValues = []              #filtered only public values
        self.privateValues = []             #filtered only private values
        
        self.iParents = []
        self.pebdStates = set()
        
        self.variables = varMap.values()    #reference to all variables in the problem
        
        for var,val in enumerate(self.values):
            if composeVarHash(self.agentID,var,True) in varMap:
                self.privateValues.append(val)
            else:
                self.publicValues.append(val)
                
    def isReceived(self):
        return self.parentID == -1 and self.senderID != -1
    
    def isReceivedFromAgent(self,agent):
        return self.context == "received" and self.senderID == agent
    
    def isInit(self):
        return self.context == "init"
    
    def isSent(self):
        return self.context == "sending"
                
    def isIParentBasedOnCost(self,op,state):            
        #WARNING: works only when assuming public actions only or unit costs!!
        if self.cost - state.cost == op.cost:
            return True
        else:
            #print ("not i-parent based on cost: " + str(self.cost)+" - "+str(state.cost)+" == "+str(op.cost))
            return False
        
    def isIParentBasedOnPreEff(self,op,state):
        if not op.isApplicable(state):
            #print ("not i-parent based on pre")
            return False
        
        #print op["opName"] + " in " + str(s1["stateID"]) + "->" + str(s2["stateID"])
        for var in range(len(self.publicValues)):
            #print var.varID
            affected = var in op.eff
        
            if affected and self.publicValues[var] != op.eff[var]:
                #print "! affected variable " + str(var.varID) + " does not equal in effect and state " + self.short()
                #print ("not i-parent based on eff")
                return False
            elif not affected and self.publicValues[var] != state.publicValues[var]:
                #print "! non-affected variable " + str(var.varID) + " does not equal in state "+str(state.values)+" and state " +str(self.values)
                #print ("not i-parent based on eff")
                return False
        return True
            
    def isIParent(self,op,state,agent):
#         if configuration["useFullStates"] == 1:
#             if self.senderID == -1:
#                 return False
#             if not self.privateIDs[agent] == state.stateID:
#                 op.setApplicableInIParent(state.hash,self.hash)
#                 return False #it is definitely not i-parent due to the known i-parent id
            
        if configuration["privateActions"] == False and not self.isIParentBasedOnCost(op,state):
            return False
        
        iparent = self.isIParentBasedOnPreEff(op,state)
        if iparent:
            op.setApplicableInIParent(state.hash,self.hash)
        return iparent
    
    def addIParent(self,state,operators):
        self.iParents.append({
                    "iparentID":state.hash,
                    "applicable":operators
                })
        
    def isPubliclyEquivalentButDistinct(self,state,agent):
        #if state.hash in self.pebdStates:
        #    return True
        if self.hash == state.hash:
            return False
        
        #is publicly equivalent?
        pubeq = self.publicValues == state.publicValues
        
        distinct = this.isDistinct(self,state,agent)
        
        #finalize
        if pubeq and distinct:  
            self.pebdStates.add(state.hash)
            return True
        return False
    
    def isDistinct(self,state,agent):
        if self.hash == state.hash:
            return False
        
        #do private values match?
        privateValuesMatch = self.privateValues == state.privateValues
        
#         #does private ID of the agent match?
#         #TODO: this will get more complicated with n->1 maping
#         #This actually does not say anything! there could be state which is equal but with different private IDs!
#         if configuration["truePrivIDs"] != True:
#             #TODO: this should be more involved
#             privateIdMatch = True
#         else:
#             privateIdMatch = self.privateIDs[agent] == state.privateIDs[agent]
        
        #does g and h match?
        #we care only if the private values match because otherwise the difference might be caused by that!
        if privateValuesMatch:
            hgMatch = self.cost == state.cost and self.heuristic == state.heuristic
        else:
            hgMatch = True
        
        #finalize
        if not hgMatch:  
            self.pebdStates.add(state.hash)
            return True
        
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
            + "privIDs="+str(self.privateIDs)\
            + ">"
            
            
            
### Transition ###

class Transition:
    def __init__(self,toState):
        self.toState = toState.hash
        self.fromStates = {}
        
    def addSubTransition(self,fromState,ops):
        if not fromState.hash in self.fromStates:
            self.fromStates[fromState.hash] = set()
            
        for op in ops:
            self.fromStates[fromState.hash].add(op.hash)
            
    def getFromStates(self):
        return set(self.fromStates.keys())
    
    def getAllOpsIntersection(self):
        return functools.reduce(lambda x,y: x & y, self.fromStates.values())
    
    def getIparentCount(self):
        return len(self.fromStates)
            
        
    def __repr__(self):
        out = "{\n"
        for fs in self.fromStates:
            out += "  " + str(fs)+" - " + str(self.fromStates[fs]) + " -> \n"
        out += "  " + str(self.toState) + "}"
        return out
    
    
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
        
    def __repr__(self):
        return str(self.constraints)