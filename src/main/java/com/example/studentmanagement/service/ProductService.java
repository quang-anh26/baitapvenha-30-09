package com.example.studentmanagement.service;

import com.example.studentmanagement.model.Product;
import com.example.studentmanagement.model.ProductImage;
import com.example.studentmanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_TYPE = Set.of("image/jpeg", "image/png");

    private final ProductRepository repository;
    private final Path uploadPath;

    public ProductService(ProductRepository repository,
                          @Value("${app.upload-dir}") String uploadDir) {
        this.repository = repository;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath();
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return repository.findAll();
    }

    /** Trả về thông báo lỗi nếu có file không hợp lệ, ngược lại trả về null. */
    public String validateImages(MultipartFile[] files) {
        if (files == null) return null;
        for (MultipartFile f : files) {
            if (f.isEmpty()) continue;
            String ext = getExtension(f.getOriginalFilename());
            String type = f.getContentType();
            if (!ALLOWED_EXT.contains(ext) || type == null || !ALLOWED_TYPE.contains(type)) {
                return "File \"" + f.getOriginalFilename() + "\" không hợp lệ. Chỉ cho phép upload ảnh .jpg hoặc .png";
            }
        }
        return null;
    }

    public Product save(Product product, MultipartFile[] files) throws IOException {
        Files.createDirectories(uploadPath);
        if (files != null) {
            for (MultipartFile f : files) {
                if (f.isEmpty()) continue;
                String newName = UUID.randomUUID() + "." + getExtension(f.getOriginalFilename());
                f.transferTo(uploadPath.resolve(newName));
                product.addImage(new ProductImage(newName));
            }
        }
        return repository.save(product);
    }

    public void delete(Long id) throws IOException {
        Product p = repository.findById(id).orElse(null);
        if (p == null) return;
        for (ProductImage img : p.getImages()) {
            Files.deleteIfExists(uploadPath.resolve(img.getFileName()));
        }
        repository.delete(p);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}