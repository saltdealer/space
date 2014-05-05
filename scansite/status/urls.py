from django.conf.urls import patterns, include, url

from status import views
urlpatterns = patterns('',
    url(r'^$', views.index),
    url(r'^(?P<task_id>\d{1,2})/$', views.main),
   # url(r'^thanks/$',views.thanks),
    )
