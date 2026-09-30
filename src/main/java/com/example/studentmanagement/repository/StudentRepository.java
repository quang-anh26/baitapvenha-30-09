package com.example.studentmanagement.repository;

import com.example.studentmanagement.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCase(
            String fullName, String studentCode, Pageable pageable);

    boolean existsByStudentCode(String studentCode);

    boolean existsByStudentCodeAndIdNot(String studentCode, Long id);
}
