//
//
// 
// 
// Created Time: Wed 09 Apr 2014 02:21:11 AM PDT
// 
// FileName:     override.c
// 
// Description:  
// 
#include <stdio.h>
#define RECTANGLE 0
#define ANGLE 1

typedef struct angle_struct{
    int t;  
    double a;  
    double b;
} angle_struct ;

typedef struct rectangle_struct {
    int t ;
    double a;
    double b;
}rectangle_struct;

double caculate_angle( double a,double b)
{
    return a*b/2;
}

double caculate_rectangle( double a, double b)
{
    return a*b;
}

// 函数指针表
double (*caculate_tbl[])(double , double ) = { caculate_rectangle , caculate_angle };

#define caculate_area(z) caculate_tbl[z.t](z.a, z.b)


int main()
{
    angle_struct angle;
    angle.t = ANGLE;
    angle.a=1;
    angle.b=2;
    rectangle_struct rectangle;
    rectangle.t = RECTANGLE;
    rectangle.a = 1;
    rectangle.b = 2;

    printf("the area of rectangle is %f and angle is %f \n", caculate_area(rectangle), caculate_area(angle));

    return 1;
}
