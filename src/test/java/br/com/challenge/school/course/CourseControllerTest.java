package br.com.challenge.school.course;

import br.com.challenge.school.enrollment.Enrollment;
import br.com.challenge.school.enrollment.EnrollmentRepository;
import br.com.challenge.school.enrollment.NewEnrollmentRequest;
import br.com.challenge.school.user.User;
import br.com.challenge.school.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @DisplayName("This test shouldn't find a course because it doesn't exists")
    @Test
    void not_found_when_course_does_not_exist() throws Exception {
        mockMvc.perform(get("/courses/java-3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_retrieve_all_courses() throws Exception {
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

    @DisplayName("This test shouldn't return any courses")
    @Test
    void no_content_when_there_are_no_courses_to_retrieve() throws Exception {
        mockMvc.perform(get("/courses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
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

    @DisplayName("This test should validate bad course requests")
    @ParameterizedTest
    @CsvSource({
            ", Java: Collections, ABCD",
            "'', Java: Collections, ABCD",
            "'    ', Java: Collections, ABCD",
            "java-2, , ABCD",
            "java-2, '', ABCD",
            "java-2, '    ', ABCD",
            "a-course-code-that-is-really-really-big , maria@email.com, ABCD",
            "java-2, a-course-name-that-is-really-really-big, ABCD"
    })
    void should_validate_bad_course_requests(String code, String name, String descriptions) throws Exception {
        NewCourseRequest newCourse = new NewCourseRequest(code,name, descriptions);

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newCourse)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("This test should not allow duplicated course codes")
    @Test
    void should_not_allow_duplication_of_course_code() throws Exception {
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));
        NewCourseRequest newCourse = new NewCourseRequest("java-1", "Java-01","Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.");

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newCourse)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("This test should not allow duplicated course names")
    @Test
    void should_not_allow_duplication_of_course_name() throws Exception {
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        NewCourseRequest newCourse = new NewCourseRequest("java-2", "Java OO","Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.");

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newCourse)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("This test should enroll a student into a course")
    @Test
    void should_enroll_student_into_course() throws Exception {
        userRepository.save(new User("ana","ana@email.com"));
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("ana", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());
    }

    @DisplayName("This test should enroll a student into multiple courses")
    @Test
    void should_enroll_student_into_multiple_courses() throws Exception {
        userRepository.save(new User("ana","ana@email.com"));
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));
        courseRepository.save(new Course("java-2", "Java Collections", "Java Collections: Lists, Sets, Maps and more."));
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("ana", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/courses/java-2/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());
    }

    @DisplayName("This method shouldn't enroll a student because it didn't find him")
    @Test
    void should_not_enroll_student_because_student_was_not_found() throws Exception {
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("This method shouldn't enroll a student because it didn't find a corresponding course")
    @Test
    void should_not_enroll_student_because_course_was_not_found() throws Exception {
        userRepository.save(new User("alex","alex@email.com"));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("alex", Date.from(Instant.now()));

        mockMvc.perform(post("/courses/java-1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("This test shouldn't enroll a student because he is already enrolled into this class")
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

    @DisplayName("This method should retrieve the enrollment report")
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

    @DisplayName("This method shouldn't retrieve the enrollment report because there's nothing to retrieve")
    @Test
    void should_return_no_content_exception_because_there_are_no_enrolled_students() throws Exception {

        mockMvc.perform(get("/courses/enroll/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}