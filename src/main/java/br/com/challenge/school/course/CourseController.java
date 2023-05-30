package br.com.challenge.school.course;

import br.com.challenge.school.enrollment.Enrollment;
import br.com.challenge.school.enrollment.EnrollmentReport;
import br.com.challenge.school.enrollment.EnrollmentRepository;
import br.com.challenge.school.enrollment.NewEnrollmentRequest;
import br.com.challenge.school.user.User;
import br.com.challenge.school.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;


@RestController
class CourseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);
    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final EnrollmentRepository enrollmentRepository;



    CourseController(CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }


    /*
       This method was refactored due to problems with a test method, which was the first problem of this technical challenge.
       The first line retrieves all courses in the database.
       Afterwards it transforms each course object into a courseResponse object, mapping them and creating a list.
       Finally, it returns this list and a response status ok 200
     */
    @GetMapping(value = "/courses")
    ResponseEntity<List<CourseResponse>> allCourses() {
        List<Course> courses = courseRepository.findAll();
        LOGGER.info("There has been made a request to search all existing courses. Number of courses found = {}", courses.size());
        List<CourseResponse> courseResponses = courses.stream().map(CourseResponse::new).collect(Collectors.toList());
        return courseResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(courseResponses);
    }

    @GetMapping("/courses/{code}")
    ResponseEntity<CourseResponse> courseByCode(@PathVariable("code") String code) {
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, format("Course with code %s not found", code)));
        return ResponseEntity.ok(new CourseResponse(course));
    }

    /*
       This method was created to produce a report of all students who have at least one enrollment and ordering the list by descending values.
       First it retrieves all users that have enrolled in at least one course.
       Then it stream of objects, mapping each user to an object that contains the number of enrollments an user has and it's email.
       This collection of objects is then sorted by descending int values, generating an enrollment report in the end.
       Finally, the ternary operator returns status 204 no content if there is no report to retrieve, otherwise it returns status 200 ok.
     */
    @GetMapping(value = "/courses/enroll/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EnrollmentReport>> enrollmentReport() {
        List<EnrollmentReport> report = userRepository.findByEnrolledCoursesIsNotEmpty().stream()
                .map(user -> new EnrollmentReport(user.getEnrolledCourses().size(),user.getEmail()))
                .sorted(Comparator.comparingInt(EnrollmentReport::getQuantidade_matriculas).reversed()).collect(Collectors.toList());
        LOGGER.info("There has been made a request to search all students who have at least one enroll and sort them by a descending order.");

        return report.isEmpty() ? ResponseEntity.status(NO_CONTENT).build() : ResponseEntity.ok(report);
    }


    @PostMapping("/courses")
    ResponseEntity<Void> newCourse(@RequestBody @Valid NewCourseRequest newCourseRequest) {
        courseRepository.save(newCourseRequest.toEntity());
        URI location = URI.create(format("/courses/%s", newCourseRequest.getCode()));
        return ResponseEntity.created(location).build();
    }

    /*
       This method was created to enroll a student into a course.
       First it retrieves an user by it's username, or else it throws status 404 not found.
       Second, it retrieves a course by it's code, otherwise it throws status 404 not found.
       Then it checks if the user has already enrolled into the same course. If that is the case, it blocks another enrollment and throws status 400 bad request.
       If the user wasn't enrolled, adds the user to the course and saves the enroll into the database returning a status 201 created.
       P.S: the entityManager.clear() function was necessary because there were problems with the in-memory cache.
     */
    @PostMapping(value = "/courses/{code}/enroll", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> newEnroll(@PathVariable("code") String code, @RequestBody @Valid NewEnrollmentRequest newEnrollmentRequest) throws Exception {
        User user = userRepository.findByUsername(newEnrollmentRequest.getUsername()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        LOGGER.info("There has been a request to find aa user by it's username, otherwise it should return status 404");
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));
        LOGGER.info("There has been a request to find a course by it's code, otherwise it should return status 404");
        if (course.getEnrolledUsers().contains(new Enrollment(course, user))) {
            throw new ResponseStatusException(BAD_REQUEST, "User is already enrolled in the course");
        }
        enrollmentRepository.save(new Enrollment(course, user));
        LOGGER.info("Enrollment accomplished.");
        return ResponseEntity.status(CREATED).build();
    }
}
