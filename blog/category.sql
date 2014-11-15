-- init database



use blog;

grant select, insert, update, delete on blog.* to 'root'@'localhost' identified by 'xj';


create table category (
    `category_id` varchar(50) not null,
    `category` varchar(50) not null,
    primary key (`category_id`)
) engine=innodb default charset=utf8;

-- email / password:
-- admin@example.com / password

insert into category(`category_id`,`category`) values ( '1','学习笔记');
insert into category(`category_id`,`category`) values ( '2','生活记录');
insert into category(`category_id`,`category`) values ( '3','其他');
