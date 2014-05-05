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
from django.contrib import auth
from django.template import RequestContext
import datetime
from forms import TaskForm
from task_request import task_Request
from models import Task ,Task_url
import json

@login_required(redirect_field_name='next' )
def home(request):
    if request.method == 'POST':
        form = TaskForm(request.POST)
        if form.is_valid():
            cd = form.cleaned_data
           
            task = Task(name=cd['name'], target=cd['target'],
                task_type=cd['choice'],website=cd['target'],user=request.user, 
                created_time=datetime.datetime.now(),task_status=1, scrawl_deep =cd['deep'])
            task.save()
            task_request = task_Request(str(task.id),
                cd['target'],'job',cd['deep'],domain='subdomain' )
            flag = task_request.start()
            if not flag:
                task.delete()
            re = {"flag":flag,"task_id":task.id}
            re = json.dumps(re)
            
            return HttpResponse( re )
    else:
        form = TaskForm()
    return render_to_response('task_home.html',{'form':form }, RequestContext(request))
	#t = get_template('home.html')
	#html = t.render(Context())
	#return HttpResponse(html)
