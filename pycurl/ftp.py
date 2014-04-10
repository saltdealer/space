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
    ftp.connect('185.28.20.238','21')
    ftp.login('u737107852','114763xj')
    ftp.cwd('photo')
    file_handler = open(file_name,'rb')
    try:
        ftp.storbinary('STOR %s'
                %os.path.basename(i).split('.')[0].replace('=','')+'.jpg',file_handler,bufsize)
        file_handler.close()
    except Exception ,e:
        print '传输失败'
        ftp.close()
        return False
    os.system('rm '+file_name)
    return True
def main(arg):
    while True:
        for a,b,c in os.walk('./照片'):
            for i in c:
                if i is not 'ftp.py' :
                    print i
        time.sleep(600)
            
if __name__ == '__main__':
    main(sys.argv)

