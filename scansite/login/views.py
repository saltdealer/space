from django.shortcuts import render
from login.forms import LoginForm 
from django.shortcuts import render, render_to_response
from django.http import HttpResponse ,HttpResponseRedirect
from django.template import RequestContext 
from django.contrib import auth


# Create your views here.

def login(request):
    errors = []
    if request.user.is_authenticated():
        return HttpResponseRedirect('/1')
    if request.method == 'POST':
        form = LoginForm(request.POST)
        if form.is_valid():
            cd = form.cleaned_data
            username = cd['username']
            password = cd['password']
            if login_validate(request,username,password):
            	#return  HttpResponse('%s' % request.GET.get('next'))
                return HttpResponseRedirect('%s' % request.GET.get('next'))
            else:
                errors.append('wrong username or password')
        return render_to_response('loginform.html',{'form':form,'errors': errors},  RequestContext(request) )
    else:
        form = LoginForm( 
                initial={'username': 'admin'}
                )
    return render_to_response('login.html',{'form': form}, RequestContext(request) )

def login_validate(request,username,password):
    rtvalue = False
    user = auth.authenticate(username=username,password=password)
    if user is not None:
        if user.is_active:
            auth.login(request,user)
            return True
        return rtvalue
    


