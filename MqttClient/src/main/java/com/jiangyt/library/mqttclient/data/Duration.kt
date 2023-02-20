package com.jiangyt.library.mqttclient.data

import androidx.annotation.IntDef

@IntDef(Duration.LEFT, Duration.UP_LEFT, Duration.RIGHT, Duration.UP_RIGHT)
@Retention(AnnotationRetention.SOURCE)
annotation class Duration {
    companion object {
        const val LEFT = 1
        const val UP_LEFT = 2
        const val RIGHT = 3
        const val UP_RIGHT = 4
    }
}
