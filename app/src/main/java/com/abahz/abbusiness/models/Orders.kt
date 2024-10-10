package com.abahz.abbusiness.models

import java.io.Serializable

data class Orders(
    val id:String ="",
    val name:String ="",
    val client:String ="",
    val date:String ="",
    val qty:String ="",
    val total:String ="",
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
