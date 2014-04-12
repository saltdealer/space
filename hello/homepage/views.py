from django.shortcuts import render
from django.http import HttpResponse
import datetime

def main_page(request):
    return HttpResponse(" hi you there ")

def time_now(request):
    now = datetime.datetime.now()
    html = "<html><body> it is now %s.</body></html>" % now
    return HttpResponse(html)


