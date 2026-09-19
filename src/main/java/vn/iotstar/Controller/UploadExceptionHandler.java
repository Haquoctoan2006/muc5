package vn.iotstar.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import vn.iotstar.Exception.StorageException;

/** Trả lỗi upload dạng JSON {message} để AJAX hiển thị được. */
@RestControllerAdvice
public class UploadExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", "File vượt quá dung lượng cho phép (10MB)"));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Map<String, String>> handleStorage(StorageException ex) {
        return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi lưu file: " + ex.getMessage()));
    }
}
