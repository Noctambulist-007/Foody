package com.noctambulist.foody.data

data class User(
    val name: String,
    val email: String,
    val imagePath: String = ""
) {
    constructor() : this("", "", "")
}
