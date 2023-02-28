package br.com.alura.school.course;

import br.com.alura.school.enrollment.Enrollment;
import br.com.alura.school.enrollment.EnrollmentReport;
import br.com.alura.school.enrollment.EnrollmentRepository;
import br.com.alura.school.enrollment.NewEnrollmentRequest;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;


@RestController
class CourseController {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    private final EnrollmentRepository enrollmentRepository;

    CourseController(CourseRepository courseRepository, UserRepository userRepository, EntityManager entityManager, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping(value = "/courses")
    ResponseEntity<List<CourseResponse>> allCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseResponse> courseResponses = courses.stream().map(CourseResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(courseResponses, HttpStatus.OK);
    }

    @GetMapping("/courses/{code}")
    ResponseEntity<CourseResponse> courseByCode(@PathVariable("code") String code) {
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("Course with code %s not found", code)));
        return ResponseEntity.ok(new CourseResponse(course));
    }

    // Esse método foi criado para gerar o relatório de matrículas
    @GetMapping(value = "/courses/enroll/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EnrollmentReport>> enrollmentReport() {
        List<EnrollmentReport> report = userRepository.findByEnrolledCoursesIsNotEmpty().stream() // cria uma lista de objetos do relatório relacionados aos usuários que tem ao menos uma matrícula
                .map(user -> new EnrollmentReport(user.getEnrolledCourses().size(),user.getEmail())) // cria uma stream (sequência de objetos) e mapeia cada usuário a um objeto que contém a quantidade de matrículas do usuário e seu email
                .sorted(Comparator.comparingInt(EnrollmentReport::getQuantidade_matriculas).reversed()).collect(Collectors.toList()); // a lista é ordenada comparando os valores int da quantidade de matrículas e gera a lista final do relatório

        return report.isEmpty() ? ResponseEntity.status(NO_CONTENT).build() : ResponseEntity.ok(report); // uso de operador ternário para retornar status 204 caso não haja relatório a ser produzido, caso contrário retornar o relatório
    }


    @PostMapping("/courses")
    ResponseEntity<Void> newCourse(@RequestBody @Valid NewCourseRequest newCourseRequest) {
        courseRepository.save(newCourseRequest.toEntity());
        URI location = URI.create(format("/courses/%s", newCourseRequest.getCode()));
        return ResponseEntity.created(location).build();
    }

    // Esse método foi criado para criar as matrículas
    @PostMapping(value = "/courses/{code}/enroll", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> newEnroll(@PathVariable("code") String code, @RequestBody @Valid NewEnrollmentRequest newEnrollmentRequest) throws Exception {
        User user = userRepository.findByUsername(newEnrollmentRequest.getUsername()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found")); // procura um usuário pelo seu nome, caso contrário retorna exceção
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found")); // procura um curso pelo código, caso contrário retorna exceção
        if (course.getEnrolledUsers().contains(new Enrollment(course, user))) { // procura na lista de estudantes matriculados do curso se o estudante já está matriculado
            throw new ResponseStatusException(BAD_REQUEST, "User is already enrolled in the course"); // impede uma nova matrícula e retorna exceção, caso esteja matriculado
        }
            course.addUser(user); // realiza a matrícula, adicionando o usuário ao curso
            entityManager.clear(); // essa função foi adicionada pelo aplicativo apresentar problemas de cache no banco de dados em memória e dar erro 500 ao realizar a matrícula
            courseRepository.save(course); // salva a matrícula
        return ResponseEntity.status(CREATED).build(); // retorna o status de matrícula criada
    }
}
