#!/usr/bin/python
#coding=utf-8
# 
# Created Time: 2014年03月31日 星期一 03时42分07秒
# 
# FileName:     myspider.py
# 
# Description:  
# 
# ChangeLog:

import sys
import os
import re
import urllib
import urllib2
import time
from time import sleep
import random
import pycurl
import Queue
import threading
import logging
from BeautifulSoup import BeautifulSoup
import getopt
import sqlite3
from Request import curl_request
import zmq
from urlparse import urlparse


scrawl_list={ }


global logger
class MyThread(threading.Thread):
	def __init__(self, workQueue, resultQueue, contentQueue , timeout=7,identity = 0):
		threading.Thread.__init__(self)
		self.timeout = timeout
		self.identity = identity
		self.setDaemon(True)
		self.workQueue = workQueue
		self.resultQueue = resultQueue
		self.contentQueue = contentQueue
		self.flag = False
		self.exit_flag = False
		self.job_id = None
		self.start()
		self.deep = 0

	def run(self):
		while True:
			try:
				#从队列里获取url
				callable, url, resultQueue, args = self.workQueue.get(timeout=self.timeout)
				self.flag = True #标记线程在请求url
				self.job_id = args.get('job_id')
				sleep = args.get('sleep')
				self.deep = args.get('deep')
				key = args.get('key')
				get_lock()
				scrawl_list[ self.job_id ][ 'in_queue' ] -=1
				un_lock()
				res = callable(url , resultQueue,self.contentQueue, sleep = 
						sleep,deep=self.deep,key = key, job_id=self.job_id,
						identity = self.identity)
				self.flag = False
				self.job_id = None
			except Queue.Empty:
				self.flag = False
				self.job_id = None
				if self.exit_flag:
					logger.info('exit_flag set')
					break
				continue
			except Exception as e:
				print 'run thread', e
				self.flag = False
				self.job_id = None
				raise

def insert_url(job_id, url, scrawl_flag, valid_flag):
	get_lock()
	try:
		cx = sqlite3.connect('db')
		cu = cx.cursor()
		cu.execute("insert into job_url values(?,?,?,?,?)", (None,job_id,url,0,valid_flag ) )
		cx.commit()
		cx.close()
	except Exception as e:
		print 'insert_url',e
		cx.close()
	finally:
		un_lock()

def select_url(job_id, url ):
	get_lock()
	sql_re = None
	try:
		cx = sqlite3.connect('db')
		cu = cx.cursor()
		cu.execute("select job_id,crawled_flag, valid_flag from job_url where url=(?) and job_id =(?)", (url,job_id) )
		sql_re = cu.fetchone()
		cx.close()
	except Exception as e:
		print  'select', e
		cx.close()
	finally:
		un_lock()
	return sql_re


def get_lock():
	if not mutex.acquire(1):
		print 'lock errrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr'

def un_lock():
	mutex.release()


#处理所有任务队列里的url 加入主队列
class url_handle(threading.Thread ):
	def __init__(self, jobs , thread_pool ):
		threading.Thread.__init__(self)
		self.jobs = jobs
		self.thread_pool = thread_pool
		self.start()
	def run(self):

		while True:
			for job in self.jobs:
				queue = job[2]
				msg = eval(job[1])
				url = None
				deep = None
				try:
					url,deep = queue.get(timeout=1)
#					print 'geting url ', url,' and deep ',deep
				except Queue.Empty:
					continue
				if url is not None and int(deep) <= int( msg.get('deep') ):
					sql_re = select_url(job[0],url)

					logger.info('the select url is'+url + ' resutl is '+str(sql_re))
					if sql_re is None:
						insert_url(job[0],url,0,0)
						get_lock()
						scrawl_list[ job[0]  ][ 'in_queue' ] += 1
						scrawl_list[ job[0] ][ 'scrawled_num' ] +=1
						un_lock()

						self.thread_pool.add_job(crawl_url, url, queue, sleep = get_rand(), key = msg.get('key') , deep =deep , job_id = job[0]  )
				elif url is not None:
					sql_re = select_url(job[0],url)
					logger.info('the select url is'+url + ' resutl is '+str(sql_re))
					if sql_re is None:
						insert_url(job[0],url,2,0)


