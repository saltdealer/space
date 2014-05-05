from django.conf.urls import patterns, include, url
from django.contrib.auth.decorators import login_required, permission_required
from django.contrib import admin
from django.conf.urls.static import static
from django.conf import settings

admin.autodiscover()
urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'scansite.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^$','task.views.home'),
    )

