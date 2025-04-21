package com.kaustubh.spring_boot_crash_course.controllers

import com.kaustubh.spring_boot_crash_course.database.model.RefreshToken
import com.kaustubh.spring_boot_crash_course.security.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
){

    data class AuthRequest(
        val email: String,
        val password: String
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    @PostMapping("/register")
    private fun register(
       @RequestBody body : AuthRequest
    ){
        authService.register(body.email,body.password)
    }

    @PostMapping("/login")
    private fun login(
        @RequestBody body : AuthRequest
    ): AuthService.TokenPair{
        return authService.login(body.email,body.password)
    }

    @PostMapping("/refresh")
    private fun refresh(
        @RequestBody body : RefreshRequest
    ): AuthService.TokenPair{
        return  authService.refresh(body.refreshToken)
    }
}