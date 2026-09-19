package vn.iotstar.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * Model dùng để nhận dữ liệu từ form/AJAX (multipart) khi thêm/sửa Product.
 */
@Data
public class ProductModel {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private MultipartFile imageFile;
    private String description;
    private Double discount;
    private Long categoryId;
    private Short status;
}
