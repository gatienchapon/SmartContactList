import pandas as pd
from sklearn.ensemble import RandomForestClassifier


def getNbCallForAContact(numeroCurrent, corespondant):
    for contact in corespondant:
        if contact['numero'] == numeroCurrent:
            return contact['nbCall']
    pass


class randomForest:
    def __init__(self):
        self.random_forest = RandomForestClassifier(n_estimators=100)

    def classify(self):
        fileToRead = 'dataset/gregHistory.txt'
        train = pd.read_csv(fileToRead)
        allClass = train['Class']
        uniqueClassList = list(set(allClass))
        corespondant = []
        i=1
        for unique in uniqueClassList:
            corespondant.append({"numero":i, "name":unique})
            allClass = [ i if x == unique else x for x in allClass ]
            i=i+1
        i=0
        for contact in corespondant:
            count = allClass.count(contact['numero'])
            corespondant[i]['nbCall'] = count
            i=i+1
        i=0
        #train['nbCall']=0
        for current in allClass:
            nbCall = getNbCallForAContact(current,corespondant)
            #train['nbCall'][i]= nbCall
            i=i+1

        df = pd.DataFrame(data = train)
        df.to_csv(fileToRead,index=False)
        df = pd.DataFrame(data = corespondant)
        df.to_csv('dataset/correspondance.txt',index=False)

        train.drop(['Class'], axis=1, inplace=True)

        self.random_forest.fit(train, allClass)

    def predicte(self, textFile):
        LocationTest = textFile
        test = pd.read_csv(LocationTest)
        Y_pred = self.random_forest.predict_proba(test)
        resultatFinal = []
        df = pd.read_csv(r'dataset/correspondance.txt')
        names = df['name']
        df.to_csv('dataset/correspondance.txt',index=False)
        i =0
        for proba in Y_pred[0]:
            resultatFinal.append({"name":names[i],"score":proba})
            i=i+1
        return resultatFinal



