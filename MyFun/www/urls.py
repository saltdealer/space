#!/usr/bin/env python
# -*- coding: utf-8 -*-

__author__ = 'Michael Liao'

import os, re, time, base64, hashlib, logging

import markdown2

from transwarp.web import get, post, ctx, view, interceptor, seeother, notfound
from transwarp.db import next_id

from apis import api, Page, APIError, APIValueError, APIPermissionError, APIResourceNotFoundError, next_code
from models import User ,Token,VerifyCode
from config import configs

_COOKIE_NAME = 'awesession'
_COOKIE_KEY = configs.session.secret

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

@interceptor('/')
def user_interceptor(next):
    logging.info('try to bind user from session cookie...')
    user = None
    cookie = ctx.request.cookies.get(_COOKIE_NAME)
    if cookie:
        logging.info('parse session cookie...')
        user = parse_signed_cookie(cookie)
        if user:
            logging.info('bind user <%s> to session...' % user.email)
    ctx.request.user = user
    return next()

@interceptor('/manage/')
def manage_interceptor(next):
    user = ctx.request.user
    if user and user.admin:
        return next()
    raise seeother('/signin')

@view('test.html')
@get('/test')
def test():

    return {'string':3} 

@view('blogs.html')
@get('/')
def index():
    blogs, page,cat = _get_blogs_by_page()
    size = len(blogs)
    return dict(page=page, blogs=blogs, user=ctx.request.user,size=size,cat=cat)

@view('blog.html')
@get('/blog/:blog_id')
def blog(blog_id):
    blog = Blog.get(blog_id)
    if blog is None:
        raise notfound()
    blog.html_content = markdown2.markdown(blog.content)
    comments = Comment.find_by('where blog_id=? order by created_at desc limit 1000', blog_id)
    return dict(blog=blog, comments=comments, user=ctx.request.user)

@view('signin.html')
@get('/signin')
def signin():
    return dict()

@get('/signout')
def signout():
    ctx.response.delete_cookie(_COOKIE_NAME)
    raise seeother('/')

@api
@post('/api/authenticate')
def authenticate():
    i = ctx.request.input(remember='')
    email = i.email.strip().lower()
    password = i.password
    remember = i.remember
    user = User.find_first('where email=?', email)
    if user is None:
        raise APIError('auth:failed', 'email', 'Invalid email.')
    elif user.password != password:
        raise APIError('auth:failed', 'password', 'Invalid password.')
    # make session cookie:
    max_age = 604800 if remember=='true' else None
    cookie = make_signed_cookie(user.id, user.password, max_age)
    ctx.response.set_cookie(_COOKIE_NAME, cookie, max_age=max_age)
    user.password = '******'
    return user

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
    i = ctx.request.input()
    user = i.user
    logging.info('the token is %s' % i.token)
    logging.info('the file is %s'% (i.file))
    return {}

@interceptor('/myfun/api-token/')
def user_interceptor(next):
    logging.info('try to bind user from token')
    i = ctx.request.input(token='',phone='')
    token = i.token.strip()
    phone = i.phone.strip()
    ctx.request.user = None
    if not phone or not _RE_PHONE.match(phone):
        return next()
    user = User.find_first('where phone=?',phone)
    if not user:
        return next()
    token_verify = Token.find_first('where id=?',user.id)
    if token_verify.token1 != token:
        return next()
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
        raise APIError('duplicated:phone','phone','phone is already in use.')
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
    logging.info('the phone is %s ' % phone)
    if not name:
        raise APIValueError('name',errcode='-1')
    if not image:
        raise APIValueError('image')
    if not token:
        raise APIValueError('token',errcode='-1')
    user = ctx.request.user
    if not user:
        raise APIError('Authencation:fail','token','token is wrong','-1')
    is_female = False
    logging.info('the user valid %d' % user.valid)
    if user and user.valid == True:
        raise APIError('register:failed', 'phone', 'phone is already in use.')
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
    user['token'] = token
    return user

@view('register.html')
@get('/register')
def register():
    return dict()

def _get_blogs_by_page():
    cat = int(ctx.request.get('cat', '0'))
    if cat!=0:
        total = Blog.count_by('where category=?',cat)
        page = Page(total, _get_page_index())
        blogs = Blog.find_by('where category=? order by created_at desc limit ?,?',cat, page.offset, page.limit)
    else:
        total = Blog.count_all()
        page = Page(total, _get_page_index())
        blogs = Blog.find_by('order by created_at desc limit ?,?', page.offset, page.limit)

    return blogs, page, cat

@get('/manage/')
def manage_index():
    raise seeother('/manage/comments')

@view('manage_comment_list.html')
@get('/manage/comments')
def manage_comments():
    return dict(page_index=_get_page_index(), user=ctx.request.user)

