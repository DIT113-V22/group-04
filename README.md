# group-04

![MegaLinter](https://github.com/DIT113-V22/group-04/actions/workflows/mega-linter.yml/badge.svg)
![Android CI](https://github.com/DIT113-V22/group-04/actions/workflows/android-ci.yml/badge.svg)
![Arduino CI](https://github.com/DIT113-V22/group-04/actions/workflows/arduino-ci.yml/badge.svg)
![Java CI](https://github.com/DIT113-V22/group-04/actions/workflows/java-ci.yml/badge.svg)

## About the project

### What are we going to make?

We will make an Android application that will command a car to follow a certain path, either by drawing that path on the app
or by manually controlling the car using a joystick. On both modes (manual control and draw control), the user is able to
control the car such that it moves at a certain speed (for e.g. the speed can be set to 50%). Driven paths can also be saved
and by extension, a previously saved path can be converted to movement instructions that is sent to the car. There will also
be a camera that shows the car's point of view to assist the user when they are using the manual driving mode.

### Why will we make it?

The car will be able to follow a given path. This functionality can be used in factories or warehouses where the floor plan is 
seldom standard. Thus, this program can be utilised to draw paths for automation purposes which will work with any layout.

Another use case would be to use this app for "robots" like Roombas (cleaning robot); as the user can manually drive a path once and 
then the roomba can save that path, so it is always able to clean the room/house in that specific order.

### How are we going to make it?

Communication between the android app and the car will be handled using MQTT and specifically, HiveMQ, a MQTT broker.

The mobile app will allow the user to draw a path on a grid-based canvas and movement instructions will be extracted from it 
which will then be sent to the so that it can follow the path.  With the use of the sensors and the SmartCar library, 
the car can follow the path safely and properly.

The joystick will be made from scratch. To make the joystick custom circle drawables, Math library and OnTouch functionality of
views will be used. The joystick will publish speed and angle to the car via a broker. The Arduino sketch will receive and 
execute these instructions. The saving of instructions will be done using an SQLite database.

Technologies used:

- HiveMQ
- SmartCar
- SmartCar Shield Library
- Arduino IDE 1.8.19
- SMCE Emulator
- Android API

## System Architecture

### Component Diagram

![ComponenetDiagram](https://user-images.githubusercontent.com/90007777/170896586-bcf16160-a54e-4915-88e8-8f987ab8e4d6.png)

### Deployment Diagram

![DeploymentDiagram](https://user-images.githubusercontent.com/40069897/171967908-dfb49c16-f5a7-4120-8880-8953803b6c0f.png)

## Getting started

### Prerequisite software

* [Arduino IDE](https://www.arduino.cc/en/software)
* [HiveMQ](https://github.com/hivemq/hivemq-community-edition)
* [SMCE-gd](https://github.com/ItJustWorksTM/smce-gd)

*and their prerequisites*

### Installation

1. Install all prerequisite software as advised on the respective pages.
2. Clone this repository.

### HiveMQ setup
*Please note that this will run HiveMQ as a local service on the computer it starts on, and assumes you are aware of how port-forwarding works to enable other devices to connect to it should you want to control the car via a physical phone for example.*

3. Navigate to your HiveMQ install directory
    1. You will probably need to edit the run.bat or run.sh to modify the port used, as it is commonly used by other programs and features.
    2. Edit port (default 9010) on line 55 in run.sh if on a UNIX system, or line 68 in run.bat if on a windows system, and save.
    3. Run the run.sh normally, or the run.bat as administrator if on a windows system.

### SMCE-gd setup

4. Start SMCE-gd
    1. Select "Start Fresh"
    2. Select the **+** on the top left of the screen
    3. Select "Add New"
    4. Navigate to the cloned repository, then /arduino and select the smartcar.ino sketch.
    5. Select the sketch in SMCE
    6. Select "Compile"
    7. Select "Start"

#### If using Java tool for testing

5. Open the project in your IDE of choice
6. If you need to make modifications to the messages and topics, you can do so, but note that the arduino sketch will need to be updated as well. In which case, you only need to save and recompile the sketch in SMCE.
7. Have fun!

#### If using the Android app

5. Open the project in Android Studio or another android IDE to compile and run there.
    1. To run this program on a physical arduino device, you can utilise android developer mode, and a USB cable to compile and install the application on the connected device.
    2. Make sure you are connected to the same network as the computer being used to run SMCE, if you go this route.
    * Using mobile hotspot is a viable alternative.
7. Connect to the MQTT broker
8. Have fun!

## Project Demo

[Demo Video](https://www.youtube.com/watch?v=mE4N443-brs)

## Contributors

Aieh Eissa

Burak Askan

Chanisra Charoenpol Magnusson

Kevin Collins

Mathias Hallander

Sejal Kanaskar
