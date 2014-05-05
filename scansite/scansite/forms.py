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

class TaskForm(forms.Form):
    target = forms.URLField( required=True,max_length=60, label='target',widget=forms.TextInput(attrs={'id':'target-input','class':'target','placeholder':'input target'} ) )
    choice = forms.ChoiceField(label='Task Type', choices=((1,'检测页面链接联通'),(2,'爬取链接')   ))
    
    def clean_message(self):
        message = self.cleaned_data['target']

        return message