@view('manage_blog_list.html')
@get('/manage/blogs')
def manage_blogs():
    return dict(page_index=_get_page_index(), user=ctx.request.user)

@view('manage_links_list.html')
@get('/manage/links')
def manage_links():
    return dict(page_index=_get_page_index(), user=ctx.request.user)

@view('manage_blog_edit.html')
@get('/manage/blogs/create')
def manage_blogs_create():
    return dict(id=None, action='/api/blogs', redirect='/manage/blogs', user=ctx.request.user)

@view('manage_blog_edit.html')
@get('/manage/blogs/edit/:blog_id')
def manage_blogs_edit(blog_id):
    blog = Blog.get(blog_id)
    if blog is None:
        raise notfound()
    return dict(id=blog.id, name=blog.name, summary=blog.summary, content=blog.content, action='/api/blogs/%s' % blog_id, redirect='/manage/blogs', user=ctx.request.user)

@view('manage_user_list.html')
@get('/manage/users')
def manage_users():
    return dict(page_index=_get_page_index(), user=ctx.request.user)

@api
@get('/api/blogs')
def api_get_blogs():
    format = ctx.request.get('format', '')
    blogs, page ,cat = _get_blogs_by_page()
    if format=='html':
        for blog in blogs:
            blog.content = markdown2.markdown(blog.content)
    return dict(blogs=blogs, page=page)

@api
@get('/api/blogs/:blog_id')
def api_get_blog(blog_id):
    blog = Blog.get(blog_id)
    if blog:
        return blog
    raise APIResourceNotFoundError('Blog')

@api
@post('/api/blogs')
def api_create_blog():
    check_admin()
    i = ctx.request.input(name='', summary='', content='',category='')
    name = i.name.strip()
    summary = i.summary.strip()
    content = i.content.strip()
    category = i.category.strip()
    if not name:
        raise APIValueError('name', 'name cannot be empty.')
    if not summary:
        raise APIValueError('summary', 'summary cannot be empty.')
    if not content:
        raise APIValueError('content', 'content cannot be empty.')
    user = ctx.request.user
    blog = Blog(user_id=user.id, user_name=user.name, name=name, summary=summary, content=content, user_image=user.image,category=category)
    blog.insert()
    return blog

@api
@post('/api/blogs/:blog_id')
def api_update_blog(blog_id):
    check_admin()
    i = ctx.request.input(name='', summary='', content='',category='')
    name = i.name.strip()
    summary = i.summary.strip()
    content = i.content.strip()
    category = i.category.strip()
    if not name:
        raise APIValueError('name', 'name cannot be empty.')
    if not summary:
        raise APIValueError('summary', 'summary cannot be empty.')
    if not content:
        raise APIValueError('content', 'content cannot be empty.')
    blog = Blog.get(blog_id)
    if blog is None:
        print 'sdfdsf'
    blog.name = name
    blog.summary = summary
    blog.content = content
    blog.category =category
    blog.update()
    return blog

@api
@post('/api/blogs/:blog_id/delete')
def api_delete_blog(blog_id):
    check_admin()
    blog = Blog.get(blog_id)
    if blog is None:
        raise APIResourceNotFoundError('Blog')
    blog.delete()
    return dict(id=blog_id)

@api
@post('/api/blogs/:blog_id/comments')
def api_create_blog_comment(blog_id):
    user = ctx.request.user
    if user is None:
        raise APIPermissionError('Need signin.')
    blog = Blog.get(blog_id)
    if blog is None:
        raise APIResourceNotFoundError('Blog')
    content = ctx.request.input(content='').content.strip()
    if not content:
        raise APIValueError('content')
    c = Comment(blog_id=blog_id, user_id=user.id, user_name=user.name, user_image=user.image, content=content)
    c.insert()
    return dict(comment=c)

@api
@post('/api/comments/:comment_id/delete')
def api_delete_comment(comment_id):
    check_admin()
    comment = Comment.get(comment_id)
    if comment is None:
        raise APIResourceNotFoundError('Comment')
    comment.delete()
    return dict(id=comment_id)

@api
@get('/api/comments')
def api_get_comments():
    total = Comment.count_all()
    page = Page(total, _get_page_index())
    comments = Comment.find_by('order by created_at desc limit ?,?', page.offset, page.limit)
    return dict(comments=comments, page=page)

@api
@get('/myfun/api/users')
def api_get_users():
    total = User.count_all()
    page = Page(total, _get_page_index())
    users = User.find_by('order by created_at desc limit ?,?', page.offset, page.limit)
    logging.info(str(users))
    for u in users:
        u.password = '******'
    return dict(users=users, page=page)
