package com.example.studentmanagement.controller;

import com.example.studentmanagement.model.Product;
import com.example.studentmanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", service.findAll());
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("product") Product product,
            BindingResult result,
            @RequestParam("files") MultipartFile[] files,
            Model model,
            RedirectAttributes ra) throws IOException {

        String imageError = service.validateImages(files);
        if (imageError != null) {
            model.addAttribute("imageError", imageError);
        }
        if (result.hasErrors() || imageError != null) {
            return "products/form";
        }
        service.save(product, files);
        ra.addFlashAttribute("message", "Thêm sản phẩm thành công!");
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) throws IOException {
        service.delete(id);
        ra.addFlashAttribute("message", "Đã xóa sản phẩm!");
        return "redirect:/products";
    }
}