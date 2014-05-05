import zmq

context = zmq.Context()

router = context.socket(zmq.ROUTER)
router.bind("tcp://*:5777")

s = router.recv_multipart()
router.send_multipart(s)
