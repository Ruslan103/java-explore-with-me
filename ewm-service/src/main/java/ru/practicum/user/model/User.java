package ru.practicum.user.model;


import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String email;
    @Column(name = "email", nullable = false, unique = true)
    private String name;
}
