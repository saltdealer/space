from django.shortcuts import render
from django.shortcuts import render, render_to_response
from django.http import HttpResponse ,HttpResponseRedirect
from django.template import RequestContext 
from django.contrib import auth
from task_request import task_Request

# Create your views here.

def main(request,task_id):

    #   return HttpResponse(task_id)
    return render_to_response('status_home.html',{'task_id':task_id} )


def index(request):
    reply = None
    if 'task_id' in request.GET:
        task_id = request.GET['task_id']
        req = task_Request(task_id.encode(),'df','status',0)
        reply = req.start()

    return HttpResponse(reply)
