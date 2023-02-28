package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;
import br.com.alura.school.enrollment.EnrollmentRepository;
import br.com.alura.school.enrollment.NewEnrollmentRequest;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CourseControllerTest {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // Esse método foi adicionado porque os métodos relativos à requisição POST da matrícula passam individualmente, mas não quando toda a bateria de testes é executada,
    // indicando que pode haver uma interferência dos outros testes por modificarem os mesmos dados (usuário, curso e matrícula)
    @AfterEach
    public void clearDatabase() {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void should_retrieve_course_by_code() throws Exception {
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        mockMvc.perform(get("/courses/java-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("java-1")))
                .andExpect(jsonPath("$.name", is("Java OO")))
                .andExpect(jsonPath("$.shortDescription", is("Java and O...")));
    }

    @Test
    void    should_retrieve_all_courses() throws Exception {
        courseRepository.save(new Course("spring-1", "Spring Basics", "Spring Core and Spring MVC."));
        courseRepository.save(new Course("spring-2", "Spring Boot", "Spring Boot"));

        mockMvc.perform(get("/courses")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].code", is("spring-1")))
                .andExpect(jsonPath("$[0].name", is("Spring Basics")))
                .andExpect(jsonPath("$[0].shortDescription", is("Spring Cor...")))
                .andExpect(jsonPath("$[1].code", is("spring-2")))
                .andExpect(jsonPath("$[1].name", is("Spring Boot")))
                .andExpect(jsonPath("$[1].shortDescription", is("Spring Boot")));
    }

    @Test
    void should_add_new_course() throws Exception {
        NewCourseRequest newCourseRequest = new NewCourseRequest("java-2", "Java Collections", "Java Collections: Lists, Sets, Maps and more.");

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newCourseRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/courses/java-2"));
    }

    @DisplayName("Esse teste deve matricular um estudante em um curso")
    @Test
    void should_enroll_student_into_course() throws Exception {
        userRepository.save(new User("alex","alex@email.com"));
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());
    }

    @DisplayName("Esse teste deve matricular um estudante em vários cursos")
    @Test
    void should_enroll_student_into_multiple_courses() throws Exception {
        userRepository.save(new User("alex","alex@email.com"));
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));
        courseRepository.save(new Course("spring-2", "Spring Boot", "Spring Boot"));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/courses/spring-2/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());
    }

    @DisplayName("Esse método não deve matricular um estudante por não achá-lo")
    @Test
    void should_not_enroll_student_because_student_was_not_found() throws Exception {
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Esse método não deve matricular um estudante porque o curso não foi achado")
    @Test
    void should_not_enroll_student_because_course_was_not_found() throws Exception {
        userRepository.save(new User("alex","alex@email.com"));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Esse teste não deve matricular um estudante porque ele já está matriculado")
    @Test
    void should_not_enroll_student_because_he_is_already_enrolled() throws Exception {
        User user = new User("alex","alex@email.com");
        userRepository.save(user);
        Course course = new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.");
        courseRepository.save(course);
        enrollmentRepository.save(new Enrollment(course,user));
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isBadRequest());

    }

    @DisplayName("Esse método deve retornar o relatório de matrículas")
    @Test
    void should_retrieve_enrollment_report() throws Exception {
        User user1 = new User("alex","alex@email.com");
        User user2 = new User("ana","ana@email.com");
        userRepository.save(user1);
        userRepository.save(user2);
        Course course1 = new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.");
        Course course2 = new Course("java-2", "Java Collections", "Java Collections: Lists, Sets, Maps and more.");
        courseRepository.save(course1);
        courseRepository.save(course2);
        enrollmentRepository.save(new Enrollment(course1,user1));
        enrollmentRepository.save(new Enrollment(course2,user1));
        enrollmentRepository.save(new Enrollment(course1,user2));

        mockMvc.perform(get("/courses/enroll/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].quantidade_matriculas", is(2)))
                .andExpect(jsonPath("$[0].email", is("alex@email.com")))
                .andExpect(jsonPath("$[1].quantidade_matriculas", is(1)))
                .andExpect(jsonPath("$[1].email", is("ana@email.com")));
    }

    @DisplayName("Esse método não deve retornar o relatório de matrículas porque não há nada para ser retornado")
    @Test
    void should_return_no_content_exception_because_there_are_no_enrolled_students() throws Exception {
        User user1 = new User("alex","alex@email.com");
        User user2 = new User("ana","ana@email.com");

        mockMvc.perform(get("/courses/enroll/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}