package ar.com.phostech.demo.entities

import java.io.Serializable

open class InsigthsCredentials
constructor(
    val id: Long,
    val name: String,
    val email: String,
    val accessKey: String,
    val valid: Boolean = true
) : Serializable
