package com.la.javaweb.repository;

import com.la.javaweb.model.Product;
import com.la.javaweb.model.ShoppingCart;
import com.la.javaweb.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserAndProduct(Users user, Product product);
    List<ShoppingCart> findByUser(Users user);
    @Modifying
    @Query("DELETE FROM ShoppingCart s WHERE s.user.username = :username")
    void deleteAllByUsername(@Param("username") String username);
}
