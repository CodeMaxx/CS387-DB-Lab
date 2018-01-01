drop table users cascade;
drop table password;
drop table post cascade;
drop table comment;
drop table follows;
drop sequence post_seq;
drop sequence comment_seq;


create table users(
	uid	varchar(20),
	name	varchar(100),
	email	varchar(50),
	primary key(uid)
);


 create table password(
     uid varchar(20),
     pass    varchar(20),
     primary key(uid),
     foreign key(uid) references users
     	on delete cascade
 );


create table follows(
	uid1	varchar(20),
	uid2	varchar(20),
	primary key(uid1, uid2),
	foreign key(uid1) references users
		on delete cascade,
	foreign key(uid2) references users
		on delete cascade
);


create table post(
	postid	varchar(20),
	uid 	varchar(20),
	time_stamp	timestamp,
	text	varchar(140),
	primary key(postid),
	foreign key(uid) references users
		on delete cascade
);


create table comment(
	commentid 	varchar(20),
	postid	varchar(20),
	uid 	varchar(20),
	time_stamp	timestamp,
	text	varchar(140),
	primary key(commentid),
	foreign key(postid) references post
		on delete cascade,
	foreign key(uid) references users
		on delete cascade
);

insert into users values('0001', 'Ron Weasley', 'ron@hp.magic');
insert into users values('0002', 'Hermione Granger', 'nerd@hp.magic');
insert into users values('0003', 'Harry Potter', 'theboywholived@hp.magic');

insert into password select uid, uid from users;

insert into follows values('0001', '0002');
insert into follows values('0001', '0003');
insert into follows values('0002', '0001');
insert into follows values('0003', '0002');

create sequence post_seq;
create sequence comment_seq;
