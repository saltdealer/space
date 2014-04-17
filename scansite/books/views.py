from django.shortcuts import render, render_to_response
from django.http import HttpResponse
from books.models import Book
from django.template import RequestContext 
from books.forms import ContactForm
from django.core.mail import send_mail

# Create your views here.

def search(request):
    error = False
    errors = []
    if 'q' in request.GET:
        q = request.GET['q']
        if not q:
            errors.append('please enter a search term')
        elif len(q) > 20:
            errors.append('length of the string you enter more than 20')
        else:
            books  = Book.objects.filter(title__icontains=q)
            return render_to_response('search_results.html',  {'books':  books, 'query':q})
    return render_to_response('search_form.html', {'errors': errors} )

def contact(request):
    if request.method == 'POST':
        form = ContactForm(request.POST)
        if form.is_valid():
            cd = form.cleaned_data
            return HttpResponse('thank you')
    else:
        form = ContactForm( 
                initial={'subject': 'I love your site!'}
                )
    return render_to_response('contact_form.html',{'form': form}, RequestContext(request) )

def thanks(request):
    return HttpResponse('dsfdfdsfdsfdsf')
