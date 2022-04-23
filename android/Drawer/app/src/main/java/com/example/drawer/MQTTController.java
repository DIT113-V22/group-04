package com.example.drawer;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTController {

    private final int qos = 2;
    private final String broker = "tcp://10.0.2.2:1883";
    private final String clientId = "MQTT-publisher";
    MemoryPersistence persistence;
    MqttClient mqttClient;

    public MQTTController() {
        persistence = new MemoryPersistence();
    }

    public void connect() {
        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");
        } catch (MqttException e) {
            //Standard error printing
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            mqttClient.subscribe(topic, 0);
            System.out.println("Subscribed to: " + topic);
        } catch (MqttException e) {
            //Standard error printing
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
        System.out.println("Subscription could not be performed");
    }

    public void publish(String topic, String content) {
        try {
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            mqttClient.publish(topic, message);

            System.out.println("Message published");
        } catch (MqttException e) {
            //Standard error printing
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
        System.out.println("Message not published");
    }

    public void disconnect() {
        //Disconnect client
        try {
            mqttClient.disconnect();
            System.out.println("Disconnected");
        } catch (MqttException e) {
            //Standard error printing
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        }
        System.out.println("Could not disconnect");
    }
}
