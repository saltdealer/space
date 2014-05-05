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
from django.contrib.auth.decorators import login_required
from django.shortcuts import render, render_to_response
from django.template import RequestContext
import datetime
from forms import TaskForm
from request import Request

@login_required(redirect_field_name='next' )
def index(request):
    offset = 0
    para = '22'
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

@login_required(redirect_field_name='next' )
def home(request):
    if request.method == 'POST':
        form = TaskForm(request.POST)
        if form.is_valid():
            cd = form.cleaned_data
            request = Request('me','www.baidu.com')
            re = request.start()
            return HttpResponse( re)
    else:
        form = TaskForm()
    return render_to_response('home.html',{'form':form }, RequestContext(request))
	#t = get_template('home.html')
	#html = t.render(Context())
	#return HttpResponse(html)
