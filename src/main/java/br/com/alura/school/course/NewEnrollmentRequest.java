package br.com.alura.school.course;

import br.com.alura.school.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;

public class NewEnrollmentRequest {

    @Size(max=20)
    @NotBlank
    @JsonProperty
    private String username;

    @NotBlank
    @Email
    @JsonProperty
    private String email;

    @JsonProperty
    private Date enrollmentDate;

    public NewEnrollmentRequest(String username, String email, Date enrollmentDate) {
        this.username = username;
        this.email = email;
        this.enrollmentDate = enrollmentDate;
    }

    public NewEnrollmentRequest() {}

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
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

    public void setEmail(String email) {
        this.email = email;
    }

    Enrollment toEntity(Course course, User user) {
        return new Enrollment(course, user, enrollmentDate);
    }
}
