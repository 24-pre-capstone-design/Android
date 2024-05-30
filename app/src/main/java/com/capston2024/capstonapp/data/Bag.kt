package com.capston2024.capstonapp.data

import java.io.Serializable


data class Bag(
    val id:Int,
    val name: String,
    val price: Int,
    var count:Int,
): Serializable
