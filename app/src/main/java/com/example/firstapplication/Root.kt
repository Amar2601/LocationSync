package com.example.firstapplication

typealias Root = List<Root2>

data class Root2(
    val userId: Long,
    val id: Long,
    val title: String,
    val body: String,
)
