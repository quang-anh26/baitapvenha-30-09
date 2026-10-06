package com.example.studentmanagement.service;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<Student> search(String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll(pageable);
        }
        String kw = keyword.trim();
        return repository.findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCase(kw, kw, pageable);
    }

    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên có id = " + id));
    }

    public boolean isCodeDuplicated(Student s) {
        return s.getId() == null
                ? repository.existsByStudentCode(s.getStudentCode())
                : repository.existsByStudentCodeAndIdNot(s.getStudentCode(), s.getId());
    }

    public Student save(Student student) {
        if (student.getStudentCode() == null || !student.getStudentCode().matches("B[A-Za-z]{2}(2[0-9]|30)[0-9]{4}")) {
            throw new IllegalArgumentException("Mã sinh viên không hợp lệ. Định dạng: B + 2 chữ cái + số từ 20 đến 30 + 4 chữ số, ví dụ BIT240024");
        }
        return repository.save(student);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
