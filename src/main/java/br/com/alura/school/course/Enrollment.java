package br.com.alura.school.course;

import br.com.alura.school.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity(name = "Enrollment")
@Table(name = "course_enrollment")
public class Enrollment {


    @EmbeddedId
    private EnrollmentId id;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "enrolled_on")
    private Date enrollmentDate = new Date();

    @Column(name = "number_of_enrollments")
    private int numberOfEnrollments;

    public Enrollment(Course course, User user) {
        this.course = course;
        this.user = user;
    }

    public Enrollment(Course course, User user, String email, int numberOfEnrollments) {
        this.course = course;
        this.user = user;
        this.email = email;
        this.numberOfEnrollments = numberOfEnrollments;
        this.id = new EnrollmentId(course.getId(), user.getId());
    }

    private Enrollment() {}

    public EnrollmentId getId() {
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

        Enrollment that = (Enrollment) o;
        return Objects.equals(course, that.course) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, user);
    }
}

