import zmq
import sys
import getopt
from time import sleep
def main(argv):
	num = None
	identity = None
	try:
		opts, args = getopt.getopt(argv[1:],'n:i:' )
	except getopt.GetoptError, err:
		print str(err)
		exit(1)
	for o, a in opts:
		if o in ('-n',):
			num = int(a)
		elif o in ('-i'):
			identity = a
	if num == None:
		print 'error'
		exit(1)
	
	context = zmq.Context()
	dealer = context.socket(zmq.DEALER)
	dealer.connect("tcp://localhost:5560")

	poller = zmq.Poller()
	poller.register(dealer, zmq.POLLIN)
	while True:
		socks = dict(poller.poll(100))
		if socks.get(dealer) == zmq.POLLIN:
			print dealer.recv_multipart()
			sleep(num)

if __name__ == '__main__':
	main(sys.argv)
