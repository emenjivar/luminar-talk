# Luminar talk ⚡️
This project is a Morse translator built in android to detect the light emissions from the camera, decode them to Morse and them to text. 

## Introduction

Luminar talk follows the [international standards](https://en.wikipedia.org/wiki/Morse_code) for Morse code, defined the following rules:

- Only two available characters: **dots** and **dashes**
- The unit of time is 1 second
- The time for dots is 1 unit
- The time for dashes are 3 units
- The Morse characters are separated by 1 unit
- The characters are separated by 3 units
- The words are separated by 7 units

For reference, you can consult the [list](https://en.wikipedia.org/wiki/Morse_code#/media/File:International_Morse_Code.svg) of characters and their respective equivalence in Morse.


https://github.com/emenjivar/luminar-talk/assets/19592284/a500ee92-f86d-42df-bae5-8ca66a39aff1


## Tech stack

This is a native android app build with the following technologies:

- Jetpack compose
- Material3
- CameraX 1.2.3
- OpenCV 4.8.0

## Installation

To install Luminar talk:

1. Download the latest version in the [releases page](https://github.com/emenjivar/luminar-talk/releases/latest)
2. Enable installation for unknown sources in your device
3. Install the APK 

## Roadmap

1. Develop a feature to translate text into light using the device's torch flashlight.
2. Implement automatic detection of the duration of the dits and dashes in Morse code.
3. Release the application on Google Play Store.

## Feedback ans issues

Did you find some issues or have suggestions for improvement, we welcome your input.

Feel free to create an [issue](https://github.com/emenjivar/luminar-talk/issues/new) detailing your problem or proposal.
