create sequence if not exists postid start 1;
create sequence if not exists commentid start 1;
create sequence if not exists rand start 1;

CREATE OR REPLACE FUNCTION pseudo_encrypt(VALUE bigint) returns bigint AS $$
DECLARE
l1 bigint;
l2 bigint;
r1 bigint;
r2 bigint;
i bigint:=0;
BEGIN
 l1:= (VALUE >> 32) & 4294967295;
 r1:= VALUE & 4294967295;
 WHILE i < 3 LOOP
   l2 := r1;
   r2 := ((((1366.0 * r1 + 150889) % 714025) / 714025.0) * 32767)::bigint;
   l1 := l2;
   r1 := r2;
   i := i + 1;
 END LOOP;
 RETURN ((l1::bigint << 32) + r1);
END;
$$ LANGUAGE plpgsql strict immutable;


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
	imageid		VARCHAR(30),
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

CREATE TABLE images(
	id bigint, 
	image bytea
);

insert into "user" values ('zhang', 'Zhang', 'zhang@gmail.com');
insert into "user" values ('shankar', 'Shankar', 'shankar@gmail.com');
insert into "user" values ('brandt', 'Brandt', 'brandt@gmail.com');
insert into "user" values ('chavez', 'Chavez', 'chavez@gmail.com');
insert into "user" values ('peltier', 'Peltier', 'peltier@gmail.com');

insert into password values ('zhang', 'user1');
insert into password values ('shankar', 'user2');
insert into password values ('brandt', 'user3');
insert into password values ('chavez', 'user4');
insert into password values ('peltier', 'user5');

insert into follows values ('zhang', 'shankar');
insert into follows values ('zhang', 'brandt');
insert into follows values ('zhang', 'peltier');
insert into follows values ('shankar', 'zhang');
insert into follows values ('shankar', 'chavez');
insert into follows values ('chavez', 'shankar');
insert into follows values ('chavez', 'zhang');
insert into follows values ('peltier', 'zhang');
insert into follows values ('peltier', 'shankar');
insert into follows values ('peltier', 'brandt');
insert into follows values ('peltier', 'chavez');

