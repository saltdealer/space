from django.conf.urls import patterns, include, url

from books import views
urlpatterns = patterns('',
    url(r'^$', views.contact),
    url(r'^thanks/$',views.thanks),
    )
