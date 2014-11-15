#!/usr/bin/env python
# -*- coding: utf-8 -*-

__author__ = 'learned by xiaojian'

from models import User, Blog, Comment
import time, logging

from transwarp import db

logging.basicConfig(level=logging.INFO)

db.create_engine(user='root', password='114763xj',database='test')
u = User(name='Test', email = 'xiaojian@a.com', password='123213',image='about:blank')

db.update(' %s ' %  u.__sql__() )

print User.__table__

u.insert()

print 'new user id:',u.id

u1 = User.find_first('where email=?', 'xiaojian@a.com')

print 'find ',u1.name

#u1.delete()

u2 = User.find_first('where email=?', 'xiaojian@a.com')
print 'find :',u2

