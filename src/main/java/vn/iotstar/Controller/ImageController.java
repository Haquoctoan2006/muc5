package vn.iotstar.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import vn.iotstar.service.IStorageService;

@RestController
public class ImageController {

    @Autowired
    IStorageService storageService;

    // GET /admin/products/images/{filename}  và cũng dùng chung cho category icon
    @GetMapping("/admin/products/images/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().body(file);
    }
}
