package com.abahz.abbusiness.models

import java.io.Serializable

data class Products (
    val name:String ="",
    val image:String ="",
    val unity:String ="",
    var price:String ="",
    val total:String ="",
    var qty:String ="",
    val sync:String =""
): Serializable{
    constructor():this(
        "",
        "",
        "",
        "",
        "",
        "",
        "")
}