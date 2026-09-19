package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Category;

public interface ICategoryService {
    void delete(Category entity);
    void deleteById(Long id);
    long count();
    Optional<Category> findById(Long id);
    List<Category> findAll();
    Page<Category> findAll(Pageable pageable);
    Optional<Category> findByCategoryName(String name);
    <S extends Category> S save(S entity);
    Page<Category> findByCategoryNameContaining(String name, Pageable pageable);
    List<Category> findByCategoryNameContaining(String name);
}
