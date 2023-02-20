package com.jiangyt.example.composeone.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jiangyt.library.mqttclient.MqttHelper

class MqttClientActivity : ComponentActivity() {

    private lateinit var mqttHelper: MqttHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mqttHelper = MqttHelper.instance
        mqttHelper.connect(this)
        //mqttHelper.subscribe(MqttHelper.MQ_TOPIC_INIT)
    }

    override fun onDestroy() {
        super.onDestroy()
        //mqttHelper.unsubscribe(MqttHelper.MQ_TOPIC_INIT)
        mqttHelper.disconnect()
    }
}