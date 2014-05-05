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

    name = forms.CharField( required=True,max_length=60, label='task name',widget=forms.TextInput(attrs={'id':'name-input','class':'task_name','placeholder':'input task_name'} ) )
    target = forms.URLField( required=True,max_length=60, label='target',widget=forms.TextInput(attrs={'id':'target-input','class':'target','placeholder':'input target'} ) )
    choice = forms.ChoiceField(label='Task Type', choices=((1,'检测页面链接联通'),(2,'爬取链接')   ))
    deep =   forms.DecimalField(required=True, max_digits=10, decimal_places=2,label='scrawl deep', widget=forms.NumberInput(attrs={'id':'deep_input','class':'deep_input','placeholder':'input scrawl deep'}))
    
    def clean_message(self):
        message = self.cleaned_data['target']

        return message
