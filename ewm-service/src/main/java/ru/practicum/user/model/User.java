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
    Long id;
    @Column(name = "name", nullable = false)
    String email;
    @Column(name = "email", nullable = false, unique = true)
    String name;
}
