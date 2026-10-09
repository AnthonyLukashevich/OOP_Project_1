# Lukashevich Project 1

A 2D physics-based basketball game built in Java using LibGDX.

Submission for Object Oriented Programming Quarter Project #1

All of my code is in the Main.java file

Path: core/src/main/java/Lukashevich/Project1

## Project Description

This project is a simple 2D basketball game focused on projectile physics and player input with minimal graphics.

The player selects the angle and power of a shot and launches the basketball with the goal of of getting it through the hoop. The game will use physics to calculate the ball's trajectory, including gravity and collisions/bounces.

Ensure JDK 21 is installed, then run the code by running the Lwjgl3Launcher.java in the path lwjgl3/src/java.

## Current Features

- 2D basketball court with perspective
- Pixel art background(Generated with AI)
- Backboard and rim
- LibGDX desktop application
- Player with random starting position
- Angle selection
- Shot power selection
- Basketball projectile physics
- Trajectory preview
- Simple rim/backboard hitbox
- Bouncing Physics
- Scoring System
- Game reset
- Tutorial Introduction

## Planned Features

- Windows executable

## Applications

- Java
- LibGDX
- Gradle
- LWJGL3

## Sources

- https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html
- https://javadoc.io/doc/com.badlogicgames.gdx/gdx/latest/index.html
- https://libgdx.com/wiki/start/a-simple-game
- https://libgdx.com/wiki/graphics/2d/fonts/bitmap-fonts
- https://libgdx.com/wiki/input/input-handling

# Changelog

All notable developments to this project are documented here.

## 9/30/2026 - 11:33
- Created Initial Project using GDX-Liftoff
- Generated(Using AI) and added a pixel art background
- Used Shape Renderer to create the court elements

## 10/3/2026 - 16:18
- Added a static player character
    - Randomly placed on court
    - Used Shape renderer to create body shapes
- Added Changelog file

## 10/3/2026 - 20:00
- Added a line at player waist
- Moved rim slightly right to align
- Inserted changelog into README
- Added frame update system
- Added basketball trajectory and shot mechanics
    - Used shape renderer
    - Added angle and power selection
    - Added trajectory physics using trig
    - Added trajectory prediction arc with keybind "t"
- Reworked code into more manageable, individual methods

## 10/7/2026 - 17:00
- Made it so player x position is randomized for each shot, not just on reset
- Changed the shaperenderer order so every frame the rim is drawn after the ball so the ball goes "through"(behind) the rim
- Created simple bouncing physics for the backboard and right rim edge
- Added sources to README

## 10/8/2026 - 13:40
- Made the ball bounce once and then reset on the second bounce off the floor

## 10/8/2026 - 19:50
- Added scoring
- Added game phases: tutorial, countdown, round, gameover
- Added text tutorial and messages