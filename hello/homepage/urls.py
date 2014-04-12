#!usr/bin/env python
#coding=utf-8
# 
# Created Time: Sat 12 Apr 2014 09:56:39 AM PDT
# 
# FileName:     homepage/urls.py
# 
# Description:  
# 
# ChangeLog:

from django.conf.urls import patterns,url

from homepage import views

urlpatterns = patterns('',
        url(r'^$', views.time_now , name = 'time')
)
