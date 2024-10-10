package com.abahz.abbusiness.models

import java.io.Serializable

data class Carts(
    val id:String ="",
    val name:String ="",
    val image:String ="",
    val unity:String ="",
    val total:String ="",
    val qty:String ="",
) : Serializable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "")
}