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
import md5
import sqlite3

class Test:
    def __init__(self):
        self.contents = ''
      
    def body_callback(self,buf):
        self.contents = self.contents + buf

def rand_str():
    return ''.join(random.sample(['a','b','c','d','e','f','g','h','i','j','k','l','m','n'],6))

def get_img(url,name,path):
    while 1:
        try :
            r = urllib2.urlopen(url)
            print path+'/'+name+'.gif'
            f = open(path+'/'+name+'.gif','ab+')
            f.write(r.read())
            r.close()
            f.close()
            break
        except Exception,e:
            print 'error'
            continue 


class curl_request:
    c=None
    def __init__(self,url,action='get'):
        self.url = url
        self.url_para =None
        self.c = pycurl.Curl()
        print self.url,"     d"
        self.c.setopt(pycurl.URL,self.url)
        self.c.setopt(pycurl.USERAGENT,'Miozilla/4.0 (compatible; MSIE 6.0; WindowsNT 5.1');
        self.c.setopt(pycurl.REFERER,'http://www.google.com/search?sourceid=chrome&ie=UTF-8&q='+rand_str())
        self.c.setopt(pycurl.COOKIE,'Hm_lvt_5251b1b3df8c7fd322ea256727293cf0=1393221156,1393223230,1393223252,1393223985;_jzqa=1.46109393469532')
       # self.c.setopt(pycurl.VERBOSE,1)

        self.c.setopt(pycurl.HEADER,1)
        self.c.setopt(pycurl.FOLLOWLOCATION, 1)
        self.c.setopt(pycurl.MAXREDIRS, 5)
        self.c.setopt(pycurl.COOKIEFILE, 'cookie_file_name.txt')
        self.c.setopt(pycurl.COOKIEJAR, 'cookie_file_name.txt')
        if action == 'post':
            self.c.setopt(pycurl.POST,1)
            self.c.setopt(pycurl.POSTFIELDS, post_data = {"noe":"noe"})
        else:
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
            self.r = self.buf.getvalue()
            self.h = self.head.getvalue()
            self.code = self.c.getinfo(pycurl.HTTP_CODE)
            self.info = self.c.getinfo(pycurl.EFFECTIVE_URL)
            self.cookie = self.c.getinfo(pycurl.INFO_COOKIELIST)
        except Exception,e:
            self.c.close()
            self.buf.close()
            self.head.close()
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


def get_dynamic_mm(buf):
    root_soup = BeautifulSoup(''.join( buf ),fromEncoding="utf-8")
    div = root_soup.find('div',{ "class":"mm_time"})
    if div:
        for divsub in div.div :
            if str(type(divsub)) ==  "<class 'BeautifulSoup.Tag'>" and divsub['class'] ==    "girl_info" :
                name = divsub.a.string.strip().replace(" ","")
                page = divsub.a['href']
        img_url = div.img['src']
        #get_img(img_url,name,name)
        return page,name

def parse_mm_home(buf,pre_referer,name):
    if not buf:
        return 
    root_soup = BeautifulSoup(''.join( buf ),fromEncoding="utf-8")
    div_user_name = root_soup.find('div',{"class":"user-photo"})
    if div_user_name:
        user_name = div_user_name.a.img['alt']
        print user_name
        
        
    div = root_soup.find('div',{"class":"left_nav"})
    if div:
        count = 0
        count_sub =0
        photo_link = []
        photo_list_link = div.ul.contents[5].a['href']
        photo_list = curl_request(str( photo_list_link  ))
        photo_list.perform(referer=str(pre_referer))
        buf_photo_list = photo_list.get_body()
        root =  BeautifulSoup(''.join(buf_photo_list),fromEncoding="utf-8")
        photo = root.find('ul',{"class":"albumlist"})

        for li in photo:
            if not str(type(li)) ==  "<class 'BeautifulSoup.Tag'>" :
                continue
            li = li.div
            if str(type(li)) ==  "<class 'BeautifulSoup.Tag'>" and li['class'] == "left-pic-box":
                link = li.dl.dt.a['href']
                photo = photo_list.perform(url= str(link),referer=str(photo_list_link) )
                photo_album_buf = photo_list.get_body()
                parse_node = BeautifulSoup(''.join(  photo_album_buf ),fromEncoding="utf-8")
                img = parse_node.find('ul',{"id": "photolist"})
                count_sub = 0
                link_img = []
                if img is None:
                    continue
                for li1 in img:
                    count_sub +=1

                    if str(type(li1)) == "<class 'BeautifulSoup.Tag'>" and      li1.dt and not li1.dt.a.has_key('onclick'):
                        link = li1.dt.a['href']
                        link_img.append(str(link))        
                for i in link_img:
                    request =  photo_list.perform(url=str(i),referer=str(photo_list_link) )
                    re = photo_list.get_body()
                    parse_re = BeautifulSoup(''.join(  re ),fromEncoding="utf-8")
                    tmp = parse_re.find('img',{"id":"pimg"})
                    link_tmp = tmp['src']
                    get_img(str(link_tmp), name+md5.new(link_tmp).hexdigest() , './image')


if __name__ == '__main__':

    if len(sys.argv) > 1 or 1:
        cx = sqlite3.connect('db')
        cu = cx.cursor()
        try:
            cu.execute("""create table content (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT   )""")
        except Exception,e:
            pass
        mm_home = None
        url = sys.argv[1]
        url = "http://www.sodao.com/app/showtime/girl?shouye"
        login = curl_request("http://reg.sodao.com/account/logon")
        login.set_url_para("?siteid=2&loginid=8aa8b46f-2e16-43c0-890a-89342d941d3b&signtext=fXayVh0a7GzH1NxSFUfBbLvoP%2fqoH%2fuHT55%2fR49GzVz64jTbJ%2bZ9XK54vRkxJ%2fKaAhyO48Lg0jMECHPH8xP5KL7FR1T27h5jP%2bcQHZDtrxhnPwLmn%2b8S4YcOFtfwShtSfy3IQtZNL6O6WrqO%2fepmR3RE9Uf%2bKXhOrkIGau0T5Xc%3d&uid=&fromurl=http%3a%2f%2fwww.sodao.com")
        login.set_post_para({"account":"jjxx2004@gmail.com","password":"12344321","rememberMe":"on","btnSubmit":"%E7%99%BB++++++%E5%BD%95"})
        login.set_cookie("referUrl_reg=http://www.sodao.com/app/showtime/girl;sodao_uba_id=1005333;CNZZDATA30034403=cnzz_eid%3D1289256132-1395642447-http%253A%252F%252Freg.sodao.com%252F%26ntime%3D1395968329%26cnzz_a%3D0%26sin%3Dhttp%253A%252F%252Fwww.sodao.com%252Fapp%252Fshowtime%252Fgirl%26ltime%3D1395970831824%26rtime%3D2;sd_uid=m2gatwwokzb4dwhz4n3itn5n;username=;ASP.NET_SessionId=sqibckyusqeeflqumrozr2af;flashnotice=2619efafdd354c0885463cfd9b15bd70")
        login.perform()

        while 1:

            root = curl_request(url) 
            root.perform()
            code =  root.get_code()
            if int(code) == 200 :
                mm_home,name =get_dynamic_mm(root.get_body())
                re = cu.execute( " select * from content where url='"+str(mm_home)+"'")
                flag = re.fetchall()
                print 'flag is ',flag
                if flag != []:
                    time.sleep(40)
                    continue
                cu.execute( "insert into content( id, url ) values( ?, ? )", ( None ,str(mm_home) ) )                                            
                cx.commit()
            if mm_home:
                home_request = curl_request(str(mm_home))
                home_request.perform(referer=url)
                parse_mm_home(home_request.get_body(),mm_home,name)
            
            time.sleep(40)
        
