DROP TABLE IF EXISTS Users cascade;

CREATE TABLE Users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL
);

DROP TABLE IF EXISTS Course cascade;

CREATE TABLE Course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(500)
);

DROP TABLE IF EXISTS course_enrollment cascade;

CREATE TABLE course_enrollment (
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    enrolled_on TIMESTAMP(0),
    PRIMARY KEY (course_id, user_id),
    CONSTRAINT FK_course_enrollment_course FOREIGN KEY (course_id) REFERENCES Course (id),
    CONSTRAINT FK_course_enrollment_users FOREIGN KEY (user_id) REFERENCES Users (id)
);
