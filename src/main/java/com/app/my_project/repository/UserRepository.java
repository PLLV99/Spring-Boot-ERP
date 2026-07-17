package com.app.my_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.my_project.entity.UserEntity;

// This interface is part of the ORM (Object-Relational Mapping) setup using Spring Data JPA
// ORM is a technique that allows developers to interact with a database using object-oriented programming principles
// The UserEntity class is mapped to a database table, and this repository provides methods to perform CRUD operations on it
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Custom query method using Spring Data JPA's query derivation mechanism.
    // Passwords are BCrypt-hashed, so lookups are by username only and the
    // hash comparison happens in the controller via BCryptPasswordEncoder.
    UserEntity findByUsername(String username);
}
