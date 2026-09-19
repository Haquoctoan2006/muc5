package vn.iotstar.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm kiếm linh hoạt: theo tên (có thể null/rỗng) và theo categoryId (có thể null)
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.categoryId = :categoryId)")
    Page<Product> search(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
    // Tìm kiếm theo tên
    List<Product> findByProductNameContaining(String name);

    // Tìm kiếm và phân trang
    Page<Product> findByProductNameContaining(String name, Pageable pageable);

    // Tìm kiếm theo category + phân trang
    Page<Product> findByCategory_CategoryNameContainingAndProductNameContaining(
            String categoryName, String productName, Pageable pageable);

    Optional<Product> findByProductName(String name);

    Optional<Product> findByCreateDate(Date createAt);

    List<Product> findByCategory_CategoryId(Long categoryId);
}
