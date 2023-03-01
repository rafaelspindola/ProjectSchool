package br.com.alura.school.enrollment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

// Considering the many to many relatioship between user and course entities this class was created to represent the composite primary key of both these classes
@Embeddable
public class EnrollmentId
        implements Serializable {
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "user_id")
    private Long userId;

    public EnrollmentId() {}

    public EnrollmentId(
            Long courseId,
            Long userId) {
        this.courseId = courseId;
        this.userId = userId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        EnrollmentId that = (EnrollmentId) o;
        return Objects.equals(courseId, that.courseId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, userId);
    }
}
