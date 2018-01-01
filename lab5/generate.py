from random import randint

names = ['Ron Weasley', 'Hermione Granger', 'Harry Potter', 'Lord Voldemort', 'Severus Snape', 'Draco Malfoy', 'Sirius Black']

## Generate posts
for i in range(7):
    print "insert into post values(nextval('post_seq'),'000" + str(i + 1) + "',now()," + "'My name is " + names[i] + "');"

## Generate comments
for i in range(20):
    n = randint(1, 7);
    m = randint(1,7);
    if i % 2 == 1:
        print "insert into comment values(nextval('comment_seq'),'" + str(n) + "','000" + str(m) + "',now(), 'Come dance with me!');";
    else:
        print "insert into comment values(nextval('comment_seq'),'" + str(n) + "','000" + str(m) + "',now(), 'Dance with " + names[i%7] + "!');";