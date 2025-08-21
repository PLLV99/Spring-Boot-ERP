package com.app.my_project.controller;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.my_project.annotation.RequireAuth;
import com.app.my_project.entity.UserEntity;
import com.app.my_project.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import io.github.cdimascio.dotenv.Dotenv;

// UserApiController: REST API controller for managing user data
@RestController
@RequestMapping("/api/users")
public class UserApiController {
    // Repository for accessing user data from the database
    private final UserRepository userRepository;
    private static final long EXPIRATION_TIME = 60 * 60 * 1000 * 24 * 7; // JWT Token expiration time (1 week)

    // Constructor for injecting UserRepository
    public UserApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users (GET /api/users)
    @GetMapping
    @RequireAuth
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Create a new user (POST /api/users)
    @PostMapping
    public UserEntity createUser(@RequestBody UserEntity user) {
        return userRepository.save(user);
    }

    // Get user by id (GET /api/users/{id})
    @GetMapping("/{id}")
    public UserEntity getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Update user details (PUT /api/users/{id})
    @PutMapping("/{id}")
    public UserEntity updateUser(@PathVariable Long id, @RequestBody UserEntity user) {
        UserEntity userToUpdate = userRepository.findById(id).orElse(null);

        if (userToUpdate == null) {
            throw new IllegalArgumentException("User not found");
        }

        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setEmail(user.getEmail());

        return userRepository.save(userToUpdate);
    }

    // Delete user by id (DELETE /api/users/{id})
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    // Signin: Check username and email (POST /api/users/signin)
    @PostMapping("/signin")
    public UserEntity signin(@RequestBody UserEntity user) {
        String username = user.getUsername();
        String email = user.getEmail();

        UserEntity userToSignin = userRepository.findByUsernameAndEmail(username, email);

        if (userToSignin == null) {
            throw new IllegalArgumentException("User not found");
        }

        return userToSignin;
    }

    // Get JWT Secret from .env file in the my-project folder (used for token
    // creation)
    private String getSecret() {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir") + "/my-project")
                .load();
        return dotenv.get("JWT_SECRET");
    }

    // Create JWT Algorithm with secret
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(getSecret());
    }

    // Admin Signin: Check username, password and create JWT Token (POST
    // /api/users/admin-signin)
    @PostMapping("/admin-signin")
    public Object adminSigin(@RequestBody UserEntity user) {
        try {
            String u = user.getUsername();
            String p = user.getPassword();

            UserEntity userForCreateToken = userRepository.findByUsernameAndPassword(u, p);

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
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error creating token");
        }
    }

    // Get admin info from JWT token (GET /api/users/admin-info)
    @GetMapping("/admin-info")
    @RequireAuth
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

    // (Check if token has admin role)
    private boolean isAdmin(String token) {
        String tokenWithoutBearer = token.replace("Bearer ", "");
        String role = JWT.require(getAlgorithm())
                .build()
                .verify(tokenWithoutBearer)
                .getClaim("role").asString();
        return "admin".equals(role);
    }

    // Admin can edit any user (PUT /api/users/admin-edit-profile/{id})
    @PutMapping("/admin-edit-profile/{id}")
    public UserEntity adminEditProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody UserEntity user) {
        try {
            // (Check token for real admin role)
            if (!isAdmin(token)) {
                throw new IllegalArgumentException("You are not admin");
            }
            UserEntity userToUpdate = userRepository.findById(id).orElse(null);
            if (userToUpdate == null)
                throw new IllegalArgumentException("User not found");
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(user.getPassword());
            }
            userRepository.save(userToUpdate);
            return userToUpdate;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("update user error: " + e.getMessage());
        }
    }

    @PutMapping("/admin-update-profile/{id}")
    public UserEntity adminUpdateProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody UserEntity user) {
        try {
            // (Check token for real admin role)
            if (!isAdmin(token)) {
                throw new IllegalArgumentException("You are not admin");
            }
            UserEntity userToUpdate = userRepository.findById(id).orElse(null);
            if (userToUpdate == null)
                throw new IllegalArgumentException("User not found");
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(user.getPassword());
            }
            userToUpdate.setRole(user.getRole());
            userRepository.save(userToUpdate);
            return userToUpdate;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("update user error: " + e.getMessage());
        }
    }

    // Admin create new user (POST /api/users/admin-create)
    @PostMapping("/admin-create")
    public UserEntity adminCreate(
            @RequestHeader("Authorization") String token,
            @RequestBody UserEntity user) {
        try {
            // ตรวจสอบ token ว่าเป็น admin จริง (Check token for real admin role)
            if (!isAdmin(token)) {
                throw new IllegalArgumentException("You are not admin");
            }
            userRepository.save(user);
            return user;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Authentication error " + e.getMessage());
        }
    }

    // Admin delete user (DELETE /api/users/admin-delete/{id})
    @DeleteMapping("/admin-delete/{id}")
    public void adminDelete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            // ตรวจสอบ token ว่าเป็น admin จริง (Check token for real admin role)
            if (!isAdmin(token)) {
                throw new IllegalArgumentException("You are not admin");
            }
            UserEntity userToDelete = userRepository.findById(id).orElse(null);
            if (userToDelete == null)
                throw new IllegalArgumentException("User not found");
            userRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Authentication error " + e.getMessage());
        }
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
                userToUpdate.setPassword(user.getPassword());
            }
            userRepository.save(userToUpdate);
            return userToUpdate;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("update user error: " + e.getMessage());
        }
    }

}