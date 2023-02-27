package br.com.alura.school.course;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class EnrollmentResponse {
    @JsonProperty
    private final String username;

    @JsonProperty
    private final String email;

    @JsonProperty
    private final Date enrollmentDate;


    public EnrollmentResponse(Enrollment enrollment) {
        this.username = enrollment.getUser().getUsername();
        this.email = enrollment.getUser().getEmail();
        this.enrollmentDate = enrollment.getEnrollmentDate();
    }
}
