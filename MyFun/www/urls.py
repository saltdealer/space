#!/usr/bin/env python
# -*- coding: utf-8 -*-

__author__ = 'Michael Liao'

import os, re, time, base64, hashlib, logging

import markdown2

from transwarp.web import get, post, ctx, view, interceptor, seeother, notfound ,MultipartFile
from transwarp.db import next_id

from apis import api, Page, APIError, APIValueError, APIPermissionError, APIResourceNotFoundError, next_code
from models import User ,Token,VerifyCode,Image
from config import configs

_COOKIE_NAME = 'awesession'
_COOKIE_KEY = configs.session.secret

image_path = os.path.join(os.getcwd(),'myfunstatic/images')
image_path_relative = 'myfunstatic/images/'

def _get_page_index():
    page_index = 1
    try:
        page_index = int(ctx.request.get('page', '1'))
    except ValueError:
        pass
    return page_index

def make_signed_cookie(id, password, max_age):
    # build cookie string by: id-expires-md5
    expires = str(int(time.time() + (max_age or 86400)))
    L = [id, expires, hashlib.md5('%s-%s-%s-%s' % (id, password, expires, _COOKIE_KEY)).hexdigest()]
    return '-'.join(L)

def parse_signed_cookie(cookie_str):
    try:
        L = cookie_str.split('-')
        if len(L) != 3:
            return None
        id, expires, md5 = L
        if int(expires) < time.time():
            return None
        user = User.get(id)
        if user is None:
            return None
        if md5 != hashlib.md5('%s-%s-%s-%s' % (id, user.password, expires, _COOKIE_KEY)).hexdigest():
            return None
        return user
    except:
        return None

def check_admin():
    user = ctx.request.user
    if user and user.admin:
        return
    raise APIPermissionError('No permission.')
_RE_EMAIL = re.compile(r'^[a-z0-9\.\-\_]+\@[a-z0-9\-\_]+(\.[a-z0-9\-\_]+){1,4}$')
_RE_MD5 = re.compile(r'^[0-9a-f]{32}$')
_RE_PHONE = re.compile('^1[358][0-9]{9}$')	

@api
@post('/myfun/api-token/upload_profile_image')
def get_file():
    logging.info('get file')
    user = ctx.request.user
    if not user:
        raise APIError('Authencation:fail','token','token is wrong','-1')
    i = ctx.request.input(filetype='.jpg')

    logging.info('the file is %s'% (i.file))
    if not isinstance(i.file, MultipartFile):
        raise APIError('IO:error','file','read the file error','-1')
    logging.info('the token is %s' % i.token)
    file_name = next_id()+'.'+i.filetype
    file_path = os.path.join(image_path, file_name)
    logging.info('the out file is %s' % file_path)
    fw = open(file_path, 'wb')
    buf = i.file.file.readline()
    while buf != '':
        fw.write(buf)
        buf = i.file.file.readline()
    fw.close()
    image = Image(image_path=image_path_relative+file_name,user_id=user.id)
    image.insert()
    image.pop('image_path')
    image.errcode='0'
    return image
@api
@post('/myfun/api/user_login')
def user_login():
    logging.info('user start to login')
    i = ctx.request.input(phone='',password='')
    phone = i.phone.strip()
    password = i.password.strip()
    logging.info('the passwd is %s' % password)
    if not phone or not _RE_PHONE.match(phone):
        raise APIError('Value:illegal','phone','The string is not a phone num','-1')
    if not password or not _RE_MD5.match(password):
        logging.info('the passwd is not illegal')
        raise APIError('Value:illegal','password','The password is not a md5string','-1')

    user = User.find_first('where phone=?', phone)
    image = Image.find_first('where id=?',user.image)
    user.image = image.image_path
    logging.info('the user is %s' % user.name)
    if user.password == password:
        user.errcode='0'
        user.pop('password')
        return user
    else:
        raise APIError('Authentication:fail','password','The password is not correct','-1')


@interceptor('/myfun/api-token/')
def user_interceptor(next):
    logging.info('try to bind user from token')
    i = ctx.request.input(token='',phone='')
    token = i.token.strip()
    phone = i.phone.strip()
    logging.info('the token %s and phone %s'% (token,phone))
    ctx.request.user = None
    if not phone or not _RE_PHONE.match(phone):
        return next()
    user = User.find_first('where phone=?',phone)
    if not user:
        return next()
    token_verify = Token.find_first('where id=?',user.id)
    if token_verify.token1 != token:
        return next()
    logging.info('the token is %s' % token_verify)
    ctx.request.user = user
    return next()

