package com.example.drawer;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTController {

    private static final int qos = 2;
    private static final String broker = "tcp://10.0.2.2:1883";
    private static final String clientId = "MQTT-publisher";
    private static MemoryPersistence persistence;
    private static MqttClient mqttClient;

    private MQTTController() {
        persistence = new MemoryPersistence();
    }

    public static void connect() {
        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");
        } catch (MqttException e) {
            //Standard error printing
            System.out.println("Could not connectd");
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        return mqttClient.isConnected();
    }

    public static void subscribe(String topic) {
        if (!isConnected()) {
            System.out.println("Not connected to MQTT broker.");
            return;
        }
        try {
            mqttClient.subscribe(topic, 0);
            System.out.println("Subscribed to: " + topic);
        } catch (MqttException e) {
            //Standard error printing
            System.out.println("Subscription could not be performed");
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
    }

    public static void publish(String topic, String content) {
        if (!isConnected()) {
            System.out.println("Not connected to MQTT broker.");
            return;
        }
        try {
            System.out.println("Publishing message: " + content + "\nto: " + topic);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            mqttClient.publish(topic, message);

            System.out.println("Message published");
        } catch (MqttException e) {
            //Standard error printing
            System.out.println("Message not published");
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (!isConnected()) {
            System.out.println("Not connected to MQTT broker.");
            return;
        }
        try {
            mqttClient.disconnect();
            System.out.println("Disconnected");
        } catch (MqttException e) {
            //Standard error printing
            System.out.println("Could not disconnect");
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
    }
}
