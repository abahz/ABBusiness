package com.abahz.abbusiness.models

data class Users(
    val uid:String="",
    val shop:String="",
    val address:String="",
    val pass:String="",
    val devise:String=""){
    constructor():this(
        "",
        "",
        "",
        "",
        "")
}