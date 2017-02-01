#!flask/bin/python
from flask import Flask
from flask import request,jsonify
import pandas as pd
import distutils.dir_util
import netifaces as ni
from randomForest import randomForest

app = Flask(__name__)


@app.route('/call/', methods=['POST'])
def add_call():
    ip = request.remote_addr
    path = 'dataset/'+ip +'_folder'
    distutils.dir_util.mkpath(path)
    content = request.json
    content = content['history']
    df = pd.DataFrame(data = content)
    df.to_csv(path+'/train.txt',index=False)
    Classifier = randomForest()
    Classifier.classify(path)
    return jsonify({"retour":"super"})

@app.route('/predict/', methods=['POST'])
def get_tasks():
    content = request.json
    history = content['history']
    id_phone = content['id_phone']
    path = 'dataset/'+id_phone +'_folder'
    distutils.dir_util.mkpath(path)
    if history[0]['Year'] != 'empty':
        df = pd.DataFrame(data = history)
        df.to_csv(path+'/train.txt',index=False)

    else:
        print("Erreur Json");
        #Classifier.classify("dataset/192.168.0.37_folder")
    requestContent = content['request']
    df = pd.DataFrame(data = requestContent)
    df.to_csv(path+'/testFile.txt',index=False)
    Classifier = randomForest(path)
    Classifier.classify()
    json = jsonify(Classifier.predicte())
    distutils.dir_util.remove_tree(path)
    return json

if __name__ == '__main__':
    #Classifier.classify("dataset/192.168.0.37_folder")
    #thredUdp = UDP()
    #thredUdp.start()
    if 2 in ni.ifaddresses('wlan0'):
        print "wlan0 IP : %s" % ni.ifaddresses('wlan0')[2][0]['addr']
    if 2 in ni.ifaddresses('eth0'):
        print "eth0 IP : %s" % ni.ifaddresses('eth0')[2][0]['addr']
    app.run(
            host="0.0.0.0",
            port=int("5000")
    )