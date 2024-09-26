    package com.la.javaweb.service.impl;

    import com.la.javaweb.model.Product;
    import com.la.javaweb.model.Users;
    import com.la.javaweb.model.WishList;
    import com.la.javaweb.repository.IWishListRepository;
    import com.la.javaweb.service.IProductService;
    import com.la.javaweb.service.IUserService;
    import com.la.javaweb.service.IWishListService;
    import com.la.javaweb.util.exception.AppException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.Set;
    import java.util.stream.Collectors;

    @Service
    public class WishListService implements IWishListService {
        @Autowired
        private IWishListRepository wishListRepository;

        @Autowired
        private IUserService userService;

        @Autowired
        private IProductService productService;

        @Override
        public void addToWishList(Long userId, Long productId) throws AppException{
            Users user = userService.getUserById(userId)
                    .orElseThrow(() -> new AppException("User not found"));

            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new AppException("Product not found"));

            WishList wishList = WishList.builder()
                    .user(user)
                    .product(product)
                    .build();

            wishListRepository.save(wishList);
        }

        @Override
        public Set<Product> getWishListByUserId(Long userId) throws AppException{
            Users user = userService.getUserById(userId)
                    .orElseThrow(() -> new AppException("User not found"));

            Set<WishList> wishList = user.getWishList();
            return wishList.stream().map(WishList::getProduct).collect(Collectors.toSet());
        }

        @Override
        public void removeFromWishList(Long wishListId) {
            wishListRepository.deleteById(wishListId);
        }
    }
