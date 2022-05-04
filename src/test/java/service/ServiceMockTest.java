package service;

import domain.Grade;
import domain.Homework;
import domain.Pair;
import domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceMockTest {
    public Service service;
    GradeXMLRepository gradeXMLRepository;
    StudentXMLRepository studentXMLRepository;
    HomeworkXMLRepository homeworkXMLRepository;

    @BeforeEach
    void setup() {
        gradeXMLRepository = mock(GradeXMLRepository.class);
        studentXMLRepository = mock(StudentXMLRepository.class);
        homeworkXMLRepository = mock(HomeworkXMLRepository.class);
        service = new Service(studentXMLRepository, homeworkXMLRepository, gradeXMLRepository);
    }

    @Test
    void saveStudent() {
        assertNotNull(studentXMLRepository);
        Student student = new Student("23", "alma", 222);
        when(studentXMLRepository.save(student)).thenReturn(null);
        when(studentXMLRepository.findOne(student.getID())).thenReturn(student);

        int result = service.saveStudent(student.getID(), student.getName(), student.getGroup());

        Mockito.verify(studentXMLRepository).save(student);
        Mockito.verify(studentXMLRepository).findOne(student.getID());

        assertEquals(1, result);
    }

    @Test
    void saveGrade() {
        assertAll("repositories",
                () -> assertNotNull(studentXMLRepository),
                () -> assertNotNull(gradeXMLRepository),
                () -> assertNotNull(homeworkXMLRepository));

        double gradeNote = 6.7;
        int deliveryWeek = 5;
        String feedback = "Ok";
        Student student = new Student("23", "alma", 222);
        Homework homework = new Homework("12", "some description", 6, 4);
        Grade grade = new Grade(new Pair(student.getID(), homework.getID()), gradeNote, deliveryWeek, feedback);

        when(studentXMLRepository.findOne(anyString())).thenReturn(student);
        when(homeworkXMLRepository.findOne(anyString())).thenReturn(homework);
        when(gradeXMLRepository.save(grade)).thenReturn(null);

        int result = service.saveGrade(student.getID(), homework.getID(), gradeNote, deliveryWeek, feedback);

        Mockito.verify(studentXMLRepository).findOne(anyString());
        Mockito.verify(homeworkXMLRepository).findOne(anyString());

        assertEquals(1, result);
    }

    @Test
    void deleteStudent() {
        assertNotNull(studentXMLRepository);

        String id = "1";
        int expected = 1;
        Student student = new Student(id, "alma", 222);
        when(studentXMLRepository.delete(anyString())).thenReturn(student);

        int result = service.deleteStudent(id);

        Mockito.verify(studentXMLRepository).delete(anyString());

        assertSame(expected, result);
    }
}
