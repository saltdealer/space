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
import random
import pycurl
import Queue
import threading
import logging
from BeautifulSoup import BeautifulSoup
import getopt
import sqlite3
from Request import curl_request


global logger
class MyThread(threading.Thread):
    def __init__(self, workQueue, resultQueue, contentQueue, key, timeout=15):
        threading.Thread.__init__(self)
        self.mutex = threading.Lock()
        self.timeout = timeout
        self.setDaemon(True)
        self.workQueue = workQueue
        self.resultQueue = resultQueue
        self.contentQueue = contentQueue
        self.start()
        self.flag = False
        self.exit_flag = False
        self.key = key
        
    def run(self):
        while True:
            try:
               # if self.mutex.acquire(1): 
                callable, args, kwargs, deep = self.workQueue.get(timeout=self.timeout)
                #self.mutex.release()
                self.flag = True
                res = callable(args,self.resultQueue,self.contentQueue,kwargs,deep,self.key)
                self.flag = False
            except Queue.Empty:
                logger.debug('queue is emtpy')
                self.flag = False
                if self.exit_flag:
                    logger.info('exit_flag set')
                    break
                continue
            except :
                print sys.exc_info()
                raise
            
class ThreadPool:
    def __init__(self, key, num_of_threads=10):
        self.workQueue = Queue.Queue()
        self.resultQueue = Queue.Queue()
        self.contentQueue = Queue.Queue()
        self.threads = []
        self.key = key
        self.__createThreadPool(num_of_threads)
       
    def __createThreadPool(self, num_of_threads):
        for i in range( num_of_threads ):
            thread = MyThread( self.workQueue, self.resultQueue, self.contentQueue, self.key )
            self.threads.append(thread)
            
    def wait_for_complete(self):
        while len(self.threads):
            thread = self.threads.pop()
            if thread.isAlive():
                thread.join()
    def get_flag(self):
        flag = False
        for thread in self.threads:
            if thread.flag:
                flag = True
        return flag
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
                
    def add_job(self,callable, args,kwargs, deep):
        self.workQueue.put( (callable, args, kwargs, deep) )

def resovle_address(base_url,link):
    base_url = base_url.strip()
    logger.debug('url base is: '+base_url.encode()+' and link is: '+link.encode())
    link = link.strip()
    link.replace(';','')
    link.replace('\\','')
    link.replace('\'','')
    link.replace('/./','/')
    bash = base_url.rfind('/')
    if len(link) < 1:
        return None
    if bash != -1 and base_url[:bash+1] != "http://":
        base_url = base_url[:base_url.rfind('/')]
    m = re.search("http|www",link)
    if link[0] == '/' and len(link)>1:
        logger.debug('return url is ' + base_url.encode() + link.encode())
        return base_url + link
    elif m is not None:
        logger.debug('return link is' + link.encode()) 
        return link
    return None
    
        
    
        
def crawl_url( url, resultQueue, contentQueue, sleep, deep, key):
    global logger
    logger.debug('start to crawl the url: '+url.encode()+' and deep is: '+str(deep))
    time.sleep(int(sleep[0]))
    home_url = curl_request(url)
    home_url.perform()
    buf = home_url.get_body()
    if buf is None:
        return 
    root_soup = BeautifulSoup(''.join( buf ),fromEncoding="utf-8")
    body = root_soup.body
    u = body
    logger.info('body is '+str(u))
    m = re.findall("<a.*?>",str(u))
    for sub in m:
        if len(sub) < 1:
            continue
        tag_a = BeautifulSoup(''.join( sub ),fromEncoding="utf-8")
        if tag_a.a is not None and tag_a.a.has_key('href'):
            url_s = tag_a.a['href']
            url_s = resovle_address(url,url_s)
         #   print 'geting url and deep is ',url_s,deep
            if url_s is not None:
                #print 'adding iiiiiiiiiiiiiiiiiii',url_s
                logger.info('geting url :'+url_s.encode()+'deep is :'+str(deep))
                resultQueue.put( (url_s, deep+1) )
    if u is None:
        return
    for k in u:
        if re.search(key,str(k)) is not None:
          #  print str(k)
            contentQueue.put( (str(url), str(k) ))

def Usage():
    print 'myspider.py usage:'

def get_rand():
    return random.sample([0.1,0.2,0.3,0.4,0.5],1)
def main(argv):
    global logger
    thread_num=10
    try:
        opts, args = getopt.getopt(argv[1:],'hu:d:t:l:f:i:',['key=','thread=','dbfile='])
    except getopt.GetoptError, err:
        print str(err)
        Usage()
        sys.exit(2)
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
        else:
            print 'unhandled option'
            sys.exit(3)

    cu = None
    cx = None
    logger = logging.getLogger()
    hdlr = logging.FileHandler(log_file)
    logger.addHandler(hdlr)
    level = (6-log_level)*10
    logger.setLevel(level)
  #  logger.info("hi")
    if dbfile is not None:
        os.remove(dbfile)
        cx = sqlite3.connect(dbfile)
        cu=cx.cursor()
        cu.execute("""create table content (id INTEGER PRIMARY KEY AUTOINCREMENT,url varchar(100), content varchar(4000)  )""")
          
    logger.debug('thread num is '+str(thread_num))
    logger.debug('scrawl_level is ' + str(scrawl_level))
    
    
    tp = ThreadPool(key,thread_num)
    tp.add_job(crawl_url, url , get_rand() ,1)
    deep = 1
    time_old = time.time()
    count = 0
    while 1:
        time_new = time.time()
        if time_new - time_old > 10:
            print '已经处理链接数：',count,'正在处理链接数',tp.get_num(),'剩余未处理的链接数：',tp.resultQueue.qsize(),'未插入数据：',tp.contentQueue.qsize()
            time_old = time.time()
        try:
            url,deep= tp.resultQueue.get(timeout=0.5)
            if url is not None and int(deep) <= scrawl_level:
               # print "adding  deep",deep
                logger.info('adding url: '+url.encode()+'and deep is: '+str(deep))
                count += 1
                tp.add_job(crawl_url, url, get_rand(), deep)
        except Queue.Empty:
            if not tp.get_flag() and tp.contentQueue.qsize() == 0 and tp.resultQueue.qsize() == 0:
                print 'work done,exiting'
                tp.set_flag()
                break
        try:
            url,content= tp.contentQueue.get(timeout=0)
            if url is not None:
              #  print 'gettingiiiiiiiiii ',content,url
                cu.execute( "insert into content(id , url,content) values(?,?,?)", (None ,str(url), content.decode('utf-8')))
        except Queue.Empty:
            continue
            
        
    if cx is not None:
        cx.commit()
        cx.close()
    tp.wait_for_complete()
    #print tp.workQueue.qsize()
    
if __name__ == '__main__':
    main(sys.argv)


