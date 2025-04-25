package com.kaustubh.spring_boot_crash_course.security

import com.kaustubh.spring_boot_crash_course.database.model.RefreshToken
import com.kaustubh.spring_boot_crash_course.database.model.User
import com.kaustubh.spring_boot_crash_course.database.repository.RefreshTokenRepository
import com.kaustubh.spring_boot_crash_course.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    fun register(email: String, password: String): User {
        val user = userRepository.findByEmail(email.trim())
        if (user != null){
            throw ResponseStatusException(HttpStatus.CONFLICT,"User with this email already exists.")
        }
       return userRepository.save(
            User(
                email= email,
                hashedPassword =  hashEncoder.encode(password)
        ))
    }

    fun login(email: String, password: String): TokenPair{
        val user = userRepository.findByEmail(email)
            ?: throw  BadCredentialsException("Invalid Credentials")

        if(!hashEncoder.matches(password,user.hashedPassword)){
            throw BadCredentialsException("Invalid Credentials")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshedToken(user.id,newRefreshToken)

        return TokenPair(
            accessToken=  newAccessToken,
            refreshToken = newRefreshToken)
    }

    private fun storeRefreshedToken(userId: ObjectId,rawRefreshToken:String){
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                createdAt = Instant.now(),
                hashedToken =hashed
            )
        )
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair{
        if(!jwtService.validateRefreshToken(refreshToken)) {
            throw ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid Refresh Token")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow{
            throw ResponseStatusException(HttpStatusCode.valueOf(404),"Refresh Token Not Found")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id,hashed)
            ?: throw ResponseStatusException(
                HttpStatusCode.valueOf(401),
                "Refresh maybe used or expired"
            )
        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id,hashed)
        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)
        storeRefreshedToken(user.id,newAccessToken)

        return TokenPair(newAccessToken,newRefreshToken)
    }

    private fun hashToken(token: String): String{
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(token.encodeToByteArray())
        return  java.util.Base64.getEncoder().encodeToString(hashBytes)
    }
}