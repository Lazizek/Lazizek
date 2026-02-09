package com.example.smartenergy.data.mqtt

import android.content.Context
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttClientWrapper @Inject constructor(context: Context) {
    private val client = MqttAndroidClient(context, "tcp://192.168.1.100:1883", "android-smart-energy")
    private val incoming = MutableSharedFlow<Pair<String, String>>(extraBufferCapacity = 100)

    init {
        client.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) = Unit
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (topic != null && message != null) incoming.tryEmit(topic to message.toString())
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
        })
    }

    fun incomingMessages() = incoming.asSharedFlow()

    fun connect(username: String, password: String) {
        if (client.isConnected) return
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = false
            userName = username
            this.password = password.toCharArray()
        }
        client.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: org.eclipse.paho.client.mqttv3.IMqttToken?) = Unit
            override fun onFailure(asyncActionToken: org.eclipse.paho.client.mqttv3.IMqttToken?, exception: Throwable?) = Unit
        })
    }

    fun disconnect() {
        if (client.isConnected) client.disconnect()
    }

    fun subscribe(topics: List<String>) {
        topics.forEach { if (client.isConnected) client.subscribe(it, 1) }
    }

    fun publishCommand(topic: String, payload: String) {
        if (client.isConnected) client.publish(topic, MqttMessage(payload.toByteArray()))
    }
}
