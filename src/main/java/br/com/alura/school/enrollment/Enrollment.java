package br.com.alura.school.enrollment;

import br.com.alura.school.course.Course;
import br.com.alura.school.user.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

// Essa classe foi criada para representar o objeto da matrícula
@Entity(name = "Enrollment")
@Table(name = "course_enrollment")
public class Enrollment {


    @EmbeddedId
    @GeneratedValue
    private EnrollmentId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("user_id")
    private User user;

    @Column(name = "enrolled_on")
    @DateTimeFormat
    private Date enrollmentDate = new Date();

    public Enrollment(Course course, User user) {
        this.course = course;
        this.user = user;
        this.id = new EnrollmentId(course.getId(), user.getId());
    }

    public Enrollment(Course course, User user, Date enrollmentDate) {
        this.user = user;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.id = new EnrollmentId(course.getId(), user.getId());
    }

    private Enrollment() {}

    public EnrollmentId getId() {
        return id;
    }

    public void setId(EnrollmentId id) {
        this.id = id;
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

    public Date getEnrollmentDate() {
        return enrollmentDate;
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

