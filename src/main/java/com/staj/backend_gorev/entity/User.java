package com.staj.backend_gorev.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data // Getter, Setter, gibi metodları otomatik oluşturur.
@NoArgsConstructor // Boş (parametresiz) constructor oluşturur (JPA için şart).
@AllArgsConstructor // Tüm ozellikleri içeren(name,surname...) dolu constructor oluşturur.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;
}