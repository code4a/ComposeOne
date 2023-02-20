package com.jiangyt.library.mqttclient.data

import com.google.gson.Gson

/**
 * 转向：
 */
internal data class Turn(@Duration private val duration: Int) {

    fun toJson(): String {
        return Gson().toJson(this)
    }
}