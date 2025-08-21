package com.app.my_project;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.models.TokenRequest;
import com.app.models.UserModel;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import io.github.cdimascio.dotenv.Dotenv;

@RestController
@RequestMapping("/jwt") // Base URL for JWT-related endpoints
public class UserController {
    // Token expiration time set to 24 hours
    private static final long EXPIRATION_TIME = 60 * 60 * 1000 * 24; // 1 day

    // Method to retrieve the JWT secret from the .env file
    public String getSecret() {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir") + "/my-project")
                .load();
        return dotenv.get("JWT_SECRET");
    }

    // Method to get the HMAC256 algorithm instance with the secret
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(getSecret());
    }

    // Endpoint to create a JWT token
    @PostMapping("/create")
    public String createToken(@RequestBody UserModel user) {
        // Create a new UserModel instance for token creation
        UserModel userForCreateToken = new UserModel(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRole());

        // Generate and return the JWT token
        return JWT.create()
                .withSubject(String.valueOf(userForCreateToken.getId())) // Set user ID as the subject
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Set expiration time
                .withIssuedAt(new Date()) // Set issued-at timestamp
                .sign(getAlgorithm()); // Sign the token with the algorithm
    }

    /*
     * // Endpoint to validate a JWT token //วิธีนี้ไม่ค่อยเวิร์ก ไปใช้วิธี token
     * (PostMapping ด้านล่างดีกว่า)
     * 
     * @GetMapping("/check/{token}")
     * public String checkToken(@PathVariable String token) {
     * try {
     * // Create a JWT verifier with the algorithm
     * JWTVerifier verifier = JWT.require(getAlgorithm()).build();
     * // Verify the token and extract the subject (user ID)
     * String id = verifier.verify(token).getSubject();
     * 
     * return "Token is valid for user id: " + id; // Return success message
     * } catch (JWTVerificationException e) {
     * return "Token is invalid or expired: " + e.getMessage(); // Return error
     * message
     * }
     * }
     */

    @PostMapping("/check")
    public Map<String, Object> checkToken(@RequestBody TokenRequest tokenRequest) {
        // Create a response map to store the result of the token validation
        Map<String, Object> response = new HashMap<>();
        try {
            // Create a JWT verifier using the HMAC256 algorithm and the secret key
            JWTVerifier verifier = JWT.require(getAlgorithm()).build();
            // Verify the token and extract the subject (user ID) from the token
            String id = verifier.verify(tokenRequest.getToken()).getSubject();

            // Populate the response map with success details
            response.put("status", "success"); // Indicate the operation was successful
            response.put("message", "Token is valid"); // Provide a success message
            response.put("userId", id); // Include the user ID extracted from the token
        } catch (JWTVerificationException e) {
            // Log the invalid token attempt for monitoring and debugging purposes
            System.err.println("Invalid token attempt: " + e.getMessage());

            // Populate the response map with error details
            response.put("status", "error"); // Indicate the operation failed
            response.put("message", "Token is invalid or expired"); // Provide an error message
            response.put("error", e.getMessage()); // Include the exception message for debugging
        }
        // Return the structured response map containing the result of the validation
        return response;
    }

}
