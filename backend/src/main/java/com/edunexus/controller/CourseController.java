package com.edunexus.controller;

import com.edunexus.model.Course;
import com.edunexus.model.User;
import com.edunexus.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable String id) {
        try {
            return ResponseEntity.ok(courseService.getCourse(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course, @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(courseService.createCourse(course, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable String id, @RequestBody Course course,
                                          @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(courseService.updateCourse(id, course, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id, @AuthenticationPrincipal User user) {
        try {
            courseService.deleteCourse(id, user);
            return ResponseEntity.ok(Map.of("message", "Course deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enroll(@PathVariable String id, @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(courseService.enroll(id, user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
