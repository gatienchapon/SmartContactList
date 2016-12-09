import pandas as pd
from sklearn.ensemble import RandomForestClassifier

class randomForest:
    def __init__(self):
        self.random_forest = RandomForestClassifier(n_estimators=100)

    def classify(self):
        LocationLearning = r'dataset/gregHistory.txt'
        train = pd.read_csv(LocationLearning)
        allClass = train['Class']
        uniqueClassList = list(set(allClass))
        corespondant = []
        i=1
        for unique in uniqueClassList:
            corespondant.append({"numero":i, "name":unique})
            allClass = [ i if x == unique else x for x in allClass ]
            i=i+1
        df = pd.DataFrame(data = corespondant)
        df.to_csv('dataset/correspondance.txt',index=False)
        train.drop(['Class'], axis=1, inplace=True)
        self.random_forest.fit(train, allClass)

    def predicte(self, textFile):
        LocationTest = textFile
        test = pd.read_csv(LocationTest, header=None)
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



