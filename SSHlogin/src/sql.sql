/*建表users*/
create table users(
id int auto_increment primary key,
name varchar(10),
password varchar(10)
)ENGINE=INNODB,charset=utf8;
/*初始化*/
insert into users(name,password) values('zhangsan','666');
