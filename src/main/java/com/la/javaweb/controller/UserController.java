package com.la.javaweb.controller;

import com.la.javaweb.dto.request.*;
import com.la.javaweb.dto.response.AddressResponse;
import com.la.javaweb.dto.response.CartItemResponse;
import com.la.javaweb.dto.response.CommentResponse;
import com.la.javaweb.dto.response.ProductResponse;
import com.la.javaweb.model.*;
import com.la.javaweb.service.*;
import com.la.javaweb.util.exception.AppException;
import jakarta.validation.Valid;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RestController
@RequestMapping("")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IProductService productService;
    @Autowired
    private ICommentService commentService;
    @Autowired
    private IWishListService wishListService;
    @Autowired
    private IShoppingCartService shoppingCartService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IOrderDetailsService orderDetailsService;


    // Account
    @GetMapping("/account")
    public ResponseEntity<Users> getUserAccount() throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);

        Users user = userService.getUserByUsername(username)
                .orElseThrow(() -> new AppException("User not found"));

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/account/change-password")
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

    @PutMapping("/account")
    public ResponseEntity<String> updateCurrentUser(@RequestBody UpdateUserRequest updateUserRequest) throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new AppException("User not found"));

        currentUser.setFullName(updateUserRequest.getFullName());

        userService.updateUser(currentUser);

        return new ResponseEntity<>("User information updated successfully", HttpStatus.OK);
    }

    @GetMapping("/account/address")
    public ResponseEntity<List<AddressResponse>> getUserAddresses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            List<AddressResponse> addresses = userService.getUserAddresses(username);
            return new ResponseEntity<>(addresses, HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/account/address/{addressId}")
    public ResponseEntity<AddressResponse> getUserAddressById(@PathVariable Long addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            AddressResponse addressResponse = userService.getUserAddressById(username, addressId);
            return new ResponseEntity<>(addressResponse, HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/account/address")
    public ResponseEntity<String> addAddress(@RequestBody AddressRequest addressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userService.addAddress(username, addressRequest);

        return new ResponseEntity<>("Address added successfully", HttpStatus.OK);
    }

    @DeleteMapping("/account/address/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            userService.deleteAddress(username, addressId);
            return new ResponseEntity<>("Address deleted successfully", HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // Category
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
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
        List<ProductResponse> productResponses = productPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("product", productResponses);
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
        List<ProductResponse> productResponses = productsPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("products", productResponses);
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
            ProductResponse productResponse = mapToProductResponse(product);
            return new ResponseEntity<>(productResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/products/new-products")
    public ResponseEntity<List<ProductResponse>> getLatestProducts(@PageableDefault(page = 0, size = 5) Pageable pageable) {
        Page<Product> latestProducts = productService.getLatestProducts(pageable);
        List<Product> products = latestProducts.getContent();
        List<ProductResponse> productResponses = products
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }

    @GetMapping("/products/categories/{categoryId}")
    public ResponseEntity<Map<String, Object>> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 3) Pageable pageable) {
        Page<Product> products = productService.getProductsByCategoryId(categoryId, pageable);
        List<ProductResponse> productResponses = products.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("products", productResponses);
        data.put("sizePage", products.getSize());
        data.put("totalElement", products.getTotalElements());
        data.put("totalPages", products.getTotalPages());
        data.put("currentPage", products.getNumber());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    @GetMapping("/products/best-sellers")
    public ResponseEntity<List<ProductResponse>> getBestSellingProducts(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(defaultValue = "3") int limit) {

        Page<Product> bestSellingProducts = productService.getBestSellingProducts(month, year, limit);
        List<Product> productList = bestSellingProducts.getContent();
        List<ProductResponse> productResponses = productList
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }

    // Comment
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

    @PostMapping("/products/{productId}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable Long productId,
            @RequestBody CommentRequest commentRequest) {

        try {
            if (!productService.existsProductById(productId)) {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>("User needs to login to comment", HttpStatus.UNAUTHORIZED);
            }
            String username = authentication.getName();

            AtomicBoolean isExists = new AtomicBoolean(false);
            List<Order> orders = orderService.getAllOrderByUserAndStatus(username,OrderStatus.SUCCESS);
            orders.forEach(item -> {
                item.getOrderDetails().forEach(orderDetail -> {
                    if(orderDetail.getProduct().getProductId().equals(productId)) {
                        isExists.set(true);
                    }
                });
            });
            if(isExists.get()) {
                Comment newComment = Comment.builder()
                        .content(commentRequest.getContent())
                        .user(userService.getUserByUsername(username).orElseThrow(() -> new AppException("User needs to login to comment")))
                        .product(productService.getProductById(productId).orElseThrow(() -> new AppException("Product not found")))
                        .createdAt(new Date())
                        .build();

                commentService.saveComment(newComment);

                return new ResponseEntity<>("Comment added successfully", HttpStatus.OK);
            } else {
                throw new AppException("You need to have successfully purchased the product to be able to comment.");
            }



        } catch (AppException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/products/{productId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long productId,
            @PathVariable Long commentId) throws AppException {

        if (!productService.existsProductById(productId)) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = productService.getCommentsByProductId(productId);
        Optional<Comment> commentOptional = comments.stream()
                .filter(comment -> comment.getCommentId().equals(commentId))
                .findFirst();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("User needs to login to delete comment", HttpStatus.UNAUTHORIZED);
        }

        if (!commentOptional.isPresent()) {
            return new ResponseEntity<>("Comment not found for the given product", HttpStatus.NOT_FOUND);
        }

        if (!commentOptional.get().getUser().getUsername().equals(username)) {
            return new ResponseEntity<>("Unauthorized: You are not the owner of this comment", HttpStatus.UNAUTHORIZED);
        }

        commentService.deleteCommentOfUser(commentId);

        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long productId,
            @PathVariable Long commentId,
            @RequestBody CommentRequest updateComment) {
        try {
            if (!productService.existsProductById(productId)) {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }

            List<Comment> comments = productService.getCommentsByProductId(productId);
            Optional<Comment> commentOptional = comments.stream()
                    .filter(comment -> comment.getCommentId().equals(commentId))
                    .findFirst();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>("User needs to login to delete comment", HttpStatus.UNAUTHORIZED);
            }

            if (!commentOptional.isPresent()) {
                return new ResponseEntity<>("Comment not found for the given product", HttpStatus.NOT_FOUND);
            }

            if (!commentOptional.get().getUser().getUsername().equals(username)) {
                return new ResponseEntity<>("Unauthorized: You are not the owner of this comment", HttpStatus.UNAUTHORIZED);
            }

            commentService.updateComment(commentId, updateComment);
            return new ResponseEntity<>("Comment edited successfully", HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // WishList
    @GetMapping("/wish-list")
    public ResponseEntity<?> getWishList() throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("User needs to login to view the wishlist", HttpStatus.UNAUTHORIZED);
        }

        String username = authentication.getName();
        Users user = userService.getUserByUsername(username)
                .orElseThrow(() -> new AppException("User not found"));

        Set<Product> wishList = wishListService.getWishListByUserId(user.getUserId());

        return new ResponseEntity<>(wishList, HttpStatus.OK);
    }

    @PostMapping("/wish-list")
    public ResponseEntity<String> addToWishList(@RequestParam Long productId) throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users user = userService.getUserByUsername(username)
                .orElseThrow(() -> new AppException("User not found"));

        wishListService.addToWishList(user.getUserId(), productId);

        return new ResponseEntity<>("Product added to wishlist successfully", HttpStatus.OK);
    }

    @DeleteMapping("/wish-list/{wishListId}")
    public ResponseEntity<String> removeFromWishList(@PathVariable Long wishListId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("User needs to login to remove a product from the wishlist", HttpStatus.UNAUTHORIZED);
        }

        wishListService.removeFromWishList(wishListId);
        return new ResponseEntity<>("Product removed from the wishlist successfully", HttpStatus.OK);
    }

    // Cart
    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getCartItems() throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<CartItemResponse> cartItems = shoppingCartService.getCartItems(username);
        double totalPrice = cartItems.stream()
                .mapToDouble(CartItemResponse::getTotalPriceProduct)
                .sum();
        Map<String, Object> data = new HashMap<>();
        data.put("listCartItem", cartItems);
        data.put("totalPrice", totalPrice);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping("/cart")
    public ResponseEntity<String> addProductToCart(@RequestBody CartItemRequest cartItemRequest) throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        shoppingCartService.addProductToCart(username, cartItemRequest);
        return new ResponseEntity<>("Added product to cart successfully", HttpStatus.CREATED);
    }

    @PutMapping("/cart/{shoppingCartId}")
    public ResponseEntity<String> updateCartItemQuantity(
            @PathVariable Long shoppingCartId,
            @RequestBody CartItemRequest cartItemRequest) throws AppException {
        shoppingCartService.updateCartItemQuantity(shoppingCartId, cartItemRequest);
        return new ResponseEntity<>("Cart item quantity updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/cart/{shoppingCartId}")
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long shoppingCartId) throws AppException {
        shoppingCartService.removeProductFromCart(shoppingCartId);
        return new ResponseEntity<>("The product has been successfully removed from the shopping cart", HttpStatus.OK);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<String> removeAllProductsFromCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        shoppingCartService.removeAllProductsFromCart(username);
        return new ResponseEntity<>("All product has been successfully removed from the shopping cart", HttpStatus.OK);
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<String> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest) throws AppException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<CartItemResponse> cart = shoppingCartService.getCartItems(username);
        if (cart.isEmpty()) {
            throw new AppException("Your cart is empty. Please add products to your cart before checking out.");
        }

        List<CartItemResponse> cartItems = shoppingCartService.getCartItems(username);

        double totalPrice = cartItems.stream()
                .mapToDouble(CartItemResponse::getTotalPriceProduct)
                .sum();

        Order order = Order.builder()
                .user(userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found")))
                .totalPrice(totalPrice)
                .orderDetails(cartItems.stream()
                        .map(cartItem -> OrderDetails.builder()
                                .product(productService.getProductById(cartItem.getProductId()).orElseThrow(() -> new RuntimeException("Product not found")))
                                .orderQuantity(cartItem.getQuantity())
                                .unitPrice(cartItem.getTotalPriceProduct())
                                .build())
                        .collect(Collectors.toSet()))
                .receiveAddress(checkoutRequest.getFullAddress())
                .receivePhone(checkoutRequest.getPhone())
                .receiveName(checkoutRequest.getReceiveName())
                .note(checkoutRequest.getNote())
                .user(userService.getUserByUsername(username).orElseThrow(() -> new RuntimeException("User not found")))
                .status(OrderStatus.WAITING)
                .createdAt(new Date())
                .receivedAt(new Date(new Date().getTime() + 4 * 24 * 60 * 60 * 1000L))
                .build();

        orderService.placeOrder(order);
        List<OrderDetails> orderDetailsList = cartItems.stream()
                .map(cartItem -> {
                    try {
                        return OrderDetails.builder()
                                .product(productService.getProductById(cartItem.getProductId()).orElseThrow(() -> new AppException("Product not found")))
                                .orderQuantity(cartItem.getQuantity())
                                .unitPrice(cartItem.getTotalPriceProduct())
                                .order(order)
                                .build();
                    } catch (AppException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        orderDetailsService.saveAll(orderDetailsList);

        shoppingCartService.clearCart(username);

        return new ResponseEntity<>("Order placed successfully!", HttpStatus.OK);
    }

    // Order
    @GetMapping("/orders/history")
    public ResponseEntity<List<Order>> getOrderHistory(@RequestParam(required = false) String orderStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<Order> orderHistory;

        if (orderStatus != null && !orderStatus.isEmpty()) {
            orderHistory = orderService.getOrdersByUsernameAndStatusOrderByCreatedAtDesc(username, OrderStatus.valueOf(orderStatus));
        } else {
            orderHistory = orderService.getOrdersByUsernameOrderByCreatedAtDesc(username);
        }

        return new ResponseEntity<>(orderHistory, HttpStatus.OK);
    }
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        Optional<Order> canceledOrder = orderService.cancelOrder(orderId);
        if (canceledOrder.isPresent()) {
            return new ResponseEntity<>("Order canceled successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unable to cancel the order. Either it does not exist or is not in WAITING state.", HttpStatus.NOT_FOUND);
        }
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .exportPrice(product.getExportPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .images(product.getImages())
                .createdAt(product.getCreatedAt())
                .build();
    }
}

