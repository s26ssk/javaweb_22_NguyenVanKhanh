package com.la.javaweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullName;

    private String username;

    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<Address> address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_detail",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Roles> roles;
    private boolean status = true;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<WishList> wishList;

}
