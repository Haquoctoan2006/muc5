package vn.iotstar.model;

import lombok.Data;

@Data
public class ProductInput {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private String images;
    private String description;
    private Double discount;
    private Short status;
    private Long categoryId;
}
