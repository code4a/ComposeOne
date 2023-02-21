package com.jiangyt.app.carremote

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jiangyt.app.carremote.view.RockerView
import com.jiangyt.library.mqttclient.MqttHelper
import com.jiangyt.library.mqttclient.data.Duration
import com.jiangyt.library.mqttclient.data.Motor

class MainActivity : AppCompatActivity() {

    private lateinit var mqttHelper: MqttHelper
    private lateinit var mLogLeft: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mqttHelper = MqttHelper.instance
        mqttHelper.connect(this)
        mLogLeft = findViewById(R.id.logLeft)
        findViewById<RockerView>(R.id.rockerViewServo).setOnAngleChangeListener(
            object : RockerView.OnAngleChangeListener {
                override fun onStart() {
                }

                override fun angle(angle: Double) {
                    mqttHelper.sendServo(angle.toInt())
                }

                override fun onFinish() {
                }

            })
        findViewById<RockerView>(R.id.rockerViewLeft).apply {
            setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE)
            setOnShakeListener(RockerView.DirectionMode.DIRECTION_8,
                object : RockerView.OnShakeListener {
                    override fun onStart() {
                        mLogLeft.text = null
                    }

                    override fun direction(direction: RockerView.Direction) {
                        var message: String? = null
                        when (direction) {
                            RockerView.Direction.DIRECTION_LEFT -> {
                                message = "左"
                                mqttHelper.sendTurn(Duration.LEFT)
                            }
                            RockerView.Direction.DIRECTION_RIGHT -> {
                                message = "右"
                                mqttHelper.sendTurn(Duration.RIGHT)
                            }
                            RockerView.Direction.DIRECTION_UP -> {
                                message = "上"
                            }
                            RockerView.Direction.DIRECTION_DOWN -> {
                                message = "下"
                            }
                            RockerView.Direction.DIRECTION_UP_LEFT -> {
                                message = "左上"
                                mqttHelper.sendTurn(Duration.UP_LEFT)
                            }
                            RockerView.Direction.DIRECTION_UP_RIGHT -> {
                                message = "右上"
                                mqttHelper.sendTurn(Duration.UP_RIGHT)
                            }
                            RockerView.Direction.DIRECTION_DOWN_LEFT -> {
                                message = "左下"
                            }
                            RockerView.Direction.DIRECTION_DOWN_RIGHT -> {
                                message = "右下"
                            }
                            else -> {}
                        }
                        mLogLeft.text = "摇动方向 : $message"
                    }

                    override fun onFinish() {
                        mLogLeft.text = null
                    }
                })
        }
        findViewById<Button>(R.id.power_front).setOnClickListener {
            mqttHelper.sendPower(Motor.FRONT)
        }
        findViewById<Button>(R.id.power_back).setOnClickListener {
            mqttHelper.sendPower(Motor.BACK)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttHelper.disconnect()
    }
}