package vn.iotstar.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import lombok.*;

/** Không dùng @Data để tránh vòng lặp hashCode()/toString() với Category. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Products")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(length = 500, nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unitPrice;

    @Column(length = 200)
    private String images;

    @Column(length = 500, nullable = false)
    private String description;

    @Column(nullable = false)
    private double discount;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @Column(nullable = false)
    private short status;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
}
