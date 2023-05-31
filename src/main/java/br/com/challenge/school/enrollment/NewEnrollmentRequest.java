package br.com.challenge.school.enrollment;

import br.com.challenge.school.course.Course;
import br.com.challenge.school.users.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;

// This class was created to generate the enrollment request
public class NewEnrollmentRequest {

    @Size(max=20)
    @NotBlank
    @JsonProperty
    private String username;

    @JsonProperty
    private Date enrollmentDate = Date.from(Instant.now());

    public NewEnrollmentRequest(String username, Date enrollmentDate) {
        this.username = username;
        this.enrollmentDate = enrollmentDate;
    }

    public NewEnrollmentRequest() {}

    public String getUsername() {
        return username;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = Date.from(Instant.now());
    }

    public void setUsername(String username) {
        this.username = username;
    }

    Enrollment toEntity(Course course, User user) {
        return new Enrollment(course, user, enrollmentDate);
    }
}
