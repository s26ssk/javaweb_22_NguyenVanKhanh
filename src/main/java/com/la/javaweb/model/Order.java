package com.la.javaweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private Users user;

    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String receiveName;

    private String receiveAddress;

    private String receivePhone;

    private String note;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private Set<OrderDetails> orderDetails;

    private Date createdAt;

    private Date receivedAt;
}
