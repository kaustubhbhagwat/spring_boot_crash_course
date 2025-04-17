package com.kaustubh.spring_boot_crash_course.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("JWT_SECRET_BASE64")private val jwtSecret: String
) {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 15L * 60 * 1000L
    val refreshTokenValidityMs = 30 * 24 * 60 * 60 * 1000L

    private fun generateToken(
        userId: String,
        type:String,
        expiry: Long
        ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)

        return Jwts.builder()
            .subject(userId)
            .claim("type",type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey,Jwts.SIG.HS256)
            .compact()
    }


    fun generateAccessToken(userId: String): String{
        return generateToken(userId,"access",accessTokenValidityMs)
    }

    fun generateRefreshToken(userId: String): String{
        return generateToken(userId,"refresh",refreshTokenValidityMs)
    }
}