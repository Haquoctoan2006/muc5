package vn.iotstar.entity;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

/**
 * Không dùng @Data: quan hệ 2 chiều Category <-> Product làm hashCode()/toString()
 * gọi đệ quy lẫn nhau -> StackOverflowError.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Categories")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(length = 255)
    private String categoryName;

    private String icon;

    // Không cascade: xóa Category không được tự xóa hết Product
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private Set<Product> products;
}
