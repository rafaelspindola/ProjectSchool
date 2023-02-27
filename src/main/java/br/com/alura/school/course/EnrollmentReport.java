package br.com.alura.school.course;

import br.com.alura.school.support.validation.Unique;
import br.com.alura.school.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrollmentReport {

    @Unique(entity = User.class, field = "email")
    @JsonProperty
    private String email;
    @JsonProperty
    private int enrollmentCount;

    @Unique(entity = User.class, field = "username")
    @JsonProperty
    private String username;

    public EnrollmentReport(String username, int enrollmentCount, String email) {
        this.email = email;
        this.enrollmentCount = enrollmentCount;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(int enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
