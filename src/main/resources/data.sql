insert into User (username, email) values ('alex', 'alex@email.com');
insert into User (username, email) values ('ana', 'ana@email.com');

insert into Course (code, name, description) values ('java-1', 'Java OO', 'Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.');
insert into Course (code, name, description) values ('java-2', 'Java Collections', 'Java Collections: Lists, Sets, Maps and more.');

-- Get the user_id and course_id values for the relevant records
select u.id as user_id, c.id as course_id
from User u, Course c
where u.username = 'alex' and c.code = 'java-1';

insert into course_enrollment (course_id, user_id, email, enrolled_on, number_of_enrollments) values (1, 1, 'alex@email.com', TIMESTAMP '2022-02-26', 1);