package com.example.SecureLoginDemo;

import io.jsonwebtoken.Jwts;

import java.util.Base64;

public class GenerateSecretKey {

    public static void main(String[] args) {
        var key = Jwts.SIG.HS256.key().build();

         String secret= Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println(secret);
    }
}


