package service;

import domain.Grade;
import domain.Homework;
import domain.Pair;
import domain.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    public static Service service;

    @BeforeAll
    static void init()
    {
        Validator<Student> studentValidator = new StudentValidator();
        Validator<Homework> homeworkValidator = new HomeworkValidator();
        Validator<Grade> gradeValidator = new GradeValidator();

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(studentValidator, "students.xml");
        HomeworkXMLRepository fileRepository2 = new HomeworkXMLRepository(homeworkValidator, "homework.xml");
        GradeXMLRepository fileRepository3 = new GradeXMLRepository(gradeValidator, "grades.xml");

        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @Test
    void saveStudent() {
        int result = service.saveStudent("20", "almacska", 222);

        assertEquals(1, result);
    }

    @Test
    void throwExceptionAtSaveStudent() {
        assertThrows(ValidationException.class, () -> {
            service.saveStudent("19", "almacska", 3333);
        });
    }

    private static Stream<Pair<String, Integer>> provideStringsForDeleteStudent() {
        return Stream.of(
                new Pair("5", 1),
                new Pair("4", 1),
                new Pair("154", 0),
                new Pair("0", 0),
                new Pair("-12", 0));
    }

    @ParameterizedTest
    @MethodSource("provideStringsForDeleteStudent")
    void deleteStudent(Pair<String, Integer> pair) {
        String id = pair.getObject1();
        int expected = pair.getObject2();
        int result = service.deleteStudent(id);

        assertSame(expected, result);
    }


    @Test
    void updateStudent() {
        Student student = new Student("1", "alma", 222);
        Student result = service.updateStudent(student.getID(), student.getName(), student.getGroup());

        assertEquals(student, result);
//        assertEquals(student.getID(), result.getID());
//        assertEquals(student.getName(), result.getName());
//        assertEquals(student.getGroup(), result.getGroup());
    }

    @Test
    void saveGrade() {
        int result = service.saveGrade("2", "2", 3.25, 8, "really bad");

        assertEquals(0, result); // "Grade exists!"
    }
}