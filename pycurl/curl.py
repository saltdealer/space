#!usr/bin/env python
#coding=utf-8
# 
import sys
reload(sys)   
sys.setdefaultencoding('utf8')  
from BeautifulSoup import BeautifulSoup
import re
import urllib2
import base64
import time
import StringIO
import random
class Test:
    def __init__(self):
        self.contents = ''
      
    def body_callback(self,buf):
        self.contents = self.contents + buf

import cStringIO

def rand_str():
    return ''.join(random.sample(['a','b','c','d','e','f','g','h','i','j','k','l','m','n'],6))

def get_img(url):    
    try :
        r = urllib2.urlopen(img)
        f = open('./'+base64.b64encode(name.encode('utf-8'))+'.gif','ab+')
        f.write(r.read())
        f.close()
        r.close()
    except Exception,e:
        print 'error'


class curl_request:
    c=None
    def __init__(self,url,action='post'):
        self.url = url
        self.c = pycurl.Curl()
        self.c.setopt(pycurl.URL,self.url)
        self.c.setopt(pycurl.USERAGENT,'Miozilla/4.0 (compatible; MSIE 6.0; WindowsNT 5.1');
        self.c.setopt(pycurl.REFERER,'http://www.google.com/search?sourceid=chrome&ie=UTF-8&q='+rand_str())
        self.c.setopt(pycurl.COOKIE,'Hm_lvt_5251b1b3df8c7fd322ea256727293cf0=1393221156,1393223230,1393223252,1393223985;_jzqa=1.46109393469532')
        self.c.setopt(pycurl.VERBOSE,1)

        self.c.setopt(pycurl.HEADER,1)
        self.c.setopt(pycurl.FOLLOWLOCATION, 1)
        self.c.setopt(pycurl.MAXREDIRS, 5)
        self.c.setopt(pycurl.COOKIEFILE, 'cookie_file_name.txt')
        self.c.setopt(pycurl.COOKIEJAR, 'cookie_file_name.txt')
        if action == 'post':
            self.c.setopt(pycurl.POST,1)
            self.c.setopt(pycurl.POSTFIELDS, post_data)
        else:
            self.c.setopt(pycurl.HTTPGET,1)

#        c.setopt(c.WRITEFUNCTION, self.write)

#        c.setopt(pycurl.HEADERFUNCTION, d.body_callback)
        self.c.setopt(pycurl.ENCODING, 'gzip,deflate');
    def perform(self,url=''):
        if url != '':
            self.c.setopt(pycurl.URL,url)
        self.buf = cStringIO.StringIO()
        self.head = cStringIO.StringIO()
        self.c.setopt(self.c.WRITEFUNCTION, self.buf.write)
        self.c.setopt(pycurl.HEADERFUNCTION, self.head.write)
        try:
            self.c.perform()
        except Exception,e:
            self.c.close()
            self.buf.close()
            self.head.close()
            continue
        self.r = self.buf.getvalue()
        self.h = self.head.getvalue()
        self.buf.close()
        self.head.close()
    def __del__(self):
        self.c.close()






t = Test()
d = Test()
post_data="dd"
img_old =''
flag = 1
while 1:
    print 'start'
    c = pycurl.Curl()
    if flag == 1:
        c.setopt(pycurl.URL, "http://www.sodao.com/app/showtime/girl")
        flag = 1
    else: 
        c.setopt(pycurl.URL,"http://show.sodao.com")
        flag =1
    c.setopt(pycurl.USERAGENT,'Miozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1'); 
    c.setopt(pycurl.REFERER,'http://www.google.com/search?sourceid=chrome&ie=UTF-8&q='+rand_str())
    c.setopt(pycurl.HTTPHEADER,['Accept: text/html','Accept-Charset: UTF-8'])
#    c.setopt(pycurl.COOKIE,'Hm_lvt_5251b1b3df8c7fd322ea256727293cf0=1393221156,1393223230,1393223252,1393223985;_jzqa=1.4610939346953260500.1393221957.139322')
    c.setopt(pycurl.VERBOSE,1)

    c.setopt(pycurl.HEADER,1)
    c.setopt(pycurl.FOLLOWLOCATION, 1)
    c.setopt(pycurl.MAXREDIRS, 5)
    c.setopt(pycurl.COOKIEFILE, 'cookie_file_name1.txt')
    c.setopt(pycurl.COOKIEJAR, 'cookie_file_name2.txt')
    #c.setopt(pycurl.POST,1);
    #c.setopt(pycurl.POSTFIELDS, post_data);
    c.setopt(c.WRITEFUNCTION, t.body_callback)
    c.setopt(pycurl.HEADERFUNCTION, d.body_callback)
    c.setopt(pycurl.ENCODING, 'gzip,deflate'); 
    
    try:
        c.perform()
    except Exception,e:
        c.close()
        continue

    soup = BeautifulSoup(''.join(t.contents),fromEncoding="utf-8")
#a=soup.findAll('div', {"class":"mmpage_content"})
#    print soup.prettify()
    print c.getinfo(pycurl.HTTP_CODE),c.getinfo(pycurl.EFFECTIVE_URL)
    print c.getinfo(pycurl.INFO_COOKIELIST)

    div = soup.find('div', {"class":"mm_time"})
    if div == None:
        time.sleep(3)
        continue
    print div, type(div)
    for divsub in div.div:
        if str(type(divsub)) == "<class 'BeautifulSoup.Tag'>" and divsub['class'] ==    "girl_info" :
            name = divsub.a.string.strip().replace(" ", "")
            page = divsub.a['href']
    img = div.img['src']
    print "old_imt",img_old,img
    if img == img_old:
        time.sleep(30)
        continue
    img_old = img
    try:
        r = urllib2.urlopen(img)
        print 'aa'
        f = open('./'+base64.b64encode(name.encode('utf-8'))+'.gif','ab+')
        f.write(r.read())
        f.close()
        time.sleep(30)
        c.close()
        r.close()
    except Exception,e:
        c.close()
        r.close()
"

if __name__ == '__main__':
    print 'aa'