class ThreadPool:
	def __init__(self,timeout, num_of_threads=10):
		self.workQueue = Queue.Queue()
		self.resultQueue = Queue.Queue()
		self.contentQueue = Queue.Queue()
		self.threads = []
		self.timeout = timeout
		self.__createThreadPool(num_of_threads)

	def __createThreadPool(self, num_of_threads):
		for i in range( num_of_threads ):
			thread = MyThread( self.workQueue, self.resultQueue, self.contentQueue, timeout = self.timeout , identity = str(i))
			self.threads.append(thread)

	def wait_for_complete(self):
		while len(self.threads):
			thread = self.threads.pop()
			if thread.isAlive():
				thread.join()
	def get_flag(self):
		jobs = { }
		deep ={ }
		for thread in self.threads:
			if thread.flag and thread.job_id is not None:
				if jobs.has_key(thread.job_id):
					jobs[thread.job_id] += 1
					deep[thread.job_id] += thread.deep
				else:
					deep[thread.job_id] = 0
					jobs[thread.job_id] = 1
		return jobs,deep
	def get_num(self):
		num = 0
		for thread in self.threads:
			if thread.flag:
				num += 1
		return num
	def set_flag(self):
		flag = False
		for thread in self.threads:
			thread.exit_flag = True

	def add_job(self,callable, args , Queue ,**kwargs):
		self.workQueue.put( (callable, args, Queue, kwargs) )

def resovle_address(base_url,link, domain_T='current-only'):
	domain_type = 0
	link = link.strip()
	link = link.replace('/./','/')
	link = link.replace(';','')

	p = urlparse(link)
	logger.info('p is '+ str(p))
	if p.hostname == None and p.path == None:
		return None

	base_url = base_url.strip()
	o = urlparse(base_url)
	temp = o.hostname.split('.')
	base_domain = temp[-2] + '.' + temp[-1]

	if p.hostname != None:	
		temp = p.hostname.split('.')
		link_domain = temp[-2] +'.' + temp[-1]

	if  o.hostname == p.hostname or p.hostname == None:
		logger.info('current domain')
		url = o.scheme + '://' + o.netloc + p.path
		domain_type = 1
	elif link_domain == base_domain :
		domain_type = 2
		url = link
		logger.info('child domain')
	else :
		domain_type = 3
		url = link
		logger.info('external domain')
	
	if domain_T == 'current-only':
		if domain_type == 2 or domain_type == 3:
			return None
	elif domain_T == 'subdomain':
		if domain_type ==3:
			return None
	else:
		pass
		
	logger.info('the url is ' + url)

	return url

def update_url_flag(job_id, url,valid_flag):
	get_lock()
	try:
		cx = sqlite3.connect('db')
		cu = cx.cursor()
		cu.execute('update job_url set crawled_flag = 1,valid_flag =(?) where url = (? ) and job_id=(?)',(valid_flag, url, job_id,) )
		cx.commit()
		cx.close()
	except Exception as e:
		print 'update', e
		cx.close()
	finally:
		un_lock()

def crawl_url( url, resultQueue, contentQueue, **arg): #sleep, deep, key):
	global logger
	sleep = arg.get('sleep')
	deep = int(arg.get('deep'))
	key = arg.get('key')
	key = eval(key)
	domain = 'subdomain'
	if key.has_key('domain'):
		domain = key['domain']
	job_id = arg.get('job_id')
	identity = arg.get('identity')
	start_time = time.time()

#	logger.debug('start to crawl the url: '+url.encode()+' and deep is: '+str(deep))
#	time.sleep(int(sleep[0]))


	home_url = curl_request(url)
	home_url.perform()
	end_time = time.time()
	buf = home_url.get_body()
	if buf is None:
		update_url_flag(job_id,url,0)
		return 

	root_soup = BeautifulSoup(''.join( buf ),fromEncoding="utf-8")
	body = root_soup.body
	u = body
#	logger.info('body is '+str(u))
	m = re.findall("<a.*?>",str(u))
	for sub in m:
		if len(sub) < 1:
			continue
		tag_a = BeautifulSoup(''.join( sub ),fromEncoding="utf-8")
		if tag_a.a is not None and tag_a.a.has_key('href'):
			url_s = tag_a.a['href']
			logger.info('getting tag' + url_s )
			url_s = resovle_address(url,url_s, domain)
		 #   print 'geting url and deep is ',url_s,deep
			if url_s is not None:
				#print 'adding iiiiiiiiiiiiiiiiiii',url_s
			#	logger.info('geting url :'+url_s.encode()+'deep is :'+str(deep))
				resultQueue.put( (url_s, deep+1) )
	update_url_flag(job_id,url,1)
	logger.info(identity  + ' thread end ' + str(end_time - start_time)  )
def Usage():
	print 'myspider.py usage:'
	print 'python myspider -u url -d deep -f logfile -l log_level --key  --thread thread_num --dbfile db'
	print 'log_level range in 1-5'
	print '--key used to search '
	print '--dbfile is the path to store matched text(default is db)'

def get_rand():
	return random.sample([0.1,0.2,0.3,0.4,0.5],1)
