# MỤC 5 — GraphQL API + AJAX (Thymeleaf) trên Spring Boot 3.1.5

## Chức năng
- GraphQL endpoint: `POST /graphql` (GraphiQL UI: `/graphiql` để test query/mutation)
- Query:
  - `products` — tất cả product
  - `productById(id)` — chi tiết 1 product
  - `productsSortedByPriceAsc` — **tất cả product sắp xếp giá tăng dần** (dùng ở trang home)
  - `productsByCategory(categoryId)` — **tất cả product của 1 category** (dùng ở trang home)
  - `productsSearch(keyword, categoryId, page, size, sortByPrice)` — **tìm kiếm có phân trang** trên Product, kèm lọc theo category + sort giá
  - `categories`, `categoryById(id)`
  - `categoriesSearch(keyword, page, size)` — **tìm kiếm có phân trang** trên Category
- Mutation: `createProduct`, `updateProduct`, `deleteProductById`, `createCategory`, `updateCategory`, `deleteCategoryById`
- Trang Thymeleaf + AJAX (gọi thẳng `/graphql` bằng jQuery):
  - `/` — trang home: hiển thị sản phẩm giá tăng dần + lọc theo category
  - `/admin/category` — CRUD + tìm kiếm phân trang Category qua GraphQL
  - `/admin/product` — CRUD + tìm kiếm phân trang + lọc category + sort giá Product qua GraphQL

## Cách chạy
1. Bật MySQL, kiểm tra `application.properties` (mặc định `root` / `12345`, database `muc5_graphql` tự tạo nếu chưa có).
2. `mvn spring-boot:run`
3. Truy cập:
   - `http://localhost:7002/` — trang home
   - `http://localhost:7002/admin/category`
   - `http://localhost:7002/admin/product`
   - `http://localhost:7002/graphiql` — công cụ test GraphQL trực tiếp

## Ví dụ query test trong GraphiQL

```graphql
query {
  productsSortedByPriceAsc {
    productId
    productName
    unitPrice
    category { categoryName }
  }
}
```

```graphql
query {
  productsSearch(keyword: "", page: 0, size: 5, sortByPrice: "asc") {
    content { productId productName unitPrice }
    totalPages
    currentPage
  }
}
```

```graphql
mutation {
  createCategory(category: { categoryName: "Áo thun" }) {
    categoryId
    categoryName
  }
}
```

## Ghi chú
- GraphQL không upload file trực tiếp, nên ảnh/icon được upload qua `POST /api/upload` (trả về tên file), sau đó tên file được gửi trong mutation.
- Lỗi nghiệp vụ (trùng tên, xóa Category còn sản phẩm, dữ liệu sai...) được trả trong mảng `errors` của GraphQL và hiển thị bằng `alert` ở giao diện.
