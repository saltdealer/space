from django.db import models

# Create your models here.


class Task(models.Model):
    name = models.CharField(max_length=30)
    target = models.CharField(max_length=150)
    task_type = models.DecimalField(max_digits=3, decimal_places=2)
    website = models.URLField( blank=True)
    created_time =  models.DateField( blank=True,null=True)
    start_time = models.DateField( blank=True ,null = True)
    end_time =  models.DateField( blank=True,null= True)
    task_status = models.DecimalField(max_digits=3,decimal_places=2)
    user = models.CharField(max_length=30)
    scrawl_deep = models.DecimalField(max_digits=10,decimal_places=2)
    
    def __unicode__(self):
        return self.name

class Task_url(models.Model):
    url =  models.CharField(max_length=1000)
    task = models.ForeignKey(Task)
    valid = models.BooleanField()
    descript =  models.CharField(max_length=150)


