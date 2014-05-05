import sys
import time
import zmq

context = zmq.Context()

receiver = context.socket(zmq.PULL)
router = context.socket(zmq.ROUTER)
router.bind("tcp://*:5559")
receiver.bind("tcp://*:5558")

poller = zmq.Poller()
poller.register(receiver,zmq.POLLIN)
poller.register(router,zmq.POLLIN)
while True:
	socks = dict(poller.poll())
	
	if socks.get(receiver) == zmq.POLLIN:
		message = receiver.recv()
		print 'receiver', message
	if socks.get(router) == zmq.POLLIN:
		message = router.recv_multipart()
		print message
		router.send(message[0],zmq.SNDMORE)
		router.send(message[1])

