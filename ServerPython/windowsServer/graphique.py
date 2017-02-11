import socket, traceback
from threading import Thread
from tkinter import *


class graphique(Thread):

	def __init__(self, my_text, root, from_IP):
		Thread.__init__(self)
		self.my_text = my_text
		self.Mafenetre = root
		self.from_IP = from_IP
	def on_closing():
		exit(0)
		Mafenetre.destroy()
	def load_graphique(self): 
		self.Mafenetre.resizable(0,0)
		self.Mafenetre.title('Server')
		self.Mafenetre['bg']='bisque'
		Frame1 = Frame(self.Mafenetre,borderwidth=2,relief=GROOVE)
		Frame1.pack(side=LEFT,padx=10,pady=10)
		Label(Frame1,text="Smart Contact List").pack(padx=10,pady=10)
		Frame2 = Frame(self.Mafenetre,borderwidth=2,relief=GROOVE)
		Frame2.pack(side=LEFT,padx=10,pady=10)
		Label(Frame2,text="Waiting for any connection ...").pack(padx=10,pady=10)
		Label(Frame2,textvariable=self.my_text,fg='navy').pack(padx=10,pady=10)
		Label(Frame2,textvariable=self.from_IP,fg='navy').pack(padx=10,pady=10)
		self.Mafenetre.mainloop()
	def run(self):
		self.load_graphique()
		