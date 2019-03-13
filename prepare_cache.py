
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


#compose the logic formula describing the properties of the operators
from sympy import *
    

#helper functions
def getPreSATVariable(op,variable,value):
    var = op+"preVar"+str(variable)+"Val"+str(value)
    sym = symbols(var)
    return sym

def getEffSATVariable(op,variable,value):
    var = op+"effVar"+str(variable)+"Val"+str(value)
    sym = symbols(var)
    return sym

def opInitApplicableInState(opHash):
    formula = True
    for var in range(0,agentPrivateVariables):
        sym = getPreSATVariable(opHash,var,0)
        formula = (formula & sym)
    return formula

def opPrivatelyIndependent(opHash,numOfStates):
    formula = False
    for var in range(0,agentPrivateVariables):
        subformula = True
        for val in range(0,agentDomainSize):
            sym = getPreSATVariable(opHash,var,val)
            subformula = (subformula & sym)
        formula = formula | subformula
    return formula

def opPrivatelyDependent(opHash,numOfStates):
    formula = False
    for var in range(0,agentPrivateVariables):
        subformula1 = True
        subformula2 = True
        for val in range(0,agentDomainSize):
            sym = getPreSATVariable(opHash,var,val)
            subformula1 = subformula1 | sym
            subformula2 = subformula2 | ~sym
        formula = formula | (subformula1 & subformula2)
    return formula

def opPrivatelyNondeterministic(opHash,numOfStates):
    formula = False
    for var in range(0,agentPrivateVariables):
        subformula = False
        for val1 in range(0,agentDomainSize):
            for val2 in range(0,agentDomainSize):
                if val1 != val2:
                    sym1 = getEffSATVariable(opHash,var,val1)
                    sym2 = getEffSATVariable(opHash,var,val2)
                    subformula = subformula | (sym1 & sym2)
        formula = formula | (subformula)
    return formula




# In[7]:


def computeFixedTS(k,l):
    
    sum = 0
    for j in range(k+1):
        sum += (-1)**j * binomial(k,j) * (2**(k-j) - 1)**l
    
#     print("compute all TS between "+str(k)+" pre and "+str(l)+" eff = " + str(sum))
    
    return sum

def computeAllTS(pfree,efree,ptrue,etrue):
    total = 0
    
    pmin = 0
    if ptrue==0:
        pmin=1
        
    emin = 0
    if etrue==0:
        emin=1
        
    for k in range(pmin,pfree+1):
        for l in range(emin,efree+1):
            if k+ptrue>0 and l+etrue >0:
                fixed = binomial(pfree,k) * binomial(efree,l) * computeFixedTS(k+ptrue,l+etrue)
                total += fixed
            
    return total


def evaluateModels(models):
    count = 0;
    maxTotalTS = 0
    
    for model in models:
#         print(model)
        count += 1
        opHash = 'op'

        opTS = 1
        subForEmptyTS = 1
        
        if model == True or model == False:
            return 1

        for var in range(0,agentPrivateVariables):

            freePre = agentDomainSize
            freeEff = agentDomainSize

            posPre = 0
            posEff = 0

            for val in range(0,agentDomainSize):
                preVar = getPreSATVariable(opHash,var,val)
                effVar = getEffSATVariable(opHash,var,val)

                if preVar in model:
                    freePre -= 1
                    if model[preVar] == True:
                        posPre += 1

                if effVar in model:
                    freeEff -=1
                    if model[effVar] == True:
                        posEff += 1

#                 print(opHash+",var="+str(var)+": pre:#"+str(freePre)+",+"+str(posPre))
#                 print(opHash+",var="+str(var)+": eff:#"+str(freeEff)+",+"+str(posEff))

            varTS = computeAllTS(freePre,freeEff,posPre,posEff)

            opTS = opTS * varTS

                
        totalTS = opTS
       
        if totalTS > maxTotalTS:
            maxTotalTS = totalTS
    
    print("Evaluated " + str(count) + " models")
    return maxTotalTS
            
#test:
print(computeAllTS(2,2,0,0))


# In[8]:


#determine RHS based on combinations of properties

#check cache:
cacheCSVFile = "ts_cache.csv"
outCSV = Path(cacheCSVFile)
exists = outCSV.is_file()
found = False


for agentPrivateVariables in range (1,5):
    for agentDomainSize in range(1,5):
        print("Compute: " + str(agentPrivateVariables)+","+str(agentDomainSize))
        
        found = False
        if exists:
            with open("ts_cache.csv") as csvfile:
                reader = csv.DictReader(csvfile)
                for row in reader:
                    if row['agentDomainSize']==str(agentDomainSize) and  row['agentPrivateVariables']==str(agentPrivateVariables):
                        print("Reading from cache file!")
                        print(row)
                        found = True

        if found:
            print("Found!")
            continue
            
        print("ts")
        ts = (2**(agentDomainSize**2) - 1)**agentPrivateVariables 
        print(ts)
        rhs_t = log(ts,2).evalf()

        print("ia")
        f_ia = opInitApplicableInState('op')
        print(f_ia)
        models_ia = satisfiable(f_ia,all_models=True)
        ts_ia = evaluateModels(models_ia)
        print(ts_ia)
        rhs_ia = log(ts_ia,2).evalf()

        print("nia")
        f_nia = ~opInitApplicableInState('op')
        print(f_nia)
        models_nia = satisfiable(f_nia,all_models=True)
        ts_nia = evaluateModels(models_nia)
        print(ts_nia)
        rhs_nia = log(ts_nia,2).evalf()

        print("pi")
        f_pi = opPrivatelyIndependent('op',1)
        print(f_pi)
        models_pi = satisfiable(f_pi,all_models=True)
        ts_pi = evaluateModels(models_pi)
        print(ts_pi)
        rhs_pi = log(ts_pi,2).evalf()

        print("pd")
        f_pd = opPrivatelyDependent('op',1)
        print(f_pd)
        models_pd = satisfiable(f_pd,all_models=True)
        ts_pd = evaluateModels(models_pd)
        print(ts_pd)
        rhs_pd = log(ts_pd,2).evalf()

        print("pn")
        f_pn = opPrivatelyNondeterministic('op',1)
        print(f_pn)
        models_pn = satisfiable(f_pn,all_models=True)
        ts_pn = evaluateModels(models_pn)
        print(ts_pn)
        rhs_pn = log(ts_pn,2).evalf()

        print("ia_pi")
        f_ia_pi = opInitApplicableInState('op') & opPrivatelyIndependent('op',1)
        print(f_ia_pi)
        models_ia_pi = satisfiable(f_ia_pi,all_models=True)
        ts_ia_pi = evaluateModels(models_ia_pi)
        print(ts_ia_pi)
        rhs_ia_pi = log(ts_ia_pi,2).evalf()

        print("nia_pi")
        f_nia_pi = ~opInitApplicableInState('op') & opPrivatelyIndependent('op',1)
        print(f_nia_pi)
        models_nia_pi = satisfiable(f_nia_pi,all_models=True)
        ts_nia_pi = evaluateModels(models_nia_pi)
        print(ts_nia_pi)
        rhs_nia_pi = log(ts_nia_pi,2).evalf()

        print("ia_pd")
        f_ia_pd = opInitApplicableInState('op') & opPrivatelyDependent('op',1)
        print(f_ia_pd)
        models_ia_pd = satisfiable(f_ia_pd,all_models=True)
        ts_ia_pd = evaluateModels(models_ia_pd)
        print(ts_ia_pd)
        rhs_ia_pd = log(ts_ia_pd,2).evalf()

        print("nia_pd")
        f_nia_pd = ~opInitApplicableInState('op') & opPrivatelyDependent('op',1)
        print(f_nia_pd)
        models_nia_pd = satisfiable(f_nia_pd,all_models=True)
        ts_nia_pd = evaluateModels(models_nia_pd)
        print(ts_nia_pd)
        rhs_nia_pd = log(ts_nia_pd,2).evalf()

        print("ia_pn")
        f_ia_pn = opInitApplicableInState('op') & opPrivatelyNondeterministic('op',1)
        print(f_ia_pn)
        models_ia_pn = satisfiable(f_ia_pn,all_models=True)
        ts_ia_pn = evaluateModels(models_ia_pn)
        print(ts_ia_pn)
        rhs_ia_pn = log(ts_ia_pn,2).evalf()

        print("nia_pn")
        f_nia_pn = ~opInitApplicableInState('op') & opPrivatelyNondeterministic('op',1)
        print(f_nia_pn)
        models_nia_pn = satisfiable(f_nia_pn,all_models=True)
        ts_nia_pn = evaluateModels(models_nia_pn)
        print(ts_nia_pn)
        rhs_nia_pn = log(ts_nia_pn,2).evalf()

        print("pi_pn")
        f_pi_pn = opPrivatelyIndependent('op',1) & opPrivatelyNondeterministic('op',1)
        print(f_pi_pn)
        models_pi_pn = satisfiable(f_pi_pn,all_models=True)
        ts_pi_pn = evaluateModels(models_pi_pn)
        print(ts_pi_pn)
        rhs_pi_pn = log(ts_pi_pn,2).evalf()

        print("pd_pn")
        f_pd_pn = opPrivatelyDependent('op',1) & opPrivatelyNondeterministic('op',1)
        print(f_pd_pn)
        models_pd_pn = satisfiable(f_pd_pn,all_models=True)
        ts_pd_pn = evaluateModels(models_pd_pn)
        print(ts_pd_pn)
        rhs_pd_pn = log(ts_pd_pn,2).evalf()

        print("ia_pi_pn")
        f_ia_pi_pn = opInitApplicableInState('op') & opPrivatelyIndependent('op',1) & opPrivatelyNondeterministic('op',1)
        print(f_ia_pi_pn)
        models_ia_pi_pn = satisfiable(f_ia_pi_pn,all_models=True)
        ts_ia_pi_pn = evaluateModels(models_ia_pi_pn)
        print(ts_ia_pi_pn)
        rhs_ia_pi_pn = log(ts_ia_pi_pn,2).evalf()

        print("nia_pi_pn")
        f_nia_pi_pn = ~opInitApplicableInState('op') & opPrivatelyIndependent('op',1) & opPrivatelyNondeterministic('op',1)
        print(f_nia_pi_pn)
        models_nia_pi_pn = satisfiable(f_nia_pi_pn,all_models=True)
        ts_nia_pi_pn = evaluateModels(models_nia_pi_pn)
        print(ts_nia_pi_pn)
        rhs_nia_pi_pn = log(ts_nia_pi_pn,2).evalf()

        print("ia_pd_pn")
        f_ia_pd_pn = opInitApplicableInState('op') & opPrivatelyDependent('op',1) & opPrivatelyNondeterministic('op',1)
        print(f_ia_pd_pn)
        models_ia_pd_pn = satisfiable(f_ia_pd_pn,all_models=True)
        ts_ia_pd_pn = evaluateModels(models_ia_pd_pn)
        print(ts_ia_pd_pn)
        rhs_ia_pd_pn = log(ts_ia_pd_pn,2).evalf()

        print("nia_pd_pn")
        f_nia_pd_pn = ~opInitApplicableInState('op') & opPrivatelyDependent('op',1) & opPrivatelyNondeterministic('op',1)
        print(f_nia_pd_pn)
        models_nia_pd_pn = satisfiable(f_nia_pd_pn,all_models=True)
        ts_nia_pd_pn = evaluateModels(models_nia_pd_pn)
        print(ts_nia_pd_pn)
        rhs_nia_pd_pn = log(ts_nia_pd_pn,2).evalf()
        
        columns=[
            'agentPrivateVariables',
            'agentDomainSize',
            'ts',
            'ia',
            'nia',
            'pi',
            'pd',
            'pn',
            'ia_pi',
            'ia_pd',
            'ia_pn',
            'nia_pi',
            'nia_pd',
            'nia_pn',
            'pi_pn',
            'pd_pn',
            'ia_pi_pn',
            'ia_pd_pn',
            'nia_pi_pn',
            'nia_pd_pn'
        ]

        outCSV = Path(cacheCSVFile)
        exists = outCSV.is_file()

        row = [
            agentPrivateVariables,
            agentDomainSize,
            rhs_t,
            rhs_ia,
            rhs_nia,
            rhs_pi,
            rhs_pd,
            rhs_pn,
            rhs_ia_pi,
            rhs_ia_pd,
            rhs_ia_pn,
            rhs_nia_pi,
            rhs_nia_pd,
            rhs_nia_pn,
            rhs_pi_pn,
            rhs_pd_pn,
            rhs_ia_pi_pn,
            rhs_ia_pd_pn,
            rhs_nia_pi_pn,
            rhs_nia_pd_pn
        ]


        with open(cacheCSVFile, 'a') as f:
            writer = csv.writer(f)
            if not exists:
                writer.writerow(columns)
            writer.writerow(row)




# In[16]:



    

