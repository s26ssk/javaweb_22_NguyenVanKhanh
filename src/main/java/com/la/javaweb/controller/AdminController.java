package com.la.javaweb.controller;

import com.la.javaweb.dto.response.CommentResponse;
import com.la.javaweb.model.*;
import com.la.javaweb.service.*;
import com.la.javaweb.util.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"*"})
public class AdminController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IProductService productService;
    @Autowired
    private ICommentService commentService;
    @Autowired
    private IOrderService orderService;


    // User
    @GetMapping("/roles")
    public ResponseEntity<List<Roles>> getAllRoles() {
        List<Roles> roles = roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @PageableDefault(page = 0, size = 3)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "userId") String sortBy) {

        Pageable pageable = PageRequest.of(page, 3, Sort.by(sortBy));
        Page<Users> usersPage = userService.getAllUsers(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("users", usersPage.getContent());
        data.put("sizePage", usersPage.getSize());
        data.put("totalElement", usersPage.getTotalElements());
        data.put("totalPages", usersPage.getTotalPages());
        data.put("currentPage", usersPage.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Optional<Users> userOptional = userService.getUserById(userId);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<Map<String, Object>> getUsersByUsername(
            @RequestParam(name = "keyword", required = false) String username,
            @PageableDefault(page = 0, size = 3) Pageable pageable) {
        Page<Users> usersPage;

        if (username != null && !username.isEmpty()) {
            usersPage = userService.searchUsersByUsername(username, pageable);
        } else {
            usersPage = userService.getAllUsers(pageable);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("users", usersPage.getContent());
        data.put("sizePage", usersPage.getSize());
        data.put("totalElement", usersPage.getTotalElements());
        data.put("totalPages", usersPage.getTotalPages());
        data.put("currentPage", usersPage.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping("/change-user-status/{userId}")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long userId) throws AppException {
        boolean isBlocked = userService.toggleUserStatus(userId);
        if (isBlocked) {
            return new ResponseEntity<>("User blocked successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User unblocked successfully", HttpStatus.OK);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword,
                                                 @RequestParam String newPassword) throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (!userService.checkPassword(username, oldPassword)) {
            return new ResponseEntity<>("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }

        userService.changePassword(username, newPassword);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }


    // Category
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @PageableDefault(page = 0, size = 3)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "categoryId") String sortBy) {

        Pageable pageable = PageRequest.of(page, 3, Sort.by(sortBy));
        Page<Category> categoryPage = categoryService.getAllCategories(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categoryPage.getContent());
        data.put("sizePage", categoryPage.getSize());
        data.put("totalElement", categoryPage.getTotalElements());
        data.put("totalPages", categoryPage.getTotalPages());
        data.put("currentPage", categoryPage.getNumber());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId) {
        Optional<Category> categoryOptional = categoryService.getCategoryById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            return new ResponseEntity<>(category, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/categories")
    public ResponseEntity<String> addCategory(@RequestBody Category newCategory) throws AppException{
        categoryService.addCategory(newCategory);
        return new ResponseEntity<>("Category added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId, @RequestBody Category updatedCategory) {
        boolean isUpdated = categoryService.updateCategory(categoryId, updatedCategory);

        if (isUpdated) {
            return new ResponseEntity<>("Category updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Category not found or update failed", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        boolean isDeleted = categoryService.deleteCategory(categoryId);

        if (isDeleted) {
            return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Category not found or delete failed", HttpStatus.NOT_FOUND);
        }
    }

    // Product
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @PageableDefault(page = 0, size = 3)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "productId") String sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "asc") String order) {

        Pageable pageable = PageRequest.of(page, 3, Sort.by(order.equals("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy)));
        Page<Product> productPage = productService.getAllProducts(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("product", productPage.getContent());
        data.put("sizePage", productPage.getSize());
        data.put("totalElement", productPage.getTotalElements());
        data.put("totalPages", productPage.getTotalPages());
        data.put("currentPage", productPage.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }


    @GetMapping("/products/search")
    public ResponseEntity<Map<String, Object>> searchProductsByName(
            @RequestParam(name = "keyword") String productName,
            @PageableDefault(page = 0, size = 3) Pageable pageable) {

        Page<Product> productsPage;

        if (productName != null && !productName.isEmpty()) {
            productsPage = productService.searchProductsByName(productName, pageable);
        } else {
            productsPage = productService.getAllProducts(pageable);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("products", productsPage.getContent());
        data.put("sizePage", productsPage.getSize());
        data.put("totalElement", productsPage.getTotalElements());
        data.put("totalPages", productsPage.getTotalPages());
        data.put("currentPage", productsPage.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        Optional<Product> productOptional = productService.getProductById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<String> addProduct(
            @RequestParam(name = "imagesUpload") List<MultipartFile> imagesUpload,
            @ModelAttribute Product newProduct
    ) {
        try {
            productService.addProduct(newProduct, imagesUpload);
            return new ResponseEntity<>("Product added successfully", HttpStatus.CREATED);
        } catch (AppException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable Long productId,
            @ModelAttribute Product updatedProduct,
            @RequestParam(value = "imagesUpload", required = false) List<MultipartFile> newImages) {
        try {
            productService.updateProduct(productId, updatedProduct, newImages);
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        boolean isDeleted = productService.deleteProduct(productId);
        if (isDeleted) {
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found or delete failed", HttpStatus.NOT_FOUND);
        }
    }


    // Comment
    @GetMapping("/products/comments")
    public ResponseEntity<Map<String, Object>> getAllComments(
            @PageableDefault(page = 0, size = 3)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "commentId") String sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "asc") String order) {

        Pageable pageable = PageRequest.of(page, 3, Sort.by(order.equals("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy)));

        Page<Comment> commentPage = commentService.getAllComments(pageable);

        List<Comment> comments = commentPage.getContent();
        List<CommentResponse> commentResponseList = comments.stream()
                .map(comment -> CommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .fullName(comment.getUser().getFullName())
                        .content(comment.getContent())
                        .commentDate(comment.getCreatedAt())
                        .build())
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("comments", commentResponseList);
        data.put("sizePage", commentPage.getSize());
        data.put("totalElement", commentPage.getTotalElements());
        data.put("totalPages", commentPage.getTotalPages());
        data.put("currentPage", commentPage.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/products/{productId}/comments")
    public ResponseEntity<?> getCommentsByProductId(
            @PathVariable Long productId,
            @PageableDefault(page = 0, size = 3)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "commentId") String sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "asc") String order) {

        if (!productService.existsProductById(productId)) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, 3, Sort.by(order.equals("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy)));
        Page<Comment> commentPage = commentService.getCommentsByProductId(productId, pageable);

        List<Comment> comments = commentPage.getContent();
        List<CommentResponse> commentResponseList = comments.stream()
                .map(comment -> CommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .fullName(comment.getUser().getFullName())
                        .content(comment.getContent())
                        .commentDate(comment.getCreatedAt())
                        .build())
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("comments", commentResponseList);
        data.put("sizePage", commentPage.getSize());
        data.put("totalElement", commentPage.getTotalElements());
        data.put("totalPages", commentPage.getTotalPages());
        data.put("currentPage", commentPage.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long productId,
            @PathVariable Long commentId) {

        if (!productService.existsProductById(productId)) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = productService.getCommentsByProductId(productId);
        Optional<Comment> commentOptional = comments.stream()
                .filter(comment -> comment.getCommentId().equals(commentId))
                .findFirst();

        if (!commentOptional.isPresent()) {
            return new ResponseEntity<>("Comment not found for the given product", HttpStatus.NOT_FOUND);
        }

        commentService.deleteComment(commentId);

        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }


    // Order
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @PageableDefault(page = 0, size = 3)
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "sortBy", required = false, defaultValue = "orderId") String sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(name = "status", required = false) OrderStatus status
    ) {
        Pageable pageable = PageRequest.of(page, 3, Sort.by(order.equals("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy)));
        Page<Order> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatusWithPaginationAndSorting(status, pageable);
        } else {
            orders = orderService.getAllOrdersWithPaginationAndSorting(pageable);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orders", orders.getContent());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long orderId) throws AppException {
        Optional<Order> orderOptional = orderService.getOrderById(orderId);
        Order order = orderOptional.orElseThrow(() -> new AppException("Order not found"));

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId, @RequestParam String orderStatus) throws AppException {
        orderService.updateOrderStatus(orderId, OrderStatus.valueOf(orderStatus));
        return new ResponseEntity<>("Order status updated successfully!", HttpStatus.OK);
    }

    @GetMapping("/dash-board/best-sellers")
    public ResponseEntity<List<Product>> getBestSellingProducts(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(defaultValue = "10") int limit) {

        Page<Product> bestSellingProducts = productService.getBestSellingProducts(month, year, limit);
        List<Product> productList = bestSellingProducts.getContent();
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

}
