#include <Smartcar.h>

//Variable declaration
const unsigned long PULSES_PER_METER = 40;
const unsigned short TRIGGER_PIN = 6;
const unsigned short ECHO_PIN = 7;
const unsigned int MAX_DISTANCE = 500;
const unsigned int offset = 0;

//Car creation
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor{arduinoRuntime, smartcarlib::pins::v2::leftMotorPins};
BrushedMotor rightMotor{arduinoRuntime, smartcarlib::pins::v2::rightMotorPins};
DifferentialControl control{leftMotor, rightMotor};
DirectionalOdometer odometer(arduinoRuntime, smartcarlib::pins::v2::leftOdometerPins, []() {odometer.update();}, PULSES_PER_METER);
GY50 gyroscope(arduinoRuntime, offset);
SR04 ultrasonic(arduinoRuntime, TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
SmartCar car(arduinoRuntime, control, gyroscope, odometer);


void setup(){
    Serial.begin(9600);
    Serial.println("Starting car at 60% speed");
    car.setSpeed(60);
}

void loop() {
    unsigned int distance = ultrasonic.getDistance();

    Serial.println(distance);

    if (distance <= 100 && distance > 0) {
        car.setSpeed(-50);
        delay(200);
        car.overrideMotorSpeed(100, -100);
        delay(100);
    } else {
        car.setSpeed(40);
    }

#ifdef __SMCE__
    // Avoid over-using the CPU if we are running in the emulator
    delay(1);
#endif
}