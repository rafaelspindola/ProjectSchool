DROP TABLE IF EXISTS User;

CREATE TABLE User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL
);

DROP TABLE IF EXISTS Course;

CREATE TABLE Course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(500)
);

DROP TABLE IF EXISTS course_enrollment;

CREATE TABLE course_enrollment (
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    email VARCHAR(100) NOT NULL,
    enrolled_on TIMESTAMP NOT NULL,
    number_of_enrollments INT NOT NULL,
    PRIMARY KEY (course_id, user_id),
    CONSTRAINT FK_course_enrollment_course FOREIGN KEY (course_id) REFERENCES Course (id),
    CONSTRAINT FK_course_enrollment_user FOREIGN KEY (user_id) REFERENCES User (id)
    CONSTRAINT uc_course_user_unique UNIQUE (course_id, user_id)
);
