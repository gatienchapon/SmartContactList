import pandas as pd
import os
import distutils.dir_util
from sklearn.ensemble import RandomForestClassifier


class randomForest:
    def __init__(self, path):
        self.random_forest = RandomForestClassifier(n_estimators=100)
        self.path = path
        self.train = pd.read_csv(path+'/train.txt')
        self.classConcatTimeSlot = dict()
        self.classFind = set()
        self.correspondance = []
        self.testComplet = []
        self.test = pd.read_csv(path+'/testFile.txt')

    def fillCorrespondanceFile(self):
        allSlotTime = [6,608,810,1012,1214,1416,1618,1820,2022,2200]
        for index, row in self.train.iterrows():
            currentTrainSlotTime = row['SlotTime']
            currentClass = row['Class']
            concat = currentClass+str(currentTrainSlotTime)
            self.classFind.add(currentClass)
            if( self.classConcatTimeSlot.has_key(concat)):
                value =  self.classConcatTimeSlot.get(concat)
                self.classConcatTimeSlot[concat]= value+1
            else:
                self.classConcatTimeSlot[concat]=1
        for contact in  self.classFind:
            line ={}
            line['Class'] = contact
            for timeSlot in allSlotTime:
                key = contact+str(timeSlot)
                if( self.classConcatTimeSlot.has_key(key)):
                    line[str(timeSlot)] =  self.classConcatTimeSlot.get(key)
                else:
                    line[str(timeSlot)] = 0
            self.correspondance.append(line)
        df = pd.DataFrame(data = self.correspondance)
        df.to_csv(self.path+'/correspondance.txt',index=False)

    def classify(self):
        self.fillCorrespondanceFile()
        learning = []

        for index, row in self.train.iterrows():
            line = row
            line.pop('Class')
            slottime_target = row['SlotTime']
            for contact in  self.classFind:
                key = contact+str(slottime_target)
                if( self.classConcatTimeSlot.has_key(key)):
                    line[contact] =  self.classConcatTimeSlot.get(key)
                else:
                    line[contact] = 0
            learning.append(line)
        df = pd.DataFrame(data = learning)
        df.to_csv(self.path+"/learningSet.txt",index=False)
        allClass = self.train['Class']
        self.random_forest.fit(learning, allClass)

    def fillTestFile(self):
        hours = {'6':'00','608':'06','810':'08','1012':'10','1214':'12','1416':'14','1618':'16','1820':'18','2022':'22','2200':'22'}
        allSlotTimes = [6,608,810,1012,1214,1416,1618,1820,2022,2200]
        slottime_target = self.test['SlotTime'][0]
        for current_slot_time in allSlotTimes:
            if slottime_target != current_slot_time:
                newLine = {}
                for t in self.test:
                    if t != 'SlotTime' and t !='Hour':
                        newLine[str(t)] = self.test[str(t)][0]
                newLine['SlotTime'] = current_slot_time
                newLine['Hour'] = hours[str(current_slot_time)]
                self.test =self.test.append(newLine, ignore_index=True)

        for index, row in self.test.iterrows():
            line = row
            slottime_target = row['SlotTime']
            for contact in self.classFind:
                key = contact+str(slottime_target)
                if( self.classConcatTimeSlot.has_key(key)):
                    line[contact] =  self.classConcatTimeSlot.get(key)
                else:
                    line[contact] = 0
            self.testComplet.append(line)
        df = pd.DataFrame(data = self.testComplet)
        df.to_csv(self.path+"/testComplet.txt",index=False)

    def predicte(self):
        self.fillTestFile()
        resultatFinal = {}
        self.testComplet = pd.read_csv(self.path+'/testComplet.txt')
        self.correspondance = pd.read_csv(self.path+'/correspondance.txt')
        for index, row in self.testComplet.iterrows():
            currentSlotTime = row['SlotTime']
            row = row.reshape(1,-1)
            Y_pred = self.random_forest.predict_proba(row)
            names = self.correspondance['Class']
            i =0
            resultatFinal[str(currentSlotTime)] = []
            for proba in Y_pred[0]:
                if(proba != 0):
                    resultatFinal[str(currentSlotTime)].append({"name":names[i],"score":proba})
                i=i+1
        return resultatFinal

rand = randomForest("dataset/greg")
rand.classify()
rand.predicte()