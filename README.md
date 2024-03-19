# Luminar talk ⚡
## Overview
This project aims to create a real time morse code translator, detecting the light emisions from the camera, translate them to text. Also provides a way to encode text into light using the flashlight.

For the moment, the project is still under construction.

## Features
- Translate morse to text throught the camera ✅
- Translate text to light using the torch flashlight (in progress 🚧)
- Settings to adjust ligh detection (in progress 🚧)
- Settings to adjust duration of ``dot`` (in progress 🚧)

https://github.com/emenjivar/luminar-talk/assets/19592284/a500ee92-f86d-42df-bae5-8ca66a39aff1

## Usage
This project follows the [standar](https://en.wikipedia.org/wiki/Morse_code) morse rules:
- there's only two symbols: ``dash`` and ``dot``.
- ``dot`` is (at the moment) 1 second, ``dash`` is the equivalent to 3 ``dots``.
- every morse character is separacted by 1 space equivalent to 1 ``dot``
- Every new letter is separacted by 3 spaces, equivalent to 3 ``dots``
- Every word is separated by 7 spaces, equivalent to ``7`` dots.

Open the app and point the camera to the source of light.
If the light is not detected very well, you could enable ``debug`` mode and verify the ligh is surrounded by a green circle.

[Here](https://en.wikipedia.org/wiki/Morse_code#/media/File:International_Morse_Code.svg) is the standar table of characters used for this project.

## Developments
### Tools
- Jetpack compose 1.6.2
- Material3
- CameraX 1.2.3
- OpenCV 4.8.0

### Pre-requisits
- Android studio
- Openjdk 17.0.9
