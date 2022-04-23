package com.example.drawer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import android.os.Bundle;

//import com.google.firebase.firestore.local.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private static final String TAG = "MainActivity";
    private String topic = "/smartcar/control/throttle";
    private Button pubBtn;
    private Button disBtn;
    private Button conBtn;
    private Button subBtn;
    String content      = "50";
    int qos             = 2;
    String broker       = "tcp://broker.hivemq.com:1883";
    String clientId     = "MQTT-publisher";
    MemoryPersistence persistence = new MemoryPersistence();

    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MqttClient sampleClient = null;
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            //Attempt connection
            conBtn = findViewById(R.id.conBtn);
            MqttClient finalSampleClient2 = sampleClient;
            conBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Connecting to broker: " + broker);
                    try {
                        finalSampleClient2.connect(connOpts);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Connected");

                }
            });

            //Publishing
            pubBtn = findViewById(R.id.pubBtn);
            MqttClient finalSampleClient = sampleClient;
            pubBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Publishing message: " + content);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    try {
                        finalSampleClient.publish(topic, message);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Message published");
                }
            });

            disBtn = findViewById(R.id.disBtn);
            MqttClient finalSampleClient1 = sampleClient;
            disBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Disconnect client
                    try {
                        finalSampleClient1.disconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Disconnected");
                }
            });
        } catch (MqttException me) {
            //Standard error printing
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }

        subBtn = findViewById(R.id.subBtn);
        MqttClient finalSampleClient1 = sampleClient;
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    finalSampleClient1.subscribe(topic, 0);
                    finalSampleClient1.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable throwable) {
                            //log
                        }

                        @Override
                        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                            Log.d(TAG, "topic: " + topic);
                            Log.d(TAG, "message: " + new String(mqttMessage.getPayload()));
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                            //log or toast
                        }
                    });
                }catch (MqttException e){

                }
            }
        });

        readMeScreen = findViewById(R.id.ReadMeScreen);
        manualControlScreen = findViewById(R.id.ManualScreen);
        drawControlScreen = findViewById(R.id.DrawScreen);

        readMeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReadMEScreen();
            }
        });

        manualControlScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openManualScreen();
            }
        });

        drawControlScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawScreen();
            }
        });
    }

    public void openReadMEScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openManualScreen(){
        Intent intent = new Intent(this, ManualControl.class);
        startActivity(intent);
    }

    public void openDrawScreen(){
        Intent intent = new Intent(this, DrawControl.class);
        startActivity(intent);
    }

}