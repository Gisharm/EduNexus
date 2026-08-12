package com.edunexus.service;

import com.edunexus.model.Course;
import com.edunexus.model.User;
import com.edunexus.repository.CourseRepository;
import com.edunexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourse(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    public Course createCourse(Course course, User creator) {
        if (User.Role.student.name().equals(creator.getRole())) {
            throw new IllegalArgumentException("Students cannot create courses");
        }
        course.setId(null);
        course.setAdminId(creator.getId());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        if (course.getEnrolledStudents() == null) course.setEnrolledStudents(new java.util.HashSet<>());
        if (course.getTeachingAssistants() == null) course.setTeachingAssistants(new java.util.HashSet<>());
        return courseRepository.save(course);
    }

    public Course updateCourse(String id, Course updated, User user) {
        Course existing = getCourse(id);
        if (!isOwnerOrAdmin(existing, user)) {
            throw new IllegalArgumentException("Only the course owner or an admin can update this course");
        }
        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getModules() != null) existing.setModules(updated.getModules());
        if (updated.getAnnouncements() != null) existing.setAnnouncements(updated.getAnnouncements());
        if (updated.getDiscussions() != null) existing.setDiscussions(updated.getDiscussions());
        if (updated.getTeachingAssistants() != null) existing.setTeachingAssistants(updated.getTeachingAssistants());
        existing.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(existing);
    }

    public void deleteCourse(String id, User user) {
        Course existing = getCourse(id);
        if (!isOwnerOrAdmin(existing, user)) {
            throw new IllegalArgumentException("Only the course owner or an admin can delete this course");
        }
        courseRepository.deleteById(id);
    }

    public Course enroll(String courseId, User student) {
        Course course = getCourse(courseId);
        course.getEnrolledStudents().add(student.getId());
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);

        if (student.getEnrolledCourses() == null) {
            student.setEnrolledCourses(new java.util.ArrayList<>());
        }
        if (!student.getEnrolledCourses().contains(courseId)) {
            student.getEnrolledCourses().add(courseId);
        }
        userRepository.save(student);
        return course;
    }

    private boolean isOwnerOrAdmin(Course course, User user) {
        return User.Role.admin.name().equals(user.getRole())
                || (user.getId() != null && user.getId().equals(course.getAdminId()));
    }
}
