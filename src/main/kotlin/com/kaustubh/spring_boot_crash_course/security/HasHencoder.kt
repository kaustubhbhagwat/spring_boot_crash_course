package com.kaustubh.spring_boot_crash_course.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HasHencoder {

    private val bCrypt = BCryptPasswordEncoder()

    fun encode(rawString: String): String = bCrypt.encode(rawString)

    fun matches(rawString: String, hashed:String): Boolean =   bCrypt.matches(rawString,hashed)

}