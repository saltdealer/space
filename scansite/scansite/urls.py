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

    url(r'^admin/', include(admin.site.urls)),
    url(r'^contact/', include('books.urls')),
    url(r'^(?P<offset>\d{1,2})/$', 'scansite.views.index', {'para':'for debug'}  ),
    url(r'^login/',include('login.urls')),
    url(r'^$','scansite.views.index'),
    url(r'create',include('task.urls')),
    url(r'^search/$', 'books.views.search'),
    url(r'^status/',include('status.urls')),
    )

urlpatterns += patterns('books',
        url(r'^search/$', 'views.search'),
)

urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
