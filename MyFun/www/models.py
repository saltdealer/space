#!/usr/bin/env python
# -*- coding: utf-8 -*-

__author__ = 'Michael Liao'

'''
Models for user, blog, comment.
'''

import time, uuid

import random
from transwarp.db import next_id
from transwarp.orm import Model, StringField, BooleanField, FloatField, TextField, IntegerField

def next_id():
    return '%015d%s000' % (int(time.time()*1000), uuid.uuid4().hex)

class User(Model):
    __table__ = 'users'

    id = StringField(primary_key=True, default=next_id, ddl='varchar(50)')
    phone = StringField(updatable=False, ddl='varchar(11)')
    password = StringField(ddl='varchar(50)')
    female = BooleanField()
    valid = BooleanField()
    name = StringField(ddl='varchar(50)')
    image = StringField(ddl='varchar(500)')
    created_at = FloatField(updatable=False, default=time.time)
class Token(Model):
    __table__ = 'tokens'

    id = StringField(primary_key=True, default=next_id, ddl='varchar(50)')
    token1 = StringField( default=next_id, ddl='varchar(50)')
    token2 = StringField( default=next_id, ddl='varchar(50)')
class VerifyCode(Model):
    __table__ = 'verify_code'

    id = StringField(primary_key=True, default=next_id, ddl='varchar(50)')
    num = StringField(ddl='varchar(11)')
    code = StringField(ddl='varchar(5)')
    created_at = FloatField(default=time.time)
