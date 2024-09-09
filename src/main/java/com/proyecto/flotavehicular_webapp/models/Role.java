package com.proyecto.flotavehicular_webapp.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long roleId;

    private String roleName;

    private String roleDescription;

    @ManyToMany(mappedBy = "roles")
    private Set<Users> users = new HashSet<>();
}
