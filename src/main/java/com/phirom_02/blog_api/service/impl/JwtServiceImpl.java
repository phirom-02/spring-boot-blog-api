package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of the {@link JwtService} interface for handling JWT operations.
 */
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    String jwtSecret;

    /**
     * Extracts the subject (username) from the provided JWT token.
     * The subject typically contains the user’s username or email.
     *
     * @param token the JWT token from which the subject is extracted.
     * @return the subject (username) from the token.
     */
    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Builds a JWT token with the provided user details and additional claims.
     * It includes the user’s username, issued date, expiration date, and the specified claims.
     *
     * @param userDetails     the user details (typically username) to be embedded in the token.
     * @param extraClaims     additional claims to include in the token.
     * @param tokenExpiration the expiration time of the token in milliseconds.
     * @return the generated JWT token.
     */
    @Override
    public String buildToken(UserDetails userDetails, Map<String, Object> extraClaims, Long tokenExpiration) {
        return Jwts.builder()
                .claims(extraClaims) // Additional claims to add
                .subject(userDetails.getUsername()) // Subject (usually username)
                .issuedAt(new Date(System.currentTimeMillis())) // Token issued time
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration)) // Token expiration time
                .signWith(getSignInKey()) // Signing the token with the secret key
                .compact(); // Return the compact JWT token
    }

    /**
     * Returns the secret key used to sign the JWT token. The secret key is decoded from the base64 encoded string
     * configured in the application properties.
     *
     * @return the secret key used for signing the JWT token.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret); // Decode the base64-encoded secret key
        return Keys.hmacShaKeyFor(keyBytes); // Return the secret key for signing
    }

    /**
     * Extracts a specific claim from the provided JWT token.
     * This method allows you to extract any claim from the JWT token using a claim resolver function.
     *
     * @param token          the JWT token to extract claims from.
     * @param claimsResolver the function to extract the claim from the parsed claims.
     * @param <T>            the type of the claim to extract.
     * @return the extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Extract all claims
        return claimsResolver.apply(claims); // Apply the claim resolver function to the claims
    }

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token the JWT token to extract claims from.
     * @return the claims extracted from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // Initialize the JWT parser
                .verifyWith(getSignInKey()) // Set the signing key for verification
                .build()
                .parseSignedClaims(token) // Parse the JWT token
                .getPayload(); // Extract the claims body
    }
}
