#!flask/bin/python
from flask import Flask
from flask import request,jsonify
import pandas as pd
import time
import json

from randomForest import randomForest

app = Flask(__name__)
Classifier = randomForest()

@app.route('/call/', methods=['POST'])
def add_call():
    content = request.json

    content = content['history']
    df = pd.DataFrame(data = content)
    df.to_csv('dataset/test.txt',index=False)
    Classifier.classify()
    return jsonify({"retour":"super"})

@app.route('/predict', methods=['GET'])
def get_tasks():
    return jsonify(Classifier.predicte('dataset/testFile.txt'))

if __name__ == '__main__':
    app.run(
            host="192.168.0.41",
            port=int("5000")
    )