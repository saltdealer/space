#!usr/bin/env python
#coding=utf-8
# Created Time: Tue 15 Apr 2014 12:02:50 PM PDT
# 
# FileName:     forms.py
# 
# Description:  
# 
# ChangeLog:

from django import forms

class LoginForm(forms.Form):
    username = forms.CharField(max_length=28, label='', widget=forms.TextInput(attrs={'id':'username-input','class':'username','placeholder':'input user name'} ) )
    password = forms.CharField(label='',widget=forms.PasswordInput(attrs={'placeholder':'input password','class':'password'} ))
    
    def clean_message(self):
        return 'none'
