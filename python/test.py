import zmq

context = zmq.Context()

sink = context.socket(zmq.ROUTER)
sink.bind("inproc://example")

# First allow 0MQ to set the identity
anonymous = context.socket(zmq.DEALER)
anonymous.connect("inproc://example")
anonymous.send(b"ROUTER uses a generated UUID")
print sink.recv_multipart()

# Then set the identity ourselves
identified = context.socket(zmq.DEALER)
identified.setsockopt(zmq.IDENTITY, b"PEER2")
identified.connect("inproc://example")
identified.send(b"ROUTER socket uses REQ's socket identity")
print sink.recv_multipart()
