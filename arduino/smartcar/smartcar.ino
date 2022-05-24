#include <MQTT.h>
#include <WiFi.h>
#include <Smartcar.h>
#ifdef __SMCE__
#include <OV767X.h>
#endif

//Variable declaration
WiFiClient net;
MQTTClient mqtt;

const char ssid[] = "";
const char pass[] = "";

const unsigned long PULSES_PER_METER = 40;
const unsigned int OFFSET = 0;

const auto oneSecond = 1000UL;
#ifdef __SMCE__
const auto triggerPin = 6;
const auto echoPin = 7;
const auto mqttBrokerUrl = "127.0.0.1";
#else
const auto triggerPin = 33;
const auto echoPin = 32;
const auto mqttBrokerUrl = "127.0.0.1";
#endif
const auto maxDistance = 400;

std::vector<char> frameBuffer;


//Car creation
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor{arduinoRuntime, smartcarlib::pins::v2::leftMotorPins};
BrushedMotor rightMotor{arduinoRuntime, smartcarlib::pins::v2::rightMotorPins};
DifferentialControl control{leftMotor, rightMotor};
DirectionalOdometer odometer(arduinoRuntime, smartcarlib::pins::v2::leftOdometerPins, []() {odometer.update();}, PULSES_PER_METER);
GY50 gyroscope(arduinoRuntime, OFFSET);
SR04 ultrasonic(arduinoRuntime, triggerPin, echoPin, maxDistance);
SmartCar car(arduinoRuntime, control, gyroscope, odometer);


void setup(){
  Serial.begin(9600);
    
  WiFi.begin(ssid, pass);
  mqtt.begin(mqttBrokerUrl, 1883, net);

  #ifdef __SMCE__
    Camera.begin(QVGA, RGB888, 15);
    frameBuffer.resize(Camera.width() * Camera.height() * Camera.bytesPerPixel());
  #endif

  //Attempt WiFi connection
  Serial.println("Connecting to WiFi...");
  auto wifiStatus = WiFi.status();
  while (wifiStatus != WL_CONNECTED && wifiStatus != WL_NO_SHIELD) {
    Serial.println(wifiStatus);
    Serial.print(".");
    delay(1000);
    wifiStatus = WiFi.status();
  }
  Serial.println("WiFi connected");

  //Attempt MQTT connection
  Serial.println("Connecting to MQTT broker");
  while (!mqtt.connect("arduino", "public", "public")) {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("Connected to MQTT broker.");

  //MQTT subscription
  mqtt.subscribe("/smartcar/control/#", 1);
  mqtt.onMessage([](String topic, String message) {
    if (topic == "/smartcar/control/throttle") {
      car.setSpeed(message.toInt());
    } else if (topic == "/smartcar/control/steering") {
      car.setAngle(message.toInt());
    } else {
      Serial.println(topic + " " + message);
    }
  });
}

void loop() {
  unsigned int distance = ultrasonic.getDistance();
  unsigned int distanceTravled = odometer.getDistance();

  //Main MQTT loop
  if (mqtt.connected()) {
    mqtt.loop();
    const auto currentTime = millis();
    static auto previousTransmission = 0UL;
    if (currentTime - previousTransmission >= oneSecond) {
      previousTransmission = currentTime;
      const auto current_distance = String(distance);
      mqtt.publish("/smartcar/odometer/distance", String(distanceTravled));
      mqtt.publish("/smartcar/ultrasound/front", current_distance);
    }
   

    //camera
    #ifdef __SMCE__
      static auto previousCameraTransmission = 0UL;
      if (currentTime - previousCameraTransmission >= 65) { //15fps
        
         
        
        previousCameraTransmission = currentTime;
        Camera.readFrame(frameBuffer.data());
        mqtt.publish("/smartcar/camera", frameBuffer.data(), frameBuffer.size(), false, 0);
      }
    #endif
  }

  #ifdef __SMCE__
    // Avoid over-using the CPU if we are running in the emulator
    delay(1);
  #endif
}
