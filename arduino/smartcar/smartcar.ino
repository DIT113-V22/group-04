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

//flags for controlling obstacle detection
auto auto_drive = 0;
auto obstacle_detection = 0;

const auto mqttInterval = 200UL;

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

struct instruction {
    double Distance;
    double Angle;
};

std::vector<instruction> instructions;
bool followInstructions = false;
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

void setup() {
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
  mqtt.publish("/smartcar/report/startup", "Connected to MQTT broker.");


  //MQTT subscription
  mqtt.subscribe("/smartcar/control/#", 1);
  mqtt.publish("/smartcar/report/startup", "MQTT subscriptions setup complete.");

  mqtt.onMessage([](String topic, String message) {
    //Allow setting flags regardless of current detection flag
    if (topic == "/smartcar/control/auto") {
      auto_drive = message.toInt();
      if (message.toInt() == 1) {
        mqtt.publish("/smartcar/report/status", "Auto drive enabled.");
      } else {
        mqtt.publish("/smartcar/report/status", "Manual drive enabled.");
      }
    }
    if (topic == "/smartcar/control/obstacle") {
      obstacle_detection = message.toInt();
      if (message.toInt() == 1) {
        mqtt.publish("/smartcar/report/status", "Flagged obstacle detection to true.");
      } else {
        mqtt.publish("/smartcar/report/status", "Flagged obstacle detection to false.");
      }
    }
    
    //Block further instructions unless flags above have been reset.
    if (obstacle_detection == 1) {
      return;
    }
    
    if (topic == "/smartcar/control/throttle") {
      car.setSpeed(message.toInt());
    } else if (topic == "/smartcar/control/steering") {
      car.setAngle(message.toInt());
    } else if (topic == "/smartcar/control/instructions") {
        String data = message;
        int iteratorNew = 0;
        int iteratorOld = 0;
        String distance;
        String angle;
        
        //10.0 -190; 3.5, 145; 9, 45;
        for (int i = 0; i < data.length(); i++) {
          if (data.charAt(i) == ';') {
            iteratorNew = i;
            String sub = data.substring(iteratorOld, iteratorNew);
            //Serial.println(sub);
            for (int j = 0; j < sub.length(); j++) {
              if (sub.charAt(j) == ',') {
                distance = sub.substring(0, j);
                angle = sub.substring((j + 1), (sub.length() - 1));
                instructions.push_back({distance.toDouble(), angle.toDouble()});
                break;
              }
            }
            iteratorOld = i + 1;
          }
        }
        followInstructions = true;
    } else {
      Serial.println(topic + " " + message);
    }
  });
}

int instructionIndex = 0;
int lastOdometer = 0;

double targetDistance;
double targetAngle;

void loop() {

  unsigned int distance = ultrasonic.getDistance();
  unsigned int distanceTraveled = odometer.getDistance();
  
  //Main MQTT loop
  if (mqtt.connected()) {
    mqtt.loop();
    const auto currentTime = millis();
    const auto obstacle_distance = String(distance);
    static auto previousTransmission = 0UL;

    if (distance <= 100 && distance > 0) {
      if ((auto_drive == 1) && (obstacle_detection == 0)) {
        car.setSpeed(0);
        obstacle_detection = 1;
        mqtt.publish("/smartcar/report/obstacle", "1");
      }
    }
    
    if (currentTime - previousTransmission >= mqttInterval) { // every 200ms
      previousTransmission = currentTime;
      
      //instructions
      if (followInstructions == true) {
        //Serial.println("test");
        if (instructionIndex == 0) {
          //car.setSpeed(50);
          targetDistance = instructions[instructionIndex].Distance;
          //Serial.println(targetDistance);
          targetAngle = instructions[instructionIndex].Angle;
          //Serial.println(targetAngle);
          instructionIndex++;
          lastOdometer = distanceTraveled / 100;
        }

        if (((distanceTraveled / 100) - lastOdometer) >= targetDistance) {
          car.setAngle(targetAngle);
          delay(1000);
          car.setAngle(0);
  
          instructionIndex++;
          targetDistance = instructions[instructionIndex].Distance;
          targetAngle = instructions[instructionIndex].Angle;
          Serial.println("changing target angle");
          lastOdometer = distanceTraveled;
        }
      }
      mqtt.publish("/smartcar/report/odometer", String(distanceTraveled));
      mqtt.publish("/smartcar/report/ultrasound", obstacle_distance);
    }
    //camera
    #ifdef __SMCE__
      static auto previousCameraTransmission = 0UL;
      if (currentTime - previousCameraTransmission >= 65) { //15fps
        previousCameraTransmission = currentTime;
        Camera.readFrame(frameBuffer.data());
        mqtt.publish("/smartcar/report/camera", frameBuffer.data(), frameBuffer.size(), false, 0);
      }
    #endif
  }

  #ifdef __SMCE__
    // Avoid over-using the CPU if we are running in the emulator
    delay(1);
  #endif
}
