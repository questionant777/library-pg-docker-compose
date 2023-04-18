insert into author (id, name) values (101, 'Kristi');
insert into author (id, name) values (102, 'Shakespeare');
insert into author (id, name) values (103, 'Prishvin');

insert into genre (id, name) values (102, 'detective');
insert into genre (id, name) values (103, 'piece');
insert into genre (id, name) values (104, 'novella');

insert into book (name, authorid, genreid) values
  ( 'Puaro', 101, 102)       , ( 'Puaro. Part1', 101, 102)
, ( 'Puaro. Part2', 101, 102), ( 'Puaro. Part3', 101, 102), ( 'Puaro. Part4', 101, 102)
, ( 'Puaro. Part5', 101, 102), ( 'Puaro. Part6', 101, 102), ( 'Puaro. Part7', 101, 102)
, ( 'Puaro. Part8', 101, 102), ( 'Puaro. Part9', 101, 102), ( 'Puaro. Part10', 101, 102)

, ( 'piece. Part1', 102, 103), ( 'piece. Part2', 102, 103), ( 'piece. Part3', 102, 103)

, ( 'novella. Part1', 103, 104), ( 'novella. Part2', 103, 104), ( 'novella. Part3', 103, 104)
;

insert into bookcomment(comment, bookid) values ('comment11', 2)
,('comment12', 2)
,('comment13', 2)
,('comment14', 2)
,('comment15', 2)
,('comment16', 2)
,('comment17', 2)
,('comment31', 3)
,('comment32', 3)
,('comment33', 3)
,('comment34', 3)
,('comment35', 3)
,('comment36', 3)
;