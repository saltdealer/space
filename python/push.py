import sys
import time
import zmq

context = zmq.Context()

sender = context.socket(zmq.PUSH)
dealer = context.socket(zmq.DEALER)
sender.connect("tcp://localhost:5558")
dealer.connect("tcp://localhost:5559")
dealer.set_string(zmq.IDENTITY,u'45')
dealer.setsockopt(zmq.RCVTIMEO,1000)
try:
    dealer.recv()
except zmq.erro:
    print e
    exit()
dealer.send('aa')
print 'connected'
sender.setsockopt(zmq.SNDTIMEO, 1000)
poller = zmq.Poller()
poller.register(sender,zmq.POLLOUT)
sender.setsockopt(zmq.LINGER, -1)
evts = poller.poll(1000)
b= dict(evts)
print b.get(sender)
print sender.send('b')
evts = poller.poll(1000)
print evts
print 'sended'
sender.close()
print context.destroy(1)
context.term()
print 'term'
