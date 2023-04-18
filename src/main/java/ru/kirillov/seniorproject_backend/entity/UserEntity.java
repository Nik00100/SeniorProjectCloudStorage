package ru.kirillov.seniorproject_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kirillov.seniorproject_backend.enums.Role;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String password;

    private String role;

    @ElementCollection
    private Set<Role> roles;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<FileEntity> fileList;
}
