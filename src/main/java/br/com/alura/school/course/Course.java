package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;
import br.com.alura.school.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "Course")
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Size(max=10)
    @NotBlank
    @Column(nullable = false, unique = true)
    private String code;

    @Size(max=20)
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private List<Enrollment> enrolledUsers = new ArrayList<>();

    @Deprecated
    protected Course() { }

    Course(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String getCode() {
        return code;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public List<Enrollment> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void addUser(User user) {
        Enrollment coursesEnrollment = new Enrollment(this, user);
        enrolledUsers.add(coursesEnrollment);
        user.getEnrolledCourses().add(coursesEnrollment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        return id != null && id.equals(((Course) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
