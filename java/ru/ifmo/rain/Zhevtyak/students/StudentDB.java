package ru.ifmo.rain.Zhevtyak.students;

import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class StudentDB implements StudentQuery {

    private List<String> getInfo(Collection<Student> students, Function<Student, String> whatNeed) {
        return students.stream()
                .map(whatNeed)
                .collect(toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getInfo(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getInfo(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return getInfo(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getInfo(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return getFirstNames(students)
                .stream()
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream()
                .min(Student::compareTo)
                .map(Student::getFirstName)
                .orElse("");
    }

    private List<Student> sorting(Collection<Student> students, Comparator<Student> comp) {
        return students.stream()
                .sorted(comp)
                .collect(toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sorting(students, Comparator.naturalOrder());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sorting(students, comparatorByName());
    }

    private Comparator<Student> comparatorByName() {
        return Comparator.comparing(Student::getLastName)
                .thenComparing(Student::getFirstName)
                .thenComparingInt(Student::getId);
    }

    private List<Student> filtering(Collection<Student> students, Predicate<Student> whatNeed) {
        return students.stream()
                .filter(whatNeed)
                .collect(toList());
    }

    private List<Student> sortAndFilter(Collection<Student> students, Function<Student, String> func, String smth) {
        return sortStudentsByName(
                filtering(students, student -> func.apply(student).equals(smth))
        );
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return sortAndFilter(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return sortAndFilter(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return sortAndFilter(students, Student::getGroup, group);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return students.stream().filter(student -> student.getGroup().equals(group))
                .collect(
                        toMap(
                                Student::getLastName,
                                Student::getFirstName,
                                BinaryOperator.minBy(Comparator.naturalOrder())
                        )
                );
    }
}
