package vn.iotstar.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iotstar.entity.Product;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private String images;
    private String description;
    private double discount;
    private Date createDate;
    private short status;
    private Long categoryId;
    private String categoryName;

    public static ProductDTO fromEntity(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(p.getProductId());
        dto.setProductName(p.getProductName());
        dto.setQuantity(p.getQuantity());
        dto.setUnitPrice(p.getUnitPrice());
        dto.setImages(p.getImages());
        dto.setDescription(p.getDescription());
        dto.setDiscount(p.getDiscount());
        dto.setCreateDate(p.getCreateDate());
        dto.setStatus(p.getStatus());
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getCategoryId());
            dto.setCategoryName(p.getCategory().getCategoryName());
        }
        return dto;
    }
}
