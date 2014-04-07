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
import random
import urllib

class Test:
    def __init__(self):
        self.contents = ''
      
    def body_callback(self,buf):
        self.contents = self.contents + buf


import pycurl
def rand_str():
    return ''.join(random.sample(['a','b','c','d','e','f','g','h','i','j','k','l','m','n'], 6))


t = Test()
d = Test()
post_data={"account":"jjxx2004@gmail.com","password":"12344321","rememberMe":"on","btnSubmit":"%E7%99%BB++++++%E5%BD%95"}
img_old ='';
cookie='referUrl_reg=http://www.sodao.com/app/showtime/girl;sodao_uba_id=1005333;CNZZDATA30034403=cnzz_eid%3D1289256132-1395642447-http%253A%252F%252Freg.sodao.com%252F%26ntime%3D1395968329%26cnzz_a%3D0%26sin%3Dhttp%253A%252F%252Fwww.sodao.com%252Fapp%252Fshowtime%252Fgirl%26ltime%3D1395970831824%26rtime%3D2;sd_uid=m2gatwwokzb4dwhz4n3itn5n; username=;'

print 'start'
while 1:
    c = pycurl.Curl()
    #url = "http://image.baidu.com/i?tn=baiduimage&ct=201326592&lm=-1&cl=2&nc=1&word="
    url = '/duy/d'
    c.setopt(pycurl.URL,url)
    c.setopt(pycurl.USERAGENT,'Mozilla/5.0 (Windows NT 6.1; rv:27.0) Gecko/20100101 Firefox/27.0')
    c.setopt(pycurl.REFERER,'http://www.google.com/search?sourceid=chrome&ie=UTF-8&q='+rand_str())
    c.setopt(pycurl.HTTPHEADER,['text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'])
    c.setopt(pycurl.COOKIE,cookie)
    c.setopt(pycurl.VERBOSE,1)
    c.setopt(pycurl.FOLLOWLOCATION, 1)
    c.setopt(pycurl.MAXREDIRS, 5)
    c.setopt(pycurl.COOKIEFILE,"cookie_file_name")
    c.setopt(pycurl.COOKIEJAR, "cookie_file_name")
    c.setopt(pycurl.POST,1)
    c.setopt(pycurl.POSTFIELDS, urllib.urlencode(post_data))
    c.setopt(c.WRITEFUNCTION, t.body_callback)
    c.setopt(pycurl.HEADERFUNCTION, d.body_callback)
    c.setopt(pycurl.ENCODING, 'gzip,deflate')

    try:
        c.perform()
    except Exception,e:
        c.close()
    soup = BeautifulSoup(''.join(t.contents),fromEncoding="utf-8")
    print d.contents
    print c.getinfo(pycurl.HTTP_CODE)
#a=soup.findAll('div', {"class":"mmpage_content"})
    print soup.prettify()
    break
    c.close()
    del c
