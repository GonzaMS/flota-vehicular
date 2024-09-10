//package com.proyecto.flotavehicular_webapp.models;
//
//import com.proyecto.flotavehicular_webapp.enums.ESTATES;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "users")
//public class Users {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long userId;
//
//    private String userName;
//
//    @Column(unique = true)
//    private String userEmail;
//
//    private String userPassword;
//
//    @Enumerated(EnumType.STRING)
//    private ESTATES userState;
//
//    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
//    private Driver conductor;
//
//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "users_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
//    private Set<Role> roles = new HashSet<>();
//}
//
//
