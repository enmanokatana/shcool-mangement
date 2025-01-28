package org.example.scool.models;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "professors")
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_number", unique = true, nullable = false)
    private String employeeNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "department")
    private String department;

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Module> modules;
}