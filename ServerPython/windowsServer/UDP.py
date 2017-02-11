import socket, traceback
from threading import Thread
from time import gmtime, strftime

class UDP(Thread):

    def __init__(self, my_text, from_IP):
		Thread.__init__(self)
		self.my_text = my_text
		self.from_IP = from_IP

    def run(self):
		print "We listen UDP"
		host = ''                               # Bind to all interfaces
		port = 6000
		s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
		s.bind((host, port))
		while 1:
			try:
				message, address = s.recvfrom(8192)
				self.my_text.set('Last Connection at ' + strftime("%Hh%M", gmtime()))
				self.from_IP.set(address)
				print "Got data from", address
				s.sendto("OKOK", address)

			except (KeyboardInterrupt, SystemExit):
				raise
			except:
				traceback.print_exc()