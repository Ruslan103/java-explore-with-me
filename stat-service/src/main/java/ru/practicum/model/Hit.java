package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "app", nullable = false)
    String app;    // example: ewm-main-service, Идентификатор сервиса для которого записывается информация
    @Column(name = "uri", nullable = false)
    String uri; // example: /events/1, URI для которого был осуществлен запрос
    @Column(name = "ip", nullable = false)
    String ip; // example: 192.163.0.1, IP-адрес пользователя, осуществившего запрос
    @Column(name = "data", nullable = false)
    LocalDateTime timestamp; // example: 2022-09-06 11:00:23, Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
