package vn.iotstar.graphql;

/** Lỗi nghiệp vụ: thông báo sẽ được trả nguyên văn về cho client trong mảng "errors" của GraphQL. */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }
}
