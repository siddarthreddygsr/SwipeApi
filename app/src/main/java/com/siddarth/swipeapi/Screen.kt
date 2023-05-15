package com.siddarth.swipeapi

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object AddProduct : Screen("addProduct")
}