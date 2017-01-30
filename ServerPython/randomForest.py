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


def fillTestFile(test, path, correspondance):

    id_Contact = correspondance['numero']
    initAllContactNbCallBySlotTime(test, id_Contact)

    slottime_target = test['SlotTime'][0]
    allSlotTimes = [6,608,810,1012,1214,1416,1618,1820,2022,2200]
    hours = {'6':'00','608':'06','810':'08','1012':'10','1214':'12','1416':'14','1618':'16','1820':'18','2022':'22','2200':'22'}
    j=0
    for contact_current in id_Contact:
        test[str(contact_current)][0] = correspondance[str(slottime_target)][j]
        j=j+1
    for current_slot_time in allSlotTimes:
        if slottime_target != current_slot_time:
            line = {}
            for t in test:
                if t != 'SlotTime' and t !='Hour':
                    line[str(t)] = test[str(t)][0]
            line['SlotTime'] = current_slot_time
            line['Hour'] = hours[str(current_slot_time)]
            j =0
            for contact_current in id_Contact:
                line[str(contact_current)] = correspondance[str(current_slot_time)][j]
                j=j+1
            test =test.append(line, ignore_index=True)
    return test

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
				train[str(current)][str(i)]= nbCall
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
		correspondance = pd.read_csv(path+'/correspondance.txt')
		test = fillTestFile(test, path, correspondance)
		resultatFinal = {}
		for index, row in test.iterrows():
			currentSlotTime = row['SlotTime']
			row = row.reshape(1,-1)
			Y_pred = self.random_forest.predict_proba(row)
			names = correspondance['name']
			i =0
			resultatFinal[str(currentSlotTime)] = []
			for proba in Y_pred[0]:
				if(proba != 0):
					resultatFinal[str(currentSlotTime)].append({"name":names[i],"score":proba})
				i=i+1
		df = pd.DataFrame(data = test)
		df.to_csv(path+'/testFile.txt',index=False)
		#print resultatFinal
		return resultatFinal

