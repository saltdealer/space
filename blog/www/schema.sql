-- init database

drop database if exists blog;

create database blog;

use blog;

grant select, insert, update, delete on blog.* to 'root'@'localhost' identified by 'xj';

    create table users (
            `id` varchar(50) not null,
            `email` varchar(50) not null,
            `password` varchar(50) not null,
            `admin` bool not null,
            `name` varchar(50) not null,
            `image` varchar(500) not null,
            `created_at` real not null,
            unique key `idx_email` (`email`),
            key `idx_created_at` (`created_at`),
            primary key (`id`)
            ) engine=innodb default charset=utf8;

    create table blogs (
            `id` varchar(50) not null,
            `user_id` varchar(50) not null,
            `user_name` varchar(50) not null,
            `user_image` varchar(500) not null,
            `name` varchar(50) not null,
            `summary` varchar(200) not null,
            `content` mediumtext not null,
            `category` bigint not null,
            `created_at` real not null,
            key `idx_created_at` (`created_at`),
            primary key (`id`)
            ) engine=innodb default charset=utf8;

    create table comments (
            `id` varchar(50) not null,
            `blog_id` varchar(50) not null,
            `user_id` varchar(50) not null,
            `user_name` varchar(50) not null,
            `user_image` varchar(500) not null,
            `content` mediumtext not null,
            `created_at` real not null,
            key `idx_created_at` (`created_at`),
            primary key (`id`)
            ) engine=innodb default charset=utf8;

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


    -- email / password:
    -- admin@example.com / password

    insert into users (`id`, `email`, `password`, `admin`, `name`, `created_at`,`image`) values ('0010018336417540987fff4508f43fbaed718e263442526000', 'jjxx2004@gmail.com', '30a756507840db3af6a8645c15c485e0', 1, 'GoodOak', 1402909113.628,"http://ww4.sinaimg.cn/mw690/679a7135jw1ejvsmtdv37j204q045wed.jpg");
