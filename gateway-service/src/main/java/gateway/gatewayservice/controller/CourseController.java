package gateway.gatewayservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CourseController {

    private final List<String> courses = new CopyOnWriteArrayList<>(List.of(
            "Java Spring Boot Cấu Trúc Siêu Sạch",
            "Thiết Kế Hệ Thống Microservices Thực Chiến"
    ));

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<List<String>> getAllCourses() {
        return ResponseEntity.ok(courses);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<String> createCourse(@RequestBody String courseName) {
        if (courseName == null || courseName.isBlank()) {
            return ResponseEntity.badRequest().body("Tên khóa học không được để trống");
        }
        courses.add(courseName);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tạo khóa học thành công: " + courseName);
    }
}
