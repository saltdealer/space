
import zmq
import json

class task_Request:
    def __init__(self,request_id,target,flag,deep,**option):
        self.request_id = request_id
        self.target = target
        self.flag = flag
        self.deep =int(deep)
        self.option = option
    def start(self):
        re = True
        context = zmq.Context()
        dealer = context.socket(zmq.DEALER)
        dealer.setsockopt(zmq.IDENTITY, self.request_id)
        dealer.connect("tcp://localhost:5559")
        dealer.setsockopt(zmq.LINGER, 0)
        dealer.setsockopt(zmq.RCVTIMEO, 2000)
        request = {
                'type':self.flag,
                'url':self.target,
                'deep':self.deep,
                'key': json.dumps(self.option)
                }
        dealer.send_json(request)
        try :
            reply= dealer.recv()
            print reply
        except Exception as e:
            print e
            re = False
        dealer.close()
        context.destroy()
        context.term()
        if self.flag=='status' and re:
            return reply

        print re
        return re

if __name__ == '__main__':
    re = Request('test','http://www.baidu.com')
    re.start()

