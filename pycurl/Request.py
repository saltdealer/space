#!usr/bin/env python
#coding=utf-8
# 
import sys
import os
reload(sys)   
sys.setdefaultencoding('utf8')  
from BeautifulSoup import BeautifulSoup
import re
import urllib2
import base64
import time
import StringIO
import random
import cStringIO
import pycurl
import urllib



class curl_request:
    c=None
    def __init__(self,url,action='get'):
        self.url = url
        self.url_para =None
        self.c = pycurl.Curl()
        print self.url
        self.c.setopt(pycurl.URL,self.url)
        self.c.setopt(pycurl.USERAGENT,'Miozilla/4.0 (compatible; MSIE 6.0; WindowsNT 5.1');
        self.c.setopt(pycurl.REFERER,'http://www.google.com/search?sourceid=chrome&ie=UTF-8&q='+rand_str())
        self.c.setopt(pycurl.VERBOSE,1)

        self.c.setopt(pycurl.HEADER,1)
        self.c.setopt(pycurl.FOLLOWLOCATION, 1)
        self.c.setopt(pycurl.MAXREDIRS, 5)
        self.c.setopt(pycurl.COOKIEFILE, 'cookie_file_name.txt')
        self.c.setopt(pycurl.COOKIEJAR, 'cookie_file_name.txt')
        self.c.setopt(pycurl.HTTPGET,1)

#        c.setopt(c.WRITEFUNCTION, self.write)

#        c.setopt(pycurl.HEADERFUNCTION, d.body_callback)
        self.c.setopt(pycurl.ENCODING, 'gzip,deflate');

    def set_url_para(self,para):
        self.url_para = para
        url = self.url + para
        self.c.setopt(pycurl.URL,url)

    def set_post_para(self,para):
        self.c.setopt(pycurl.POST,1)
        self.c.setopt(pycurl.POSTFIELDS, urllib.urlencode( para))
    def set_cookie(self,cookie):
        self.c.setopt(pycurl.COOKIE,cookie)

    def perform(self,url='',referer=''):
        if url != '':
            self.c.setopt(pycurl.URL,url)
        if referer != '':
            self.c.setopt(pycurl.REFERER,referer)
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
        self.r = self.buf.getvalue()
        self.h = self.head.getvalue()
        self.code = self.c.getinfo(pycurl.HTTP_CODE)
        self.info = self.c.getinfo(pycurl.EFFECTIVE_URL)
        self.cookie = self.c.getinfo(pycurl.INFO_COOKIELIST)

        self.buf.close()
        self.head.close()
    def __del__(self):
        self.c.close()

    def get_body(self):
        return self.r
    def get_head(self):
        return self.h
    def get_code(self):
        return self.code
    def get_info(self):
        return self.info
    def get_cookie(self):
        return self.cookie
