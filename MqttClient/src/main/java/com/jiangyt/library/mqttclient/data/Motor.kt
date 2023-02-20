package com.jiangyt.library.mqttclient.data

import androidx.annotation.IntDef

@IntDef(Motor.FRONT, Motor.BACK)
@Retention(AnnotationRetention.SOURCE)
annotation class Motor {
    companion object {
        const val FRONT = 1
        const val BACK = 2
    }
}
