package com.abahz.abbusiness.models

import java.time.Month

data class Notes(
    val id:String ="",
    var client:String ="",
    var note:String ="",
    val date:String ="",
    val month:String ="",
    var price:String ="",
    val sync:String =""){
    constructor():this(
        "",
        "",
        "",
        "",
        "",
        "",
        "")
}
