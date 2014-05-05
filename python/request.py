
import zmq

class Request:
    def __init__(self,request_id,target):
        self.request_id = request_id
        self.target = target
    def start(self):
        re = "OK"
        context = zmq.Context()
        dealer = context.socket(zmq.DEALER)
        dealer.setsockopt(zmq.IDENTITY, b"request1")
        dealer.connect("tcp://localhost:5559")
        dealer.setsockopt(zmq.LINGER, 0)
        dealer.setsockopt(zmq.RCVTIMEO, 2000)
        request = {
		    'type':'job',
		    'url':self.target,
		    'deep':'2',
		    'key':'shenmeane'
		    }
        dealer.send_json(request)
        try :
            dealer.recv()
        except Exception as e:
            print e
            re = "FAIL"
        dealer.close()
        context.destroy()
        context.term()
        print re
        return re

if __name__ == '__main__':
    re = Request('test','http://www.baidu.com')
    re.start()

