package com.jiangyt.library.mqttclient.data

import com.google.gson.Gson

internal data class Servo(private val angle:Int){
    fun toJson(): String {
        return Gson().toJson(this)
    }
}
