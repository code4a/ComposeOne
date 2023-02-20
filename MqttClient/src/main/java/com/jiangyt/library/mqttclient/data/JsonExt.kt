package com.jiangyt.library.mqttclient.data

import com.google.gson.Gson

inline fun <reified T> String.toBean(): T {
    return Gson().fromJson(this, T::class.java)
}