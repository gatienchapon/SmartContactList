#!flask/bin/python
from flask import Flask
from flask import request,jsonify
import pandas as pd
import time
import json
import distutils.dir_util
import netifaces as ni

from randomForest import randomForest

app = Flask(__name__)
Classifier = randomForest()

@app.route('/call/', methods=['POST'])
def add_call():
    ip = request.remote_addr
    path = 'dataset/'+ip +'_folder'
    distutils.dir_util.mkpath(path)
    content = request.json
    content = content['history']
    df = pd.DataFrame(data = content)
    df.to_csv(path+'/test.txt',index=False)
    Classifier.classify(path)
    return jsonify({"retour":"super"})

@app.route('/predict/', methods=['POST'])
def get_tasks():
    ip = request.remote_addr
    path = 'dataset/'+ip +'_folder'
    distutils.dir_util.mkpath(path)
    content = request.json
    content = content['request']
    df = pd.DataFrame(data = content)
    df.to_csv(path+'/testFile.txt',index=False)
    return jsonify(Classifier.predicte(path))

if __name__ == '__main__':
    if 2 in ni.ifaddresses('wlan0'):
        print "wlan0 IP : %s" % ni.ifaddresses('wlan0')[2][0]['addr']
    if 2 in ni.ifaddresses('eth0'):
        print "eth0 IP : %s" % ni.ifaddresses('eth0')[2][0]['addr']
    app.run(
            host="0.0.0.0",
            port=int("5000")
    )