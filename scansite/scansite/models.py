from django.db import models

# Create your models here.


class Task(models.Model):
    name = models.CharField(max_length=30)
    target = models.CharField(max_length=150)
    task_type = models.DecimalField(max_digits=3)
    website = models.URLField()
    created_time =  models.DateField()
    start_time models.DateField()
    end_time =  models.DateField()
    task_status = models.DecimalField(max_digits=3)
    user = models.CharField(max_length=30)
    
    def __unicode__(self):
        return self.name

class task_url(models.Model):
    url =  models.CharField(max_length=1000)
    task = models.ForeignKey(Task)
    valid = models.BooleanField()
    descript =  models.CharField(max_length=150)


