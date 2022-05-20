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



## Contributors

---

Aieh Eissa

Burak Askan

Chanisra Charoenpol Magnusson

Kevin Collins

Mathias Hallander

Sejal Kanaskar
