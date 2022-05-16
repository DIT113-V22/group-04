package com.example.drawer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Controller for managing MQTT connections, subscriptions, and publishing of messages.
 *
 * @author Soarnir
 */
public class MQTTController {

    private static final int qos = 2;
    private static final String broker = "tcp://10.0.2.2:1883";
    private static final String clientId = "MQTT-publisher";
    private static final String TAG = "MainActivity";
    private static final String STARTTAG = "Startup";
    private static final String SUBTAG = "Subscription";
    private static final String PUBTAG = "Publishing";
    private static final String ETAG = "Error";
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;
    private final HashMap<String, HashMap<Integer, TextView>> subscriptionMap;
    private final ArrayList<ImageView> cameraViews;
    private final MemoryPersistence persistence;
    private MqttClient mqttClient;
    private static MQTTController mqttController_instance = null;
    private String previousMessage;

    /**
     * Constructs the MQTT controller, limited to one instance by Singleton pattern.
     */
    private MQTTController() {
        previousMessage = "";
        cameraViews = new ArrayList<>();
        persistence = new MemoryPersistence();
        subscriptionMap = new HashMap<>();
    }

    /**
     * Singleton pattern implementation, getting or creating a new singular instance.
     *
     * @return MQTTController instance
     */
    public static MQTTController getInstance() {
        if (mqttController_instance == null) {
            mqttController_instance = new MQTTController();
        }
        return mqttController_instance;
    }

    /**
     * Attempt connection to the MQTT broker specified.
     * Creates the client first, sets the callback method, and then attempts connection.
     */
    public void connect() {
        try {
            //Create client
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            //Enable callback
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d(ETAG, "Connection lost");
                    throwable.printStackTrace();
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) {
                    String message = new String(mqttMessage.getPayload());

                    if (topic.equals("/smartcar/camera")) {
                        final Bitmap bm = Bitmap
                                .createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

                        final byte[] payload = mqttMessage.getPayload();
                        final int[] colors = new int[IMAGE_WIDTH * IMAGE_HEIGHT];
                        for (int ci = 0; ci < colors.length; ++ci) {
                            final int r = payload[3 * ci] & 0xFF;
                            final int g = payload[3 * ci + 1] & 0xFF;
                            final int b = payload[3 * ci + 2] & 0xFF;
                            colors[ci] = Color.rgb(r, g, b);
                        }
                        bm.setPixels(colors, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
                        if (cameraViews.size() > 0) {
                            cameraViews.forEach(camera -> camera.setImageBitmap(bm));
                        }
                    }

                    if (subscriptionMap.get(topic) != null) {
                        subscriptionMap.get(topic).forEach((id, textView) -> textView.setText(message));
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //needed for MQTT callback, useful for debugging.
                }
            });

            //Attempt connection
            Log.d(STARTTAG, "Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            if (!notConnected()) {
                Log.d(STARTTAG, "Connected");
            } else {
                Log.d(ETAG, "Could not connect");
            }

        } catch (MqttException e) {
            //Standard error printing
            Log.d(ETAG, "Could not connect");
            e.printStackTrace();
        }
    }

    /**
     * Simple connection test.
     *
     * @return connection status
     */
    public boolean notConnected() {
        if (mqttClient == null) {
            return true;
        }
        return !mqttClient.isConnected();
    }

    /**
     * Adds any textview in the program to the map of current subscriptions.
     * This allows the controller to manage and update multiple textviews across all scenes.
     *
     * @param textView Android textview
     * @param topic MQTT topic
     */
    public void updateTextView(TextView textView, String topic) {
        HashMap<Integer, TextView> textViewHashMap = subscriptionMap.get(topic);
        if (textViewHashMap == null) {
            textViewHashMap = new HashMap<>();
        }
        textViewHashMap.put(textView.getId(), textView);
        subscriptionMap.put(topic, textViewHashMap);
    }

    /**
     * Add an imageview object to be updated with camera information.
     *
     * @param imageView Android imageview
     */
    public void updateCamera(ImageView imageView) {
        cameraViews.add(imageView);
    }

    /**
     * General subscription to any topic on the MQTT broker.
     *
     * @param topic subscribed topic
     */
    public void subscribe(String topic) {
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

    /**
     * Publish a message to the mqtt broker.
     *
     * @param topic published topic
     * @param content published message
     */
    public void publish(String topic, String content) {
        if (notConnected()) {
            Log.d(ETAG, "Not connected to MQTT broker.");
            return;
        }
        try {
            if (previousMessage.equals(content)) {
                return;
            }
            Log.d(PUBTAG, "Publishing message: " + content);
            Log.d(PUBTAG, "                to: " + topic);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            mqttClient.publish(topic, message);
            previousMessage = content;
        } catch (MqttException e) {
            //Standard error printing
            Log.d(ETAG, "Message could not be published");
            e.printStackTrace();
        }
    }

    /**
     * Attempt to disconnect from mqtt broker.
     */
    public void disconnect() {
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
}
