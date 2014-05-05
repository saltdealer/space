import zmq

context = zmq.Context()

dealer = context.socket(zmq.DEALER)
dealer.setsockopt(zmq.IDENTITY, b"PEER2")
dealer.connect("tcp://localhost:5777")
dealer.send(b"dfdf")
print dealer.recv()
