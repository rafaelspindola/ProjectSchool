insert into Users (username, email) values ('alex', 'alex@email.com');
insert into Users (username, email) values ('ana', 'ana@email.com');

insert into Course (code, name, description) values ('java-1', 'Java OO', 'Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.');
insert into Course (code, name, description) values ('java-2', 'Java Collections', 'Java Collections: Lists, Sets, Maps and more.');

insert into course_enrollment (user_id, course_id, enrolled_on) values (1, 1, CURRENT_TIMESTAMP);
insert into course_enrollment (user_id, course_id, enrolled_on) values (1, 2, CURRENT_TIMESTAMP);

