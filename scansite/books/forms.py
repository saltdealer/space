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

class ContactForm(forms.Form):
    subject = forms.CharField(max_length=28)
    email = forms.EmailField(required=False, label='your email address')
    message = forms.CharField( widget=forms.Textarea)
    
    def clean_message(self):
        message = self.cleaned_data['message']
        num_words = len(message.split())
        if num_words < 4:
            raise forms.ValidationError("Not enough words!")
        return message
