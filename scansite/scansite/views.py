#!usr/bin/env python
#coding=utf-8
# 
# Created Time: Tue 15 Apr 2014 02:10:51 AM PDT
# 
# FileName:     views.py
# 
# Description:  
# 
# ChangeLog:

from django.template.loader import get_template
from django.template import Context
from django.http import HttpResponse
import datetime

def index(request, offset, para):
    try:
        offset = int(offset)
    except ValueError:
        raise Http404()

    values = request.META.items()
    values.append(para)
    values.sort()
    now = datetime.datetime.now()
    dt = now + datetime.timedelta(hours=offset)
    t = get_template('index.html')
    html = t.render(Context( { 'offset': offset,'date_time': dt, 'tag':'tag', 'paras': values  } ))
    return HttpResponse( html )
