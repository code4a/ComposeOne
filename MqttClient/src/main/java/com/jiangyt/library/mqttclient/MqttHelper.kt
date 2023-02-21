package com.jiangyt.library.mqttclient

import android.content.Context
import android.util.Log
import com.jiangyt.library.mqttclient.data.*
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttHelper private constructor() {

    private lateinit var mqttClient: MqttAndroidClient

    // TAG
    companion object {
        const val TAG = "AndroidMqttClient"
        const val SERVER_URI = "wss://zqjpg0.cn1.mqtt.chat:443"
        const val MQ_UNAME = "jiangyt"
        const val MQ_PWD =
            "YWMtHf4eDK2pEe2D7Ll36BbHr0xSMLtmfUxfhjfwwc80HYY5HHpArSsR7Yumu_fmyiW1AwMAAAGGWD6HFjeeSACgbbx73lk5viwr-wEZ_lcihXZva_jaroKzwkwf3RoCRQ"

        private const val MQ_TOPIC_BASE = "/wifi_car"
        private const val MQ_TOPIC_UP = "/up"
        private const val MQ_TOPIC_DOWN = "/down"
        const val MQ_TOPIC_INIT = "${MQ_TOPIC_BASE + MQ_TOPIC_DOWN}/init"
        const val MQ_TOPIC_CB_TURN = "${MQ_TOPIC_BASE + MQ_TOPIC_DOWN}/turn"
        const val MQ_TOPIC_CB_POWER = "${MQ_TOPIC_BASE + MQ_TOPIC_DOWN}/power"
        const val MQ_TOPIC_CB_SERVO = "${MQ_TOPIC_BASE + MQ_TOPIC_DOWN}/servo"
        const val MQ_TOPIC_TURN = "${MQ_TOPIC_BASE + MQ_TOPIC_UP}/turn"
        const val MQ_TOPIC_POWER = "${MQ_TOPIC_BASE + MQ_TOPIC_UP}/power"
        const val MQ_TOPIC_SERVO = "${MQ_TOPIC_BASE + MQ_TOPIC_UP}/servo"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MqttHelper()
        }
    }

    fun connect(context: Context) {
        mqttClient = MqttAndroidClient(context, SERVER_URI, "car_control_client@zqjpg0")
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
                topic?.let {
                    when (it) {
                        MQ_TOPIC_INIT -> {
                            message?.payload?.let { ba ->
                                Log.d(TAG, String(ba))
                            } ?: kotlin.run {
                                // 空消息
                            }
                        }
                        MQ_TOPIC_CB_TURN -> {

                        }
                        MQ_TOPIC_CB_POWER -> {

                        }
                        else -> {

                        }
                    }
                }
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "Connection lost ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(TAG, "Delivery complete ${token.toString()}")
            }
        })
        val options = MqttConnectOptions()
        options.userName = MQ_UNAME
        options.password = MQ_PWD.toCharArray()

        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                    subscribe(MQ_TOPIC_INIT)
                    subscribe(MQ_TOPIC_CB_TURN)
                    subscribe(MQ_TOPIC_CB_POWER)
                    subscribe(MQ_TOPIC_CB_SERVO)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure $exception")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }

    fun subscribe(topic: String, qos: Int = 1) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to subscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to unsubscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * 转向
     */
    fun sendTurn(@Duration duration: Int) {
        publish(MQ_TOPIC_TURN, Turn(duration).toJson())
    }

    /**
     * 前进/后退
     */
    fun sendPower(@Motor power: Int) {
        publish(MQ_TOPIC_POWER, Power(power).toJson())
    }

    /**
     * 舵机转向
     */
    fun sendServo(angle: Int) {
        publish(MQ_TOPIC_SERVO, Servo(angle).toJson())
    }

    private fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to disconnect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

}