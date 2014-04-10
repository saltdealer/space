#!usr/bin/env python
#coding=utf-8
# 
# 
# Created Time: Wed 09 Apr 2014 09:36:20 AM PDT
# 
# FileName:     ftp.py
# 
# Description:  
# 
# ChangeLog:
import sys
import os 
from ftplib import FTP 
import socket
import time

def ftp_upload( file_name ):
    socket.setdefaulttimeout(10)
    ftp = FTP()
    bufsize = 1024*5
    ftp.set_debuglevel(2)
    try:
        ftp.connect('185.28.20.238','21')
        ftp.login('u737107852','114763xj')
        ftp.cwd('photo')
    except Exception ,e:
        print '链接失败'
        return False

    file_handler = open(file_name,'rb')
    try:
        ftp.storbinary('STOR %s' %os.path.basename(file_name).split('.')[0].replace('=','')+'.jpg',file_handler,bufsize)
        file_handler.close()
    except Exception ,e:
        print '传输失败',e
        ftp.close()
        return False
    os.system('rm '+file_name)
    return True
def main(arg):
    while True:
        for a,b,c in os.walk('./'):
            for i in c:
                if i is not 'ftp.py' :
                    ftp_upload(i)
        time.sleep(600)
            
if __name__ == '__main__':
    main(sys.argv)

