package br.com.alura.school.course;

import br.com.alura.school.user.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity(name = "CourseEnrollment")
@Table(name = "courses_enrollment")
public class CourseEnrollment {


    @EmbeddedId
    private CourseEnrollmentId id;

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

    public CourseEnrollment(Course course, User user) {
        this.course = course;
        this.user = user;
        this.username = username;
        this.email = email;
        this.numberOfEnrollments = numberOfEnrollments;
        this.id = new CourseEnrollmentId(course.getId(), user.getId());
    }

    private CourseEnrollment() {}

    public CourseEnrollmentId getId() {
        return id;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public int getNumberOfEnrollments() {
        return numberOfEnrollments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CourseEnrollment that = (CourseEnrollment) o;
        return Objects.equals(course, that.course) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, user);
    }
}

