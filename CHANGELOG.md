All notable developments to this project are documented here.

9/30/2026 - 11:33
- Created Initial Project using GDX-Liftoff
- Generated(Using AI) and added a pixel art background
- Used Shape Renderer to create the court elements

10/3/2026 - 16:18
- Added a static player character
    - Randomly placed on court
    - Used Shape renderer to create body shapes
- Added Changelog file

10/3/2026 - 20:00
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

10/5/2026 - 
- Made it so player x position is randomized for each shot, not just on reset
- Changed the shaperenderer order so every frame the rim is drawn after the ball so the ball goes "through"(behind) the rim
- Created simple bouncing physics for the backboard and right rim edge
- Added sources to README

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