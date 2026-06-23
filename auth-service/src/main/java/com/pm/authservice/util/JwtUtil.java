package com.pm.authservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    // Declare a final Key object that will be used for JWT signing and verification
    // 'final' ensures the key cannot be changed after construction (immutable security)
    private final Key secretKey;

    // Constructor that receives the JWT secret from application.properties/yml
    // @Value injects the property value from 'jwt.secret' configuration
    public JwtUtil(@Value("${jwt.secret}") String secret){

        // Step 1: Convert the Base64-encoded secret string to bytes
        // The secret is stored as Base64 to safely represent binary data as text
        // getBytes(StandardCharsets.UTF_8) ensures consistent encoding across platforms
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));

        // Step 2: Create a HMAC-SHA key from the decoded bytes
        // Keys.hmacShaKeyFor() creates a proper Key object suitable for JWT signing
        // This validates the key is the correct length for the HMAC-SHA algorithm
        // The key is stored in the final field for use in token generation/verification
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String role){
        return Jwts.builder()
                .subject(email)
                .claim("role",role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ 1000*60*60))
                .signWith(secretKey)
                .compact();

        //----->>> .subject(email) - Sets the "sub" (subject) claim in the JWT
                // The subject typically identifies the principal (user) of the token
                // In JWT standard, "sub" is a registered claim that represents the entity
                // the token is about (usually user ID or email)
                // This is used to identify WHICH user this token belongs to

        //----->>> .claim("role", role) - Adds a custom claim to the JWT payload
                // Claims are key-value pairs that store additional user information
                // "role" is a custom claim we're adding to store the user's authority
                // This will be used later for authorization (role-based access control)
                // Other examples: "userId", "permissions", "tenant", etc.

        //----->>> .issuedAt(new Date()) - Sets the "iat" (issued at) claim
                // Records when the token was created (current timestamp)
                // Used to determine token age and for time-based validations

        //----->>> .expiration(new Date(System.currentTimeMillis() + 1000*60*60))
                // Sets the "exp" (expiration) claim
                // Token expires after 1 hour (60 minutes) from creation
                // Calculation: 1000ms * 60 seconds * 60 minutes = 1 hour
                // After this time, token is considered invalid and will be rejected

        //----->>> .signWith(secretKey) - Signs the JWT with the HMAC-SHA key
                // This creates a digital signature that ensures:
                // 1. Token hasn't been tampered with (integrity)
                // 2. Token was issued by our server (authenticity)
                // The signature is created by hashing header + payload + secret

        //----->>> .compact() - Builds the final JWT string
                // Takes all the components (header, payload, signature) and:
                // 1. Base64Url-encodes the header
                // 2. Base64Url-encodes the payload (with all claims)
                // 3. Creates the signature using the secret
                // 4. Combines everything with dots: "header.payload.signature"
                // Returns a compact, URL-safe JWT string ready for transmission
    }
}
