package vn.iotstar.Controller;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.service.IStorageService;

/**
 * GraphQL không nhận file trực tiếp, nên upload ảnh qua endpoint này,
 * sau đó gửi tên file trả về vào mutation (createProduct/updateProduct/...).
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Set<String> ALLOWED = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Autowired
    IStorageService storageService;

    @PostMapping
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return bad("File rỗng");
        }
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if (ext == null || !ALLOWED.contains(ext.toLowerCase())) {
            return bad("Chỉ cho phép ảnh jpg, jpeg, png, gif, webp");
        }
        String fileName = storageService.getSorageFilename(file, UUID.randomUUID().toString());
        storageService.store(file, fileName);
        return ResponseEntity.ok(Map.of("fileName", fileName));
    }

    private ResponseEntity<Map<String, String>> bad(String message) {
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
