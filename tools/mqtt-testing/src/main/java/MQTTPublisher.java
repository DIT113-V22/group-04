import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTPublisher {

    public static void main(String[] args) {

        String topic        = "/smartcar/control/throttle";
        String content      = "50";
        int qos             = 2;
        String broker       = "tcp://localhost:1883";
        String clientId     = "MQTT-publisher";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            //Generate MQTT client
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            //Attempt connection
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            //Publishing
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");

            //Disconnect client
            sampleClient.disconnect();
            System.out.println("Disconnected");

            //Exit program
            System.exit(0);
        } catch (MqttException me) {
            //Standard error printing
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}