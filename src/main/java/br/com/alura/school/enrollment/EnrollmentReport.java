package br.com.alura.school.enrollment;

import br.com.alura.school.support.validation.Unique;
import br.com.alura.school.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrollmentReport {

    @Unique(entity = User.class, field = "email")
    @JsonProperty
    private String email;
    @JsonProperty
    private int quantidade_matriculas;

    public EnrollmentReport(int quantidade_matriculas, String email) {
        this.email = email;
        this.quantidade_matriculas = quantidade_matriculas;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getQuantidade_matriculas() {
        return quantidade_matriculas;
    }

    public void setQuantidade_matriculas(int quantidade_matriculas) {
        this.quantidade_matriculas = quantidade_matriculas;
    }
}
