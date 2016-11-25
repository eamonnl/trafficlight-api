Rest API Trafficlight Controller
===============================

Controlls a Cleware USB traffic light via Rest API.

## Hardware

You can buy the USB traffic light in this shop
 - http://www.cleware-shop.de
 
This application can run from a Raspberry PI

## Software

To control the USB device from your command line, you need the software from Cleware:
 - http://www.cleware.net/download.html

For Linux use this link:
 - http://www.vanheusden.com/clewarecontrol/
 
 
## Compiling
 
This maven module packages an executable jar. Compile it with `mvn package`.
 
 
## Executing
 
The jar is executed using `java -jar trafficlight-api.jar`. I run the application on an Raspberry PI and demonise it using `nohup`. For example:
 
    $nohup java -jar jenkins-trafficlight.jar 1>/dev/null 2>&1 &

Invoke the API (port 8080) by sending GET or POST HTTP requests with the JSON body:

    {"lights":"FLASH_ALL"}
    
The valid settings are: GREEN, AMBER, FLASHING_AMBER, RED, ALLOFF, ALLON, FLASH_ALL.