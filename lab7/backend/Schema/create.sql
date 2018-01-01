create sequence if not exists postid start 1;
create sequence if not exists commentid start 1;

CREATE TABLE "user"(
	uid			VARCHAR(20),
	name		VARCHAR(20) not null,
	email		VARCHAR(30),
	PRIMARY KEY (uid)
);

CREATE TABLE password(
	id			VARCHAR(20),
	password	VARCHAR(20),
	PRIMARY KEY (id),
	FOREIGN KEY (id) REFERENCES "user"(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE follows(
	uid1		VARCHAR(20),
	uid2		VARCHAR(20),
	PRIMARY KEY (uid1, uid2),
	FOREIGN KEY (uid1) REFERENCES "user"(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (uid2) REFERENCES "user"(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

CREATE TABLE post (
	postid int primary key default nextval('postid'),
	uid			VARCHAR(20),
	timestamp	TIMESTAMP,
	text		TEXT,
	FOREIGN KEY (uid) REFERENCES "user"(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);



CREATE TABLE comment(
	commentid int primary key default nextval('commentid'),
	postid int references post on delete cascade,
	uid			VARCHAR(20),
	timestamp	TIMESTAMP,
	text		TEXT,
	FOREIGN KEY (uid) REFERENCES "user"(uid)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (postid) REFERENCES post(postid)
		ON DELETE CASCADE
		ON UPDATE CASCADE
);

insert into "user" values ('00128', 'Zhang', 'user1@gmail.com');
insert into "user" values ('12345', 'Shankar', 'user2@gmail.com');
insert into "user" values ('19991', 'Brandt', 'user3@gmail.com');
insert into "user" values ('23121', 'Chavez', 'user4@gmail.com');
insert into "user" values ('44553', 'Peltier', 'user5@gmail.com');

insert into password values ('00128', 'user1');
insert into password values ('12345', 'user2');
insert into password values ('19991', 'user3');
insert into password values ('23121', 'user4');
insert into password values ('44553', 'user5');

insert into follows values ('00128', '12345');
insert into follows values ('00128', '19991');
insert into follows values ('00128', '44553');
insert into follows values ('12345', '00128');
insert into follows values ('12345', '23121');
insert into follows values ('23121', '12345');
insert into follows values ('23121', '00128');
insert into follows values ('44553', '00128');
insert into follows values ('44553', '12345');
insert into follows values ('44553', '19991');
insert into follows values ('44553', '23121');

