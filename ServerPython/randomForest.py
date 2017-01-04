import pandas as pd
import os
import distutils.dir_util
from sklearn.ensemble import RandomForestClassifier


def getNbCallForAContactForAGivenSlotTime(currentClass, corespondant, slotTimeCurrent):
    for contact in corespondant:
        if contact['numero'] == currentClass:
            return contact[slotTimeCurrent]

def count_for_a_slottime(allSlots, allClass, numero, currentSlotTime):
    i=0
    result = 0
    for one_slot in allSlots:
        if one_slot == currentSlotTime:
            if allClass[i] == numero:
                result = result + 1
        i = i+1
    return result


def initAllContactNbCallBySlotTime(train, uniqueClassList):
    for uniqueCall in uniqueClassList:
        train[str(uniqueCall)] =0





class randomForest:
    def __init__(self):
        self.random_forest = RandomForestClassifier(n_estimators=100)

    def classify(self, path):
        fileToRead = path+'/test.txt'
        train = pd.read_csv(fileToRead)
        allClass = train['Class']
        uniqueClassList = list(set(allClass))
        allSlotTime = [6,608,810,1012,1214,1416,1618,1820,2022,2200]
        corespondant = []
        i=1
        for unique in uniqueClassList:
            corespondant.append({"numero":i, "name":unique})
            allClass = [ i if x == unique else x for x in allClass ]
            i=i+1
        i=0
        for contact in corespondant:
            for currentSlotTime in allSlotTime:
                count = count_for_a_slottime(train['SlotTime'],allClass,contact['numero'],currentSlotTime)
                corespondant[i][currentSlotTime] = count
            i=i+1
        uniqueClassList = list(set(allClass))
        i=0
        initAllContactNbCallBySlotTime(train, uniqueClassList)
        for current_line in train['SlotTime']:
            for current in allClass:
                nbCall = getNbCallForAContactForAGivenSlotTime(current,corespondant, current_line)
                train[str(current)][i]= nbCall
            i=i+1

        df = pd.DataFrame(data = train)
        df.to_csv(fileToRead,index=False)
        df = pd.DataFrame(data = corespondant)
        df.to_csv(path+'/correspondance.txt',index=False)

        train.drop(['Class'], axis=1, inplace=True)

        self.random_forest.fit(train, allClass)

    def predicte(self, path):
        LocationTest = path+'/testFile.txt'
        test = pd.read_csv(LocationTest)
        df = pd.read_csv(path+'/correspondance.txt')
        id_Contact = df['numero']
        initAllContactNbCallBySlotTime(test, id_Contact)
        i =0
        for contact_current in id_Contact:
            slottime_target = test['SlotTime'][0]
            test[str(contact_current)][0] = df[str(slottime_target)][i]
            i=i+1
        print test
        Y_pred = self.random_forest.predict_proba(test)
        resultatFinal = []

        names = df['name']
        i =0
        for proba in Y_pred[0]:
            resultatFinal.append({"name":names[i],"score":proba})
            i=i+1
        df = pd.DataFrame(data = test)
        df.to_csv(path+'/testFile.txt',index=False)
        return resultatFinal


