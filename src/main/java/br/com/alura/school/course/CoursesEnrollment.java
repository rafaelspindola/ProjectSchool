package br.com.alura.school.course;

import br.com.alura.school.user.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity(name = "CoursesEnrollment")
@Table(name = "courses_enrollment")
public class CoursesEnrollment {


    @EmbeddedId
    private CoursesEnrollmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @Size(max=20)
    @Column(name = "name", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "enrolled_on")
    private Date enrollmentDate = new Date();

    @Column(name = "number_of_enrollments")
    private int numberOfEnrollments;

    private CoursesEnrollment() {}

    public CoursesEnrollmentId getId() {
        return id;
    }

    public void setId(CoursesEnrollmentId id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public int getNumberOfEnrollments() {
        return numberOfEnrollments;
    }

    public void setNumberOfEnrollments(int numberOfEnrollments) {
        this.numberOfEnrollments = numberOfEnrollments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CoursesEnrollment that = (CoursesEnrollment) o;
        return Objects.equals(course, that.course) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, user);
    }
}

