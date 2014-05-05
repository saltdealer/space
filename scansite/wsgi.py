import os 
import sys

path='/home/blacktea/space/space/scansite'
if path not in sys.path:
    sys.path.append(path)

os.environ['DJANGO_SETTINGS_MODULE'] = 'scansite.settings'

import django.core.handlers.wsgi
application = django.core.handlers.wsgi.WSGIHandler()
