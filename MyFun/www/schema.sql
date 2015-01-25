-- init database

drop database if exists myfun;

create database myfun;

use myfun;

grant select, insert, update, delete on myfun.* to 'root'@'localhost' identified by 'xj';

create table users (
    `id` varchar(50) not null,
    `phone` varchar(11) not null,
    `password` varchar(50) not null,
    `female` bool ,
    `name` varchar(50) ,
    `image` varchar(500) ,
    `created_at` real not null,
    `valid` bool not null,
    unique key `idx_phone` (`phone`),
    key `idx_created_at` (`created_at`),
    primary key (`id`)
) engine=innodb default charset=utf8;


-- email / password:
-- admin@example.com / password

INSERT INTO `users` VALUES ('0010018336417540987fff4508f43fbaed718e263442526000','15710029792','30a756507840db3af6a8645c15c485e0',1,'GoodOak','http://ww4.sinaimg.cn/mw690/679a7135jw1ejvsmtdv37j204q045wed.jpg',1402909113.628,0);


create table tokens (
    `id` varchar(50) not null,
	`token1` varchar(50) not null,
	`token2` varchar(50) not null,
	primary key (`id`)
) engine=innodb default charset=utf8;

create table verify_code (
        `id` varchar(50) not null,
        `num` varchar(11) not null,
        `code` varchar(5) not null,
        `created_at` real not null,
        unique key `idx_num` (`num`),
        primary key (`id`)
) engine=innodb default charset=utf8;

create table image_id (
        `id` real not null AUTO_INCREMENT,
        `image_path` varchar(100) not null,
        `user_id` varchar(50) not null,
        primary key (`id`)
) AUTO_INCREMENT=111 engine=innodb default charset=utf8;