def main(argv):

	global mutex
	mutex = threading.Lock()
	thread_num = 10
	global logger
	log_file = 'log'
	dbfile = 'db'
	log_level = 5
	timeout = 7
	try:
		opts, args = getopt.getopt(argv[1:], 'n:f:l:', ['thread=','timeout='])
	except getopt.GetoptError, err:
		print str(err)
		exit(1)

	for o, a in opts:
		if o in ('-h','--help'):
			Usage()
			sys.exit(1)
		elif o in ('-u',):
			url = a
		elif o in ('-d',):
			scrawl_level = int(a)
		elif o in ('-f',):
			log_file = a
		elif o in ('-l',):
			log_level = int(a)
		elif o in ('--key'):
			key = a
		elif o in ('--thread'):
			thread_num = int(a)
		elif o in ('--dbfile'):
			dbfile = a
		elif o in ('--timeout'):
			timeout = int(a)
		else:
			print 'unhandled option'
			sys.exit(3)

	dbfile='db'
	if not os.path.exists(dbfile):
		cx = sqlite3.connect(dbfile)
		cu=cx.cursor()
		cu.execute("""create table job_url (id INTEGER PRIMARY KEY AUTOINCREMENT, job_id varchar(100), url text , crawled_flag INT8 ,valid_flag INT8 default 0 )""")
		cx.commit()
		cx.close()	

	cx = sqlite3.connect(dbfile)
	cu = cx.cursor()

	context = zmq.Context()
	dealer = context.socket(zmq.DEALER)
	dealer.setsockopt(zmq.IDENTITY, b"PEER2")
	dealer.connect("tcp://localhost:5560")
	poller = zmq.Poller()
	poller.register(dealer, zmq.POLLIN)

	logger = logging.getLogger()
	hdlr = logging.FileHandler(log_file)
	logger.addHandler(hdlr)
	level = (6-log_level)*10
	logger.setLevel(level)
	logger.debug('thread num is '+str(thread_num))

	jobs = []
	tp = ThreadPool(timeout,thread_num)
	status = { }
	ths =[]
	for i in range(30):
		th = url_handle(jobs,tp )
		ths.append(th)
	while True:
		socks = dict(poller.poll(2000))
		print 'after poll'
		if socks.get(dealer) == zmq.POLLIN:
			#从broker读取信息
			packet = dealer.recv_multipart()
			if len(packet) < 2:
				print packet
				continue 

			#判断job_id是否已经在job列表里
			job_id_recorded = None
			for job_i in jobs:
				if job[0] == packet[0]:
					job_id_recorded = job_i

			#申明任务需要的队列 和 变量
			if job_id_recorded == None:
				queue = Queue.Queue()
				packet.append(queue)
				packet.append(0)
				jobs.append(packet)
				status[ packet[0] ] = { 'Type' : 1 }
				get_lock()
				scrawl_list[packet[0]] = { }
				scrawl_list[ packet[0] ][ 'in_queue' ] = 0
				scrawl_list[ packet[0] ][ 'scrawled_num' ] = 0
				un_lock()

			#任务参数
			msg = eval(packet[1])
			print 'main thread adding job url is', msg.get('url') , 'job_id is ',packet[0]
			#加入主任务队列
			get_lock()
			scrawl_list[ packet[0] ][ 'in_queue' ] += 1
			scrawl_list[ packet[0] ][ 'scrawled_num' ] +=1 
			un_lock()
			url = msg.get('url')
			if url.find('http://') == -1:
				url = 'http://' + url
			print 'url is ',url
			tp.add_job(crawl_url,  url , queue, sleep = get_rand() , key = msg.get('key'), deep = 1, job_id = packet[0] )
		#获取每个crawl进程的状态
		th_status, th_deep = tp.get_flag()
		print th_status

		#处理进度信息
		for job in jobs:
			count = scrawl_list[ packet[0] ]['in_queue']
			scrawled_num = scrawl_list[ packet[0] ]['scrawled_num']
			if th_status.has_key(job[0]):
				ing = th_status[ job[0] ]
				deep_now = th_deep[ job[0] ] / ing 
			else:
				ing = 0
				deep_now =0
			print scrawl_list
			status[job[0]] = {'Type':2, 'left':job[2].qsize(), 'queuing':count, 'crawling':ing, 'crawled_num': scrawled_num, 'deep_now': deep_now, 'deep': eval(job[1]).get('deep') }
			print job[0], ' left url : ', job[2].qsize() , ' left in workqueue ', count , 'doing', ing, 'size of whole workqueue',tp.workQueue.qsize()
			if job[2].qsize() == 0 and count ==0 and ing ==0:
				job[3] += 1
				print  job[0] , 'is done delete this job'
				if job[3] > 2:
					del jobs[jobs.index(job)]
					del status[job[0]]
					#send stop flag
					status[job[0]] = {'Type':3 }
					end_status = {'Type':'status' }
					end_status['msg'] = status
					dealer.send_json( end_status )
					del status[job[0]]
		msg_status = {'Type':'status' }
		msg_status['msg']= status
		dealer.send_json( msg_status )

if __name__ == '__main__':
	main(sys.argv)

