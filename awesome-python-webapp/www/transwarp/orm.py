#!/usr/bin/env python
# -*- coding: uft-8 -*-

__author__ = ' leanrned by xiaojian'

'''
Databases operation module. This module is idependent with web module
'''

import time ,logging
import db

class Filed(object):
    _count = 0
    
    def __init__(self, **kw):
        self.name = kw.get('name', None)
        self._default = kw.get('default', None)
        self.primary_key = kw.get('primary_key', False)
        self.nullable = kw.get('nullable', False)
        self.updatable = kw.get('updatable', True)
        self.insertable = kw.get('insertable', True)
        self.ddl = kw.get('ddl','')
        self._order = Field._count
        Field._count = Field._count + 1
    @property
           
