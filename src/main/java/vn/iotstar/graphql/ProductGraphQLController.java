package vn.iotstar.graphql;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.model.ProductInput;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@Controller
public class ProductGraphQLController {

    @Autowired
    IProductService productService;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    IStorageService storageService;

    @QueryMapping
    public List<Product> products() {
        return productService.findAll();
    }

    @QueryMapping
    public Product productById(@Argument Long id) {
        return productService.findById(id).orElse(null);
    }

    // Yêu cầu 1: hiển thị tất cả product có price từ thấp đến cao (trang home)
    @QueryMapping
    public List<Product> productsSortedByPriceAsc() {
        return sortByPriceAsc(productService.findAll());
    }

    // Yêu cầu 2: lấy tất cả product của 1 category (trang home) - cũng xếp giá thấp -> cao
    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return sortByPriceAsc(productService.findByCategoryId(categoryId));
    }

    // Yêu cầu 3: tìm kiếm có phân trang (kèm lọc category + sắp xếp giá)
    @QueryMapping
    public Map<String, Object> productsSearch(@Argument String keyword,
                                              @Argument Long categoryId,
                                              @Argument Integer page,
                                              @Argument Integer size,
                                              @Argument String sortByPrice) {
        int p = page == null ? 0 : Math.max(page, 0);
        int s = size == null ? 5 : Math.min(Math.max(size, 1), 100);
        Sort sort = Sort.unsorted();
        if ("asc".equalsIgnoreCase(sortByPrice)) {
            sort = Sort.by(Sort.Direction.ASC, "unitPrice");
        } else if ("desc".equalsIgnoreCase(sortByPrice)) {
            sort = Sort.by(Sort.Direction.DESC, "unitPrice");
        }
        // productId để thứ tự ổn định giữa các trang khi nhiều sản phẩm cùng giá
        sort = sort.and(Sort.by(Sort.Direction.ASC, "productId"));

        Pageable pageable = PageRequest.of(p, s, sort);
        String kw = keyword == null ? "" : keyword.trim();
        Page<Product> result = productService.search(kw, categoryId, pageable);
        return Map.of(
                "content", result.getContent(),
                "totalPages", result.getTotalPages(),
                "totalElements", (int) result.getTotalElements(),
                "currentPage", p
        );
    }

    @MutationMapping
    public Product createProduct(@Argument ProductInput product) {
        String name = clean(product.getProductName());
        validate(name, product);
        if (productService.findByProductName(name).isPresent()) {
            throw new BusinessException("Sản phẩm này đã tồn tại trong hệ thống");
        }
        Category category = findCategory(product.getCategoryId());

        Product entity = new Product();
        entity.setProductName(name);
        entity.setQuantity(product.getQuantity() == null ? 0 : product.getQuantity());
        entity.setUnitPrice(product.getUnitPrice());
        entity.setImages(blankToNull(product.getImages()));
        entity.setDescription(clean(product.getDescription()));
        entity.setDiscount(product.getDiscount() == null ? 0 : product.getDiscount());
        entity.setStatus(product.getStatus() == null ? 1 : product.getStatus());
        entity.setCreateDate(new Date());
        entity.setCategory(category);
        return productService.save(entity);
    }

    @MutationMapping
    public Product updateProduct(@Argument ProductInput product) {
        if (product.getProductId() == null) {
            throw new BusinessException("Thiếu productId");
        }
        Optional<Product> optProduct = productService.findById(product.getProductId());
        if (optProduct.isEmpty()) {
            throw new BusinessException("Không tìm thấy Product");
        }
        String name = clean(product.getProductName());
        validate(name, product);
        Optional<Product> dup = productService.findByProductName(name);
        if (dup.isPresent() && !dup.get().getProductId().equals(product.getProductId())) {
            throw new BusinessException("Sản phẩm này đã tồn tại trong hệ thống");
        }
        Category category = findCategory(product.getCategoryId());

        Product entity = optProduct.get();
        String oldImage = entity.getImages();
        entity.setProductName(name);
        entity.setQuantity(product.getQuantity() == null ? entity.getQuantity() : product.getQuantity());
        entity.setUnitPrice(product.getUnitPrice());
        String newImage = blankToNull(product.getImages());
        if (newImage != null) {
            entity.setImages(newImage);
        }
        entity.setDescription(clean(product.getDescription()));
        entity.setDiscount(product.getDiscount() == null ? entity.getDiscount() : product.getDiscount());
        if (product.getStatus() != null) {
            entity.setStatus(product.getStatus());
        }
        entity.setCategory(category);
        Product saved = productService.save(entity);

        if (newImage != null && oldImage != null && !oldImage.equals(newImage)) {
            removeFile(oldImage);
        }
        return saved;
    }

    @MutationMapping
    public Boolean deleteProductById(@Argument Long id) {
        Optional<Product> opt = productService.findById(id);
        if (opt.isEmpty()) {
            throw new BusinessException("Không tìm thấy Product");
        }
        Product entity = opt.get();
        productService.delete(entity);
        removeFile(entity.getImages());
        return true;
    }

    // ---------- helper ----------
    private List<Product> sortByPriceAsc(List<Product> list) {
        return list.stream()
                .sorted(Comparator.comparingDouble(Product::getUnitPrice)
                        .thenComparing(Product::getProductId))
                .collect(Collectors.toList());
    }

    private Category findCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("Vui lòng chọn Category");
        }
        return categoryService.findById(categoryId)
                .orElseThrow(() -> new BusinessException("Category không tồn tại"));
    }

    private void validate(String name, ProductInput in) {
        if (name.isEmpty()) {
            throw new BusinessException("Tên sản phẩm không được để trống");
        }
        if (in.getUnitPrice() == null || in.getUnitPrice() < 0) {
            throw new BusinessException("Đơn giá phải >= 0");
        }
        if (in.getQuantity() != null && in.getQuantity() < 0) {
            throw new BusinessException("Số lượng phải >= 0");
        }
        if (in.getDiscount() != null && (in.getDiscount() < 0 || in.getDiscount() > 100)) {
            throw new BusinessException("Giảm giá phải nằm trong khoảng 0 - 100");
        }
    }

    private String clean(String s) {
        return s == null ? "" : s.trim();
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private void removeFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        try {
            storageService.delete(fileName);
        } catch (Exception ignored) {
            // xóa file thất bại không làm hỏng nghiệp vụ chính
        }
    }
}
