import zmq
import json
context = zmq.Context()
frontend = context.socket(zmq.ROUTER)
backend = context.socket(zmq.ROUTER)

frontend.bind("tcp://*:5559")
backend.bind("tcp://*:5560")

poller = zmq.Poller()
poller.register(frontend, zmq.POLLIN)
poller.register(backend, zmq.POLLIN)

worker_list = {}
request_list = { }
status_list = { }
while True:
    socks = dict(poller.poll(100))

    if socks.get(frontend) == zmq.POLLIN:
        message = frontend.recv_multipart()
        print message
        requester_id = message[0]
        msg = eval(message[1])
        if msg.get('type') == 'job':
            worker_wanted = None
            if request_list.has_key(requester_id):
                worker_wanted = request_list[ requester_id  ]
            else:
                for worker in worker_list:
                    if worker_list[worker] == []:
                        print 'worker is none ,wanted is ',worker
                        worker_list[worker].append(requester_id) 
                        worker_wanted = worker
                        break
                    else :
                        if worker_wanted ==None:
                            worker_wanted = worker
                            continue
                        if len( worker_list[ worker ]) < len(worker_list[worker_wanted]) :
                            workerer_wanted = worker
            print worker_wanted
            if worker_wanted == None:
                continue
            request_list[ requester_id  ] = worker_wanted
            message2 = []
            message2.append(  worker_wanted )
            message2.append( requester_id )
            message2.append( str(msg) )
            backend.send_multipart(message2)

            message2 =[]
            message2.append(requester_id )
            message2.append('ok')
            frontend.send_multipart(message2)
        elif  msg.get('type') == 'status':
            message2 =[]
            message2.append(requester_id )
            if requester_id not in status_list:
                continue
            message2.append( json.dumps(status_list[requester_id]))
            print message2
            frontend.send_multipart(message2)

    if socks.get(backend) == zmq.POLLIN:
        print 'request_list is ',request_list,' worker_list ',worker_list
        message = backend.recv_multipart()
        worker_id = message[0]
        if worker_list.has_key(worker_id):
            pass
        else:
            worker_list[worker_id] = []
        msg = eval(message[1])
        print msg
        if msg['Type'] =='status':
            print 'worker ',worker_id,'\'s  status is ', msg['msg']
            for m in msg['msg']:
                status_list[m]=msg['msg'][m] 
                if msg['msg'][m]['Type'] == 3:
                    if request_list.has_key(m):
                        del request_list[m]
                    if  m in worker_list[worker_id]:
                        del worker_list[worker_id][  worker_list[worker_id].index(m)   ]
        print 'status_list is ',status_list


        frontend.send_multipart(message)