@api
@post('/myfun/api/get_verify_code')
def get_code():
    i = ctx.request.input(num='')
    num = i.num.strip()
    if not num or not _RE_PHONE.match(num):
        raise APIError('TooOften',num,'the phone num is not right!','-1')
    user = User.find_first('where phone=?', num)
    if user and user.valid==True:
        raise APIError('duplicated:phone','phone','phone is already in use.','-1')
    verify_code = VerifyCode.find_first('where num=?',num)
    if verify_code:
        update_time = verify_code.created_at
        logging.info('the update_time %d %d' % (update_time, time.time()))
        delta = 60 - (time.time() - update_time) 
        if delta > 0:
            raise APIError('TooOften:code','%d' %  delta,'get code too often','-1')
        verify_code.code = next_code();
        verify_code.created_at = time.time()
        verify_code.update()
    else:
        verify_code = VerifyCode(num=num,code=next_code())
        verify_code.insert()
    verify_code.pop('id')
    verify_code.pop('created_at')
    verify_code.errcode='0'
    return verify_code
   
@api
@post('/myfun/api/create_user')
def create_user():
    i = ctx.request.input(phone='',password='',code='')
    phone = i.phone.strip()
    password = i.password.strip()
    code = i.code.strip()
    verify = VerifyCode.find_first('where num=?', phone)
    logging.info('the code %s and verify %s' %(code,verify))
    if not verify or verify.code!=code:
        raise APIError('register:failed','verify code','verify code is not correct.','-1')
    if time.time() - verify.created_at > 90:
        raise APIValueError('code',errcode='-3')
    
    if not phone or not _RE_PHONE.match(phone):
        raise APIValueError('phone',errcode='-1')
    if not password:
        raise APIValueError('password', errcode='-1')

    verify.delete()
    user = User.find_first('where phone=?',phone)
    if user and user.valid==True:
        raise APIError('register:failed','phone','phone is already in use.')

    if user:
        token = Token.find_first('where id=?', user.id)
        if not token:
            token_string = next_id()
            token = Token(id = user.id, token1=token_string, token2 = token_string)
            token.insert()
        else:
            token.token1 = next_id()
            logging.info('the update token is %s' % token.token1)
            token.update()
        user.password = password
        user.update()
        user.token = token.token1
    else:
        user = User(phone=phone, valid=False, password=password)
        user.insert()
        token_string = next_id()
        token = Token(id = user.id, token1=token_string, token2 = token_string)
        token.insert()
        user.token = token.token1
    user.pop('id')
    user.pop('password')
    user.pop('created_at')
    user.errcode='0'
    return user
@api
@post('/myfun/api-token/update_user')
def register_user():
    i = ctx.request.input(name='', female='',image='',token='')
    name = i.name.strip()
    female = i.female.strip().lower()
    image = i.image.strip()
    token = i.token.strip()
    if not name:
        raise APIValueError('name',errcode='-1')
    if not image:
        raise APIValueError('image')
    if not token:
        raise APIValueError('token',errcode='-1')
    user = ctx.request.user
    logging.info('the user is %s' % user.id)
    if not user:
        raise APIError('Authencation:fail','token','token is wrong','-1')
    logging.info('the user is %s' % user.id)
    is_female = False
    logging.info('the user valid %d' % user.valid)
    if user and user.valid == True:
        logging.info('valid  dddd')
        raise APIError('register:failed', 'phone', 'phone is already in use.','-1')
	if female == 'male':
		is_female = False
	elif female == 'female':
		is_female = True
	else:
		raise APIValueError('female')  
    user.valid = True
    user.image = image
    user.female = is_female
    user.name = name
    user.update()
    # make session cookie:
    user.pop('password')
    user.pop('created_at')
    user.pop('id')
    user.errcode='0'
    user['token'] = token
    return user

@api
@get('/myfun/api/users')
def api_get_users():
    cwd = os.getcwd()
    total = User.count_all()
    page = Page(total, _get_page_index())
    users = User.find_by('order by created_at desc limit ?,?', page.offset, page.limit)
    logging.info(str(users))
    for u in users:
        u.password = '******'
    return dict(users=users, page=page,cwd=image_path)
