# group-04

## About the project

---

### What are we going to make?

A car that is able to follow a path that is drawn by the user. Also, a previously saved path can be used by the car to traverse. 
The car can also move at an assigned speed (for e.g. the speed can be set to 50%). Furthermore, the 
car can also be controlled using a gyroscope and the path taken can be recorded. For the manual driving of the car, point of view
of car shall be in the manual screen to assist the driver.

### Why will we make it?

The car will be able to follow a given path. So this functionality can be used in factories or warehouses where the floor plan is seldom standard.
Thus, this program can be utilised to draw paths for automation purposes which will work with any layout.


On the other hand, the car will also be able to be controlled via a joystick and save the path that was driven.
This could be beneficial to, for example, Roombas (cleaning robot) as the user can manually drive a path once and 
then the roomba can save that path, so it is always able to clean the room/house in that specific order.

### How are we going to make it?

The mobile app will allow the user to create a grid-based drawing which will be converted into a set of instructions that the car can execute.
The set of instructions that is pulled from a grid-based drawing will be saved along with the image of the drawing.
With the use of the sensors and the SmartCar library, the car can follow the path safely and properly.

The joystick will be made from scratch. To make the joystick custom circle drawables, Math library and OnTouch functionality of views will be used.
The joystick will publish speed and angle to the car via a broker. Arduino codebase will recieve and execute these instructions.
Saving of instructions will done with GSON by transforming necessary instructions into JSON.
For the camera, a premade template will be used with along with a connection via a MQTT broker. 


Technologies used:

- HiveMQ
- SmartCar
- SmartCar Shield Library
- Arduino IDE 1.8.19
- SMCE Emulator
- Android API


## Getting started

---
### Prerequisites


### Installation

The project requires few software installations as pre-requisits.

## HiveMQ setup
*Please note that this will run HiveMQ as a local service on the computer it starts on, and assumes you are aware of how port-forwarding works to enable other devices to connect to it should you want to control the car via a physical phone for example.*

 Navigate to your HiveMQ install directory
    1. You will probably need to edit the run.bat or run.sh to modify the port used, as it is commonly used by other programs and features.
    2. Edit port (default 9010) on line 55 in run.sh if on a UNIX system, or line 68 in run.bat if on a windows system, and save.
    3. Run the run.sh normally, or the run.bat as administrator if on a windows system. 

## Start SMCE-gd
    1. Select "Start Fresh"
    2. Select the **+** on the top left of the screen
    3. Select "Add New"
    4. Navigate to the cloned repository, then /arduino and select the smartcar.ino sketch.
    5. Select the sketch in SMCE
    6. Select "Compile"
    7. Select "Start"

#### If using Java tool for testing

- Open the project in your IDE of choice
- If you need to make modifications to the messages and topics, you can do so, but note that the arduino sketch will need to be updated as well. In which case, you only need to save and recompile the sketch in SMCE.
- Have fun!

#### If using the Android app

- Open the project in Android Studio or another android IDE to compile and run there.
    1. To run this program on a physical arduino device, you can utilise android developer mode, and a USB cable to compile and install the application on the connected device.
    2. Make sure you are connected to the same network as the computer being used to run SMCE, if you go this route.
    * Using mobile hotspot is a viable alternative.
- Connect to the MQTT broker
- Have fun!


## Contributors

---

Aieh Eissa

Burak Askan

Chanisra Charoenpol Magnusson

Kevin Collins

Mathias Hallander

Sejal Kanaskar
