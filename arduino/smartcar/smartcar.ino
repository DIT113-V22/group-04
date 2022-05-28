#include <MQTT.h>
#include <WiFi.h>
#include <Smartcar.h>
#ifdef __SMCE__
#include <OV767X.h>
#endif

#define OFFSET 0

//Variable declaration
WiFiClient net;
MQTTClient mqtt;

const char ssid[] = "";
const char pass[] = "";

const unsigned long PULSES_PER_METER = 40;

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

int instructionIndex = 0;
int lastOdometer = 0;

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
  Serial.println("Connected");

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
        //resets
        instructionIndex = 0;
        instructions.clear();
      
        String data = message;
        int iteratorNew = 0;
        int iteratorOld = 0;
        String distance;
        String angle;
        Serial.println("test");
        //2.0, 20
        
        //10.0 -190; 3.5, 145; 9, 45;
        for (int i = 0; i < data.length(); i++) {
          if (data.charAt(i) == ';') {
            iteratorNew = i;
            String sub = data.substring(iteratorOld, iteratorNew);
            //Serial.println(sub);
            for (int j = 0; j < sub.length(); j++) {
              if (sub.charAt(j) == ',') {
                Serial.print("sub: ");
                Serial.println(sub);
                distance = sub.substring(0, j);
                angle = sub.substring((j + 1), (sub.length() - 1));
                instructions.push_back({distance.toDouble(), angle.toDouble()});
                delay(100);
                break;
              }
            }
            iteratorOld = i + 1;
          }
        }
        for (int i = 0; i < instructions.size(); i++) {
          Serial.print("Distance: ");
          Serial.print(instructions[i].Distance);
          Serial.print(", angle: ");
          Serial.println(instructions[i].Angle);
          delay(100);
        }
        followInstructions = true;
    } else {
      Serial.println(topic + " " + message);
    }
  });
}

double targetDistance;
double targetAngle;
auto currentTime = millis();

void loop() {
  unsigned int distance = ultrasonic.getDistance();
  unsigned int distanceTraveled = odometer.getDistance();
  //Serial.println("current distance: " + String(distanceTraveled));
  
  //Main MQTT loop
  if (mqtt.connected()) {
    mqtt.loop();
    currentTime = millis();
    const auto obstacle_distance = String(distance);
    static auto previousTransmission = 0UL;

    if (distance <= 100 && distance > 0) {
      if ((auto_drive == 1) && (obstacle_detection == 0)) {
        car.setSpeed(0);
        obstacle_detection = 1;
        mqtt.publish("/smartcar/report/obstacle", "1");
      }
    }
    
    //instructions
    if (followInstructions == true && (instructionIndex < instructions.size())) {
      if (instructionIndex == 0) {
        Serial.println("first instruction");
        //car.setSpeed(50);
        targetDistance = instructions[instructionIndex].Distance;
        targetAngle = instructions[instructionIndex].Angle;
        Serial.println(String(targetDistance));
        Serial.println(String(targetAngle));
        
        lastOdometer = distanceTraveled;
        instructionIndex++;
      }

      if ((distanceTraveled - lastOdometer) >= targetDistance) {
        Serial.println("rotation started");
        rotateCar(targetAngle);
        Serial.println("rotation complete");
        car.setAngle(0);

        if (instructionIndex < instructions.size()) {
          targetDistance = instructions[instructionIndex].Distance;
          targetAngle = instructions[instructionIndex].Angle;
          Serial.println("traveled: " + String(distanceTraveled));
          Serial.println("last: " + String(lastOdometer));
          Serial.println("target: " + String(targetDistance));
          Serial.println("changing target angle");
          lastOdometer = distanceTraveled;
          instructionIndex++;
        } else {
          car.setSpeed(0);
        }
      }
    }
    
    if (currentTime - previousTransmission >= mqttInterval) {
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
      
    #ifdef __SMCE__
      // Avoid over-using the CPU if we are running in the emulator
      delay(1);
    #endif
  }
}

void rotateCar(double angle) {
  #define CALIBRATE360 6301
  #define SPEED 50

  car.setSpeed(0);
  
  if (angle > 0) {
    car.overrideMotorSpeed(   SPEED, - SPEED);
  } else if (angle < 0) {
    car.overrideMotorSpeed( - SPEED,   SPEED);
  }

  delay(6301);
  
  car.setSpeed(40);
}
