package com.example.drawer;

import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;

public class MQTTController {

    private static final int qos = 2;
    private static final String broker = "tcp://10.0.2.2:1883";
    private static final String clientId = "MQTT-publisher";
    private static final MemoryPersistence persistence = new MemoryPersistence();;
    private static MqttClient mqttClient;
    private static final String TAG = "MainActivity";
    private static final String STARTTAG = "Startup";
    private static final String SUBTAG = "Subscription";
    private static final String PUBTAG = "Publishing";
    private static final String ETAG = "Error";
    private static final HashMap<TextView, String> subscriptionMap = new HashMap<>();

    private MQTTController() {

    }

    public static void connect() {
        try {
            //Create client
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            //Attempt connection
            Log.d(STARTTAG, "Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            if (!notConnected()) {
                Log.d(STARTTAG, "Connected");
            } else {
                Log.d(ETAG, "Could not connect");
            }

            //Enable callback
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d(ETAG, "Connection lost");
                    throwable.printStackTrace();
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    String message = new String(mqttMessage.getPayload());

                    // TODO optimize, not crucial due to small size of hashmap, but needed eventually. -MH
                    // potentially make it topic keyed, then have lists of objects, could be good
                    subscriptionMap.forEach((text, topic) -> {
                        if (s.equals(topic)) text.setText(message);
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //needed for MQTT callback, useful for debugging.
                }
            });
        } catch (MqttException e) {
            //Standard error printing
            Log.d(ETAG, "Could not connect");
            e.printStackTrace();
        }
    }

    public static boolean notConnected() {
        if (mqttClient == null) return true;
        return !mqttClient.isConnected();
    }

    public static void update(TextView text, String topic) {
        subscriptionMap.put(text, topic);
    }

    public static void subscribe(String topic) {
        if (notConnected()) {
            Log.d(ETAG, "Not connected to MQTT broker.");
            return;
        }
        try {
            mqttClient.subscribe(topic, 0);
            Log.d(SUBTAG, "Subscribed to: " + topic);
        } catch (MqttException e) {
            //Standard error printing
            Log.d(ETAG, "Subscription could not be performed");
            e.printStackTrace();
        }
    }

    public static void publish(String topic, String content) {
        if (notConnected()) {
            Log.d(ETAG, "Not connected to MQTT broker.");
            return;
        }
        try {
            Log.d(PUBTAG, "Publishing message: " + content);
            Log.d(PUBTAG, "                to: " + topic);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            //Standard error printing
            Log.d(ETAG, "Message could not be published");
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (notConnected()) {
            Log.d(ETAG, "Not connected to MQTT broker.");
            return;
        }
        try {
            mqttClient.disconnect();
            Log.d(TAG, "Disconnected");
        } catch (MqttException e) {
            //Standard error printing
            Log.d(ETAG, "Could not disconnect from broker");
            e.printStackTrace();
        }
    }

    public static void init() {
        subscriptionMap.clear();
    }
}
