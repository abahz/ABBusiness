package com.abahz.abbusiness.models

import java.io.Serializable

data class Factures (
    val client :String= "",
    val total:String= "",
    val reduction:String= "",
    val date:String = "",
    val month:String = "",
    val sync:String ="",
    val id:String = ""
    ):Serializable{
        constructor():this("","","","","","","")
    }