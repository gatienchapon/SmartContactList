import socket, traceback
from threading import Thread


class UDP(Thread):

    def __init__(self):
        Thread.__init__(self)

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
                print "Got data from", address
                s.sendto("OKOK", address)

            except (KeyboardInterrupt, SystemExit):
                raise
            except:
                traceback.print_exc()
