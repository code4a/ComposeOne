package com.jiangyt.library.mqttclient.data

import com.google.gson.Gson

/**
 * 动力
 */
internal data class Power(@Motor private val power: Int) {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}