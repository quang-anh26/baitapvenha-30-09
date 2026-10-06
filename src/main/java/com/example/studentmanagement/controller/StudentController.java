package com.example.studentmanagement.controller;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private static final int PAGE_SIZE = 5;
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/students";
    }

    // READ - danh sach + tim kiem + phan trang
    @GetMapping("/students")
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<Student> result = service.search(keyword, page, PAGE_SIZE);
        model.addAttribute("students", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalItems", result.getTotalElements());
        model.addAttribute("keyword", keyword);
        return "students/list";
    }

    // READ - chi tiet
    @GetMapping("/students/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("student", service.findById(id));
        return "students/detail";
    }

    // CREATE - hien thi form
    @GetMapping("/students/new")
    public String createForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("title", "Thêm sinh viên");
        return "students/form";
    }

    // UPDATE - hien thi form
    @GetMapping("/students/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", service.findById(id));
        model.addAttribute("title", "Cập nhật sinh viên");
        return "students/form";
    }

    // CREATE + UPDATE - luu
    @PostMapping("/students/save")
    public String save(@Valid @ModelAttribute("student") Student student,
                       BindingResult result,
                       Model model,
                       RedirectAttributes ra) {
        if (!result.hasFieldErrors("studentCode")
                && (student.getStudentCode() == null || !student.getStudentCode().matches("B[A-Za-z]{2}(2[0-9]|30)[0-9]{4}"))) {
            result.rejectValue("studentCode", "invalid", "Mã sinh viên không hợp lệ. Định dạng: B + 2 chữ cái + số từ 20 đến 30 + 4 chữ số, ví dụ BIT240024");
        }
        if (!result.hasFieldErrors("studentCode") && service.isCodeDuplicated(student)) {
            result.rejectValue("studentCode", "duplicate", "Mã sinh viên đã tồn tại");
        }
        if (result.hasErrors()) {
            model.addAttribute("title", student.getId() == null ? "Thêm sinh viên" : "Cập nhật sinh viên");
            return "students/form";
        }
        boolean isNew = student.getId() == null;
        service.save(student);
        ra.addFlashAttribute("message", isNew ? "Thêm sinh viên thành công!" : "Cập nhật sinh viên thành công!");
        return "redirect:/students";
    }

    // DELETE
    @PostMapping("/students/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("message", "Đã xóa sinh viên!");
        return "redirect:/students";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound(IllegalArgumentException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/students";
    }
}
