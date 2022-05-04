package service;

import domain.*;
import repository.*;
import validation.ValidationException;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class Service {
    private StudentXMLRepository studentXmlRepo;
    private HomeworkXMLRepository homeworkXmlRepo;
    private GradeXMLRepository gradeXmlRepo;

    public Service(StudentXMLRepository studentXmlRepo, HomeworkXMLRepository homeworkXmlRepo, GradeXMLRepository gradeXmlRepo) {
        this.studentXmlRepo = studentXmlRepo;
        this.homeworkXmlRepo = homeworkXmlRepo;
        this.gradeXmlRepo = gradeXmlRepo;
    }

    public Iterable<Student> findAllStudents() { return studentXmlRepo.findAll(); }

    public Iterable<Homework> findAllHomework() { return homeworkXmlRepo.findAll(); }

    public Iterable<Grade> findAllGrades() { return gradeXmlRepo.findAll(); }

    public int saveStudent(String id, String name, int group) throws ValidationException  {
        Student student = new Student(id, name, group);
        Student result = studentXmlRepo.save(student);
        if (result != null) {
            return 0;
        }

        result = studentXmlRepo.findOne(id);

        if (result != null) {
            return 1;
        }

        return 0;
    }

    public int saveHomework(String id, String description, int deadline, int startline) throws ValidationException {
        Homework homework = new Homework(id, description, deadline, startline);
        Homework result = homeworkXmlRepo.save(homework);

        if (result == null) {
            return 1;
        }
        return 0;
    }

    public int saveGrade(String idStudent, String idHomework, double valGrade, int delivered, String feedback) throws ValidationException {
        Homework homework = homeworkXmlRepo.findOne(idHomework);
        if (studentXmlRepo.findOne(idStudent) == null || homework == null) {
            return -1;
        }
        else {
            int deadline = homework.getDeadline();

            if (delivered - deadline > 2) {
                valGrade =  1;
            } else {
                if (delivered - deadline > 0) {
                    valGrade =  valGrade - 2.5 * (delivered - deadline);
                }
            }
            Grade grade = new Grade(new Pair(idStudent, idHomework), valGrade, delivered, feedback);
            Grade result = gradeXmlRepo.save(grade);

            if (result == null) {
                return 1;
            }
            return 0;
        }
    }

    public int deleteStudent(String id) throws IllegalArgumentException {
        Student result = studentXmlRepo.delete(id);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int deleteHomework(String id) throws IllegalArgumentException {
        Homework result = homeworkXmlRepo.delete(id);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public Student updateStudent(String id, String nameNew, int groupNew) throws ValidationException {
        Student studentNew = new Student(id, nameNew, groupNew);
        Student result = studentXmlRepo.update(studentNew);
        return result;
    }

    public int updateHomework(String id, String descriptionNew, int deadlineNew, int startlineNew) throws ValidationException {
        Homework homeworkNew = new Homework(id, descriptionNew, deadlineNew, startlineNew);
        Homework result = homeworkXmlRepo.update(homeworkNew);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int extendDeadline(String id, int noWeeks) {
        Homework homework = homeworkXmlRepo.findOne(id);

        if (homework != null) {
            LocalDate date = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int currentWeek = date.get(weekFields.weekOfWeekBasedYear());

            if (currentWeek >= 39) {
                currentWeek = currentWeek - 39;
            } else {
                currentWeek = currentWeek + 12;
            }

            if (currentWeek <= homework.getDeadline()) {
                int deadlineNou = homework.getDeadline() + noWeeks;
                return updateHomework(homework.getID(), homework.getDescription(), deadlineNou, homework.getStartline());
            }
        }
        return 0;
    }

    public void createStudentFile(String idStudent, String idHomework) {
        Grade grade = gradeXmlRepo.findOne(new Pair(idStudent, idHomework));

        gradeXmlRepo.createFile(grade);
    }
}
