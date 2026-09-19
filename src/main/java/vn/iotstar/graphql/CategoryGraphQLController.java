package vn.iotstar.graphql;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import vn.iotstar.model.CategoryInput;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@Controller
public class CategoryGraphQLController {

    @Autowired
    ICategoryService categoryService;

    @Autowired
    IProductService productService;

    @Autowired
    IStorageService storageService;

    @QueryMapping
    public List<Category> categories() {
        return categoryService.findAll();
    }

    @QueryMapping
    public Category categoryById(@Argument Long id) {
        return categoryService.findById(id).orElse(null);
    }

    // categoriesSearch(keyword, page, size) - tìm kiếm có phân trang
    @QueryMapping
    public Map<String, Object> categoriesSearch(@Argument String keyword,
                                                @Argument Integer page,
                                                @Argument Integer size) {
        int p = page == null ? 0 : Math.max(page, 0);
        int s = size == null ? 5 : Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(p, s, Sort.by("categoryId"));
        String kw = keyword == null ? "" : keyword.trim();
        Page<Category> result = categoryService.findByCategoryNameContaining(kw, pageable);
        return Map.of(
                "content", result.getContent(),
                "totalPages", result.getTotalPages(),
                "totalElements", (int) result.getTotalElements(),
                "currentPage", p
        );
    }

    @MutationMapping
    public Category createCategory(@Argument CategoryInput category) {
        String name = clean(category.getCategoryName());
        if (name.isEmpty()) {
            throw new BusinessException("Tên Category không được để trống");
        }
        if (categoryService.findByCategoryName(name).isPresent()) {
            throw new BusinessException("Category đã tồn tại trong hệ thống");
        }
        Category entity = new Category();
        entity.setCategoryName(name);
        entity.setIcon(blankToNull(category.getIcon()));
        return categoryService.save(entity);
    }

    @MutationMapping
    public Category updateCategory(@Argument CategoryInput category) {
        if (category.getCategoryId() == null) {
            throw new BusinessException("Thiếu categoryId");
        }
        Optional<Category> opt = categoryService.findById(category.getCategoryId());
        if (opt.isEmpty()) {
            throw new BusinessException("Không tìm thấy Category");
        }
        String name = clean(category.getCategoryName());
        if (name.isEmpty()) {
            throw new BusinessException("Tên Category không được để trống");
        }
        Optional<Category> dup = categoryService.findByCategoryName(name);
        if (dup.isPresent() && !dup.get().getCategoryId().equals(category.getCategoryId())) {
            throw new BusinessException("Category đã tồn tại trong hệ thống");
        }

        Category entity = opt.get();
        String oldIcon = entity.getIcon();
        entity.setCategoryName(name);
        String newIcon = blankToNull(category.getIcon());
        if (newIcon != null) {
            entity.setIcon(newIcon);
        }
        Category saved = categoryService.save(entity);
        if (newIcon != null && oldIcon != null && !oldIcon.equals(newIcon)) {
            removeFile(oldIcon);
        }
        return saved;
    }

    @MutationMapping
    public Boolean deleteCategoryById(@Argument Long id) {
        Optional<Category> opt = categoryService.findById(id);
        if (opt.isEmpty()) {
            throw new BusinessException("Không tìm thấy Category");
        }
        int count = productService.findByCategoryId(id).size();
        if (count > 0) {
            throw new BusinessException("Không thể xóa: Category đang có " + count
                    + " sản phẩm. Hãy xóa hoặc chuyển sản phẩm trước.");
        }
        Category entity = opt.get();
        categoryService.delete(entity);
        removeFile(entity.getIcon());
        return true;
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
