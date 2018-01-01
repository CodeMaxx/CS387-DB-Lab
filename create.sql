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
insert into users values('0004', 'Lord Voldemort', 'voldy@hp.magic');
insert into users values('0005', 'Severus Snape', 'halfbloodprince@hp.magic');
insert into users values('0006', 'Draco Malfoy', 'dmalfoy@hp.magic');
insert into users values('0007', 'Sirius Black', 'godfather@hp.magic');

insert into password select uid, uid from users;

insert into follows values('0001', '0002');
insert into follows values('0001', '0003');
insert into follows values('0001', '0004');
insert into follows values('0001', '0005');
insert into follows values('0001', '0006');
insert into follows values('0001', '0007');
insert into follows values('0002', '0005');
insert into follows values('0002', '0007');
insert into follows values('0002', '0001');
insert into follows values('0003', '0002');
insert into follows values('0003', '0004');
insert into follows values('0003', '0001');
insert into follows values('0003', '0006');
insert into follows values('0004', '0002');
insert into follows values('0004', '0004');
insert into follows values('0005', '0002');
insert into follows values('0005', '0005');
insert into follows values('0006', '0002');
insert into follows values('0006', '0004');
insert into follows values('0007', '0003');
insert into follows values('0007', '0005');

create sequence post_seq;
create sequence comment_seq;

insert into post values(nextval('post_seq'),'0001',now(),'My name is Ron Weasley');
insert into post values(nextval('post_seq'),'0002',now(),'My name is Hermione Granger');
insert into post values(nextval('post_seq'),'0003',now(),'My name is Harry Potter');
insert into post values(nextval('post_seq'),'0004',now(),'My name is Lord Voldemort');
insert into post values(nextval('post_seq'),'0005',now(),'My name is Severus Snape');
insert into post values(nextval('post_seq'),'0006',now(),'My name is Draco Malfoy');
insert into post values(nextval('post_seq'),'0007',now(),'My name is Sirius Black');
insert into post values(nextval('post_seq'),'0001',now(),'My name is Ron Weasley');
insert into post values(nextval('post_seq'),'0002',now(),'My name is Hermione Granger');
insert into post values(nextval('post_seq'),'0003',now(),'My name is Harry Potter');
insert into post values(nextval('post_seq'),'0004',now(),'My name is Lord Voldemort');
insert into post values(nextval('post_seq'),'0005',now(),'My name is Severus Snape');
insert into post values(nextval('post_seq'),'0006',now(),'My name is Draco Malfoy');
insert into post values(nextval('post_seq'),'0007',now(),'My name is Sirius Black');
insert into post values(nextval('post_seq'),'0001',now(),'My name is Ron Weasley');
insert into post values(nextval('post_seq'),'0002',now(),'My name is Hermione Granger');
insert into post values(nextval('post_seq'),'0003',now(),'My name is Harry Potter');
insert into post values(nextval('post_seq'),'0004',now(),'My name is Lord Voldemort');
insert into post values(nextval('post_seq'),'0005',now(),'My name is Severus Snape');
insert into post values(nextval('post_seq'),'0006',now(),'My name is Draco Malfoy');
insert into post values(nextval('post_seq'),'0007',now(),'My name is Sirius Black');
insert into post values(nextval('post_seq'),'0001',now(),'My name is Ron Weasley');
insert into post values(nextval('post_seq'),'0002',now(),'My name is Hermione Granger');
insert into post values(nextval('post_seq'),'0003',now(),'My name is Harry Potter');
insert into post values(nextval('post_seq'),'0004',now(),'My name is Lord Voldemort');
insert into post values(nextval('post_seq'),'0005',now(),'My name is Severus Snape');
insert into post values(nextval('post_seq'),'0006',now(),'My name is Draco Malfoy');
insert into post values(nextval('post_seq'),'0007',now(),'My name is Sirius Black');
insert into post values(nextval('post_seq'),'0001',now(),'My name is Ron Weasley');
insert into post values(nextval('post_seq'),'0002',now(),'My name is Hermione Granger');
insert into post values(nextval('post_seq'),'0003',now(),'My name is Harry Potter');
insert into post values(nextval('post_seq'),'0004',now(),'My name is Lord Voldemort');
insert into post values(nextval('post_seq'),'0005',now(),'My name is Severus Snape');
insert into post values(nextval('post_seq'),'0006',now(),'My name is Draco Malfoy');
insert into post values(nextval('post_seq'),'0007',now(),'My name is Sirius Black');

insert into comment values(nextval('comment_seq'),'2','0004',now(), 'Dance with Ron Weasley!');
insert into comment values(nextval('comment_seq'),'7','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'5','0003',now(), 'Dance with Harry Potter!');
insert into comment values(nextval('comment_seq'),'7','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'7','0007',now(), 'Dance with Severus Snape!');
insert into comment values(nextval('comment_seq'),'6','0002',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'4','0005',now(), 'Dance with Sirius Black!');
insert into comment values(nextval('comment_seq'),'4','0007',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'6','0007',now(), 'Dance with Hermione Granger!');
insert into comment values(nextval('comment_seq'),'5','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'1','0007',now(), 'Dance with Lord Voldemort!');
insert into comment values(nextval('comment_seq'),'3','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'2','0002',now(), 'Dance with Draco Malfoy!');
insert into comment values(nextval('comment_seq'),'4','0001',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'4','0003',now(), 'Dance with Ron Weasley!');
insert into comment values(nextval('comment_seq'),'4','0003',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'7','0001',now(), 'Dance with Harry Potter!');
insert into comment values(nextval('comment_seq'),'1','0003',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'4','0005',now(), 'Dance with Severus Snape!');
insert into comment values(nextval('comment_seq'),'5','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'2','0004',now(), 'Dance with Ron Weasley!');
insert into comment values(nextval('comment_seq'),'7','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'5','0003',now(), 'Dance with Harry Potter!');
insert into comment values(nextval('comment_seq'),'7','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'7','0007',now(), 'Dance with Severus Snape!');
insert into comment values(nextval('comment_seq'),'6','0002',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'4','0005',now(), 'Dance with Sirius Black!');
insert into comment values(nextval('comment_seq'),'4','0007',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'6','0007',now(), 'Dance with Hermione Granger!');
insert into comment values(nextval('comment_seq'),'5','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'1','0007',now(), 'Dance with Lord Voldemort!');
insert into comment values(nextval('comment_seq'),'3','0006',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'2','0002',now(), 'Dance with Draco Malfoy!');
insert into comment values(nextval('comment_seq'),'4','0001',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'4','0003',now(), 'Dance with Ron Weasley!');
insert into comment values(nextval('comment_seq'),'4','0003',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'7','0001',now(), 'Dance with Harry Potter!');
insert into comment values(nextval('comment_seq'),'1','0003',now(), 'Come dance with me!');
insert into comment values(nextval('comment_seq'),'4','0005',now(), 'Dance with Severus Snape!');
insert into comment values(nextval('comment_seq'),'5','0006',now(), 'Come dance with me!');