package com.app.my_project.controller;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.app.my_project.annotation.Public;
import com.app.my_project.annotation.RequireRole;
import com.app.my_project.entity.UserEntity;
import com.app.my_project.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;


// UserApiController: REST API controller for managing user data
@RestController
@RequestMapping("/api/users")
public class UserApiController {
    // Repository for accessing user data from the database
    private final UserRepository userRepository;
    private static final long EXPIRATION_TIME = 60 * 60 * 1000 * 24 * 7; // JWT Token expiration time (1 week)
    // BCrypt hashes passwords with a per-password salt; plaintext is never stored
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Constructor for injecting UserRepository
    public UserApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users (GET /api/users) - the staff list is admin-only
    @RequireRole("admin")
    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll(Sort.by("id"));
    }

    // Get JWT Secret (loaded from .env or environment via WebConfig)
    private String getSecret() {
        return com.app.my_project.WebConfig.getSecret();
    }

    // Create JWT Algorithm with secret
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(getSecret());
    }

    // Admin Signin: Check username, password and create JWT Token (POST
    // /api/users/admin-signin)
    // @Public: the only endpoint reachable without a token - you cannot
    // log in if login itself requires being logged in
    @Public
    @PostMapping("/admin-signin")
    public Object adminSigin(@RequestBody UserEntity user) {
        String u = user.getUsername();
        String p = user.getPassword();

        // Look up by username only, then compare the BCrypt hash —
        // never query the database by plaintext password
        UserEntity userForCreateToken = userRepository.findByUsername(u);
        // 401, and the same message either way: telling the caller which half was
        // wrong would let them enumerate valid usernames
        if (userForCreateToken == null || !passwordEncoder.matches(p, userForCreateToken.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        String token = JWT.create()
                .withSubject(String.valueOf(userForCreateToken.getId())) // subject = user id
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // วันหมดอายุ
                .withIssuedAt(new Date()) // วันออก token
                .withClaim("role", userForCreateToken.getRole())
                .sign(getAlgorithm()); // เซ็น token
        String role = userForCreateToken.getRole();
        record UserResponse(String token, String role) {
        }
        return new UserResponse(token, role);
    }

    // Get admin info from JWT token (GET /api/users/admin-info)
    @GetMapping("/admin-info")
    public Object adminInfo(@RequestHeader("Authorization") String token) {
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
        Long userId = Long.valueOf(JWT.decode(tokenWithoutBearer).getSubject());
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        record UserResponse(Long id, String username, String email, String role) {
        }

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    // Get userId from JWT token (for endpoints that need userId from token)
    public Long getUserIdFromToken(String token) {
        String tokenWithoutBearer = token.replace("Bearer ", "");

        if (tokenWithoutBearer.trim().isEmpty()) {
            throw new IllegalArgumentException("Token is empty");
        }

        return Long.valueOf(
                JWT.require(getAlgorithm())
                        .build()
                        .verify(tokenWithoutBearer)
                        .getSubject());
    }

    // Admin can edit any user (PUT /api/users/admin-edit-profile/{id})
    // The role check lives in @RequireRole, read by JwtInterceptor: one place
    // instead of an isAdmin() call copy-pasted into every admin method, and a
    // rejection now returns 403 rather than a 500 from a thrown exception.
    @RequireRole("admin")
    @PutMapping("/admin-edit-profile/{id}")
    public UserEntity adminEditProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody UserEntity user) {
        try {
            UserEntity userToUpdate = userRepository.findById(id).orElse(null);
            if (userToUpdate == null)
                throw new IllegalArgumentException("User not found");
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(userToUpdate);
            return userToUpdate;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("update user error: " + e.getMessage());
        }
    }

    @RequireRole("admin")
    @PutMapping("/admin-update-profile/{id}")
    public UserEntity adminUpdateProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody UserEntity user) {
        try {
            UserEntity userToUpdate = userRepository.findById(id).orElse(null);
            if (userToUpdate == null)
                throw new IllegalArgumentException("User not found");
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userToUpdate.setRole(user.getRole());
            userRepository.save(userToUpdate);
            return userToUpdate;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("update user error: " + e.getMessage());
        }
    }

    // Admin create new user (POST /api/users/admin-create)
    @RequireRole("admin")
    @PostMapping("/admin-create")
    public UserEntity adminCreate(
            @RequestHeader("Authorization") String token,
            @RequestBody UserEntity user) {
        // IllegalStateException -> 409 Conflict: the request was well formed, it just
        // collides with a username that is already taken
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalStateException("Username '" + user.getUsername() + "' already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    // Admin delete user (DELETE /api/users/admin-delete/{id})
    @RequireRole("admin")
    @DeleteMapping("/admin-delete/{id}")
    public void adminDelete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        UserEntity userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null)
            throw new IllegalArgumentException("User not found");
        userRepository.deleteById(id);
    }

    // User or admin edit their own profile (PUT /api/users/edit-profile)
    @PutMapping("/edit-profile")
    public UserEntity editOwnProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserEntity user) {
        try {
            Long userId = getUserIdFromToken(token); // ดึง userId จาก JWT token
            UserEntity userToUpdate = userRepository.findById(userId).orElse(null);
            if (userToUpdate == null)
                throw new IllegalArgumentException("User not found");
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(userToUpdate);
            return userToUpdate;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("update user error: " + e.getMessage());
        }
    }

}