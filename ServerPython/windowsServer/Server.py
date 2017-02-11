#!flask/bin/python
from flask import Flask
from flask import request,jsonify
import pandas as pd
import distutils.dir_util
import netifaces as ni
from randomForest import randomForest
from UDP import UDP
from graphique import graphique
import os
import ctypes, sys
import win32com.shell.shell as shell
from tkinter import *

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
	print "On envoie le resultat"
	return json

@app.route("/")
def hello():
    return "Hello World!"
	

	
def is_admin():
    try:
        return ctypes.windll.shell32.IsUserAnAdmin()
    except:
        return False

Mafenetre = Tk()
	
if __name__ == '__main__':
	if not is_admin():
		my_text = StringVar()
		from_IP = StringVar()
		my_text.set("No information received")
		from_IP.set("")
		script = os.path.abspath(sys.argv[0])
		params = ' '.join([script] + sys.argv[1:] + ['asadmin'])
		shell.ShellExecuteEx(lpVerb='runas', lpFile=sys.executable, lpParameters=params)
		threadGraphique = graphique(my_text,Mafenetre,from_IP)
		threadGraphique.start()
		batcmd = 'netsh advfirewall firewall show rule name="SmartContactList rule TCP"| findstr "SmartContactList rule"'
		if  (os.system(batcmd) == 1):
			os.system('netsh advfirewall firewall add rule name="SmartContactList rule TCP" dir=in action=allow protocol=TCP localport=5000')
			os.system('netsh advfirewall firewall add rule name="SmartContactList rule UDP" dir=in action=allow protocol=UDP localport=5000')
		thredUdp = UDP(my_text, from_IP)
		thredUdp.start()
		app.run(
				host="0.0.0.0",
				port=int("5000")
		)
		
	