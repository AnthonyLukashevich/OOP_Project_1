package Lukashevich.Project1;

import com.badlogic.gdx.ApplicationAdapter; // base class, empty create/render/dispose to override
import com.badlogic.gdx.Gdx; // global access to input, graphics, files, gl
import com.badlogic.gdx.Input; // key constants like Input.Keys.T
import com.badlogic.gdx.graphics.GL20; // opengl constants, used for clearing screen
import com.badlogic.gdx.graphics.Texture; // image stored on the gpu
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // draws textures
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; // draws shapes and lines
import com.badlogic.gdx.math.MathUtils; // sin, cos, random helpers

public class Main extends ApplicationAdapter {

    // drawing tools
    private SpriteBatch batch; // draws the background
    private Texture background; // gym image
    private ShapeRenderer shapeRenderer; // draws court, hoop, player, ball, arrow

    // player
    private float playerX; // center x
    private float playerY; // floor level since game is side view 2d

    // ball
    private float ballX; // center x
    private float ballY; // center y
    private static final float BALL_RADIUS = 10; // KEEP RADIUS AT 10, things will break otherwise
    private int numberOfFloorBounces = 0; // how many times the ball has bounced off the floor

    // state of the shot
    private enum ShotState { AIMING_ANGLE, AIMING_POWER, FLYING } // the 3 states
    private ShotState state; // which state is currently active

    private float angle; // degrees, 0 to 90
    private float angleDir; // 1 going up, -1 going down
    private float power; // 0 to 1
    private float powerDir; // 1 going up, -1 going down

    private float velX; // pixels per second, negative = left
    private float velY; // pixels per second, positive = up

    // tuning, allows you to change speeds of game elements
    private static final float GRAVITY = -900f; // pixels per second squared, negative is down
    private static final float ANGLE_SPEED = 90f; // degrees per second the angle swings
    private static final float POWER_SPEED = 1.2f; // power per second the power swings
    private static final float MIN_SPEED = 300f; // launch speed at power 0
    private static final float MAX_SPEED = 1000f; // launch speed at power 1
    private static final float ARROW_MIN = 40f; // arrow length at power 0
    private static final float ARROW_MAX = 140f; // arrow length at power 1

    private boolean showPath = false; // path preview on or off

    // setup
    @Override
    public void create() {
        batch = new SpriteBatch(); // make the image drawer

        background = new Texture(Gdx.files.internal("court_background.png")); // load from assets folder
        background.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // no smoothing, keeps pixel art "sharp"

        shapeRenderer = new ShapeRenderer(); // make the shape drawer

        playerX = MathUtils.random(450, 800); // random spot on the court
        playerY = 100; // floor height

        resetShot(); // set up the first shot
    }

    // new shot, ball to hand, values set to start
    private void resetShot() {
        playerX = MathUtils.random(450, 800); // reset position on the court
        ballX = playerX - 30; // player right hand
        ballY = playerY + 40; // hand height
        angle = 0; // start at lowest angle
        angleDir = 1; // swing up first
        power = 0; // start at lowest power
        powerDir = 1; // swing up first
        velX = 0; // reset/set vel to 0
        velY = 0; // reset/set vel to 0
        state = ShotState.AIMING_ANGLE; // set to first state
        numberOfFloorBounces = 0;
    }

    // power coeff converted to launch speed
    private float getLaunchSpeed() {
        return MIN_SPEED + power * (MAX_SPEED - MIN_SPEED); //takes coeff times range and adds min to get speed
    }

    // called by render() every frame, delta is seconds since last frame
    private void update(float delta) {
        // toggle path preview
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            showPath = !showPath; // flip on/off
        }

        if (state == ShotState.AIMING_ANGLE) {
            angle += angleDir * ANGLE_SPEED * delta; // calculate new angle
            if (angle >= 90) { angle = 90; angleDir = -1; } // hit top, go back down
            if (angle <= 0)  { angle = 0;  angleDir = 1; } // hit bottom, go back up

            // get click input to lock angle and move to next state
            if (Gdx.input.justTouched()) {
                state = ShotState.AIMING_POWER;
            }

        } else if (state == ShotState.AIMING_POWER) {
            power += powerDir * POWER_SPEED * delta; // calculate power for the frame
            if (power >= 1) { power = 1; powerDir = -1; } // hit max, go back down
            if (power <= 0) { power = 0; powerDir = 1; } // hit min, go back up

            if (Gdx.input.justTouched()) {
                float speed = getLaunchSpeed(); // total launch speed from power
                velX = -speed * MathUtils.cosDeg(angle); // horizontal part, negative = left toward hoop
                velY = speed * MathUtils.sinDeg(angle); // vertical part, positive = up
                state = ShotState.FLYING; // shot is live
            }

        } else if (state == ShotState.FLYING) {
            velY += GRAVITY * delta; // gravity pulls velY down, velX untouched
            ballX += velX * delta; // move by velocity * time
            ballY += velY * delta; 
            
            bounceOffBackboard();
            bounceOfRim();
            bounceOffFloor();

            // ball resets when it hits the floor
            if (ballY < playerY) {
                resetShot();
            }
        }
    }

    // main loop, called every frame
    @Override
    public void render() {
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f); // seconds since last frame, capped so lag can't teleport the ball
        update(delta); // change the numbers first, then draw them

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1); // pick near-black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // wipe last frame

        batch.begin(); // start image drawing
        batch.draw(background, 0, 0, 960, 540); // fills the window with the background asset
        batch.end(); // finish image drawing

        // draw order is important for layering
        drawCourt();
        drawPlayer();
        drawAiming();
    }

    // floor, hoop, lines
    private void drawCourt() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // filled in shapes

        shapeRenderer.setColor(0.72f, 0.45f, 0.20f, 1); // color of the floor
        //triangles create the court
        shapeRenderer.triangle(0, 0, 960, 0, 960, 175); 
        shapeRenderer.triangle(0, 0, 960, 175, 190, 175); 

        shapeRenderer.setColor(0.30f, 0.30f, 0.30f, 1); // dark gray
        shapeRenderer.rect(166, 350, 12, 190); // vertical pole from ceiling
        shapeRenderer.rect(166, 340, 35, 12); // bend to backboard
        shapeRenderer.setColor(0.95f, 0.95f, 0.95f, 1); // near white
        shapeRenderer.rect(201, 300, 8, 85); // backboard

        shapeRenderer.end(); // done with solid shapes for court

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line); // outline shapes
        shapeRenderer.setColor(1, 1, 1, 1); // white

        shapeRenderer.line(0, 0, 190, 175); // left edge of court
        shapeRenderer.line(190, 175, 960, 175); // back edge
        shapeRenderer.line(700, 0, 700, 175); // center line
        shapeRenderer.ellipse(570, 65, 260, 70); // center ellipse for perspective

        shapeRenderer.end();
    }

    // player drawn after court so it is on top
    private void drawPlayer() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.85f, 0.65f, 0.45f, 1); // skin color
        shapeRenderer.circle(playerX, playerY + 90, 12); // head

        shapeRenderer.setColor(0.1f, 0.2f, 0.8f, 1); // blue jersey
        shapeRenderer.rect(playerX - 12, playerY + 30, 24, 50); // body
        shapeRenderer.rect(playerX - 25, playerY + 35, 13, 45); // left arm
        shapeRenderer.rect(playerX + 12, playerY + 35, 13, 45); // right arm

        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1); // dark shorts
        shapeRenderer.rect(playerX - 15, playerY, 30, 40); // legs

        shapeRenderer.setColor(0.95f, 0.45f, 0.05f, 1); // orange
        shapeRenderer.circle(ballX, ballY, BALL_RADIUS); // ball

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1); // white detail lines

        shapeRenderer.line(playerX, playerY, playerX, playerY + 40); // splits the leg rectangle into two legs
        shapeRenderer.line(playerX - 15, playerY + 35, playerX - 15, playerY + 70); // left arm seperate from body
        shapeRenderer.line(playerX + 15, playerY + 35, playerX + 15, playerY + 70); // right arm seperate from body
        shapeRenderer.line(playerX - 15, playerY + 40, playerX + 15, playerY + 40); // waist

        shapeRenderer.setColor(0, 0, 0, 1); // black for ball details
        shapeRenderer.circle(ballX, ballY, BALL_RADIUS); // ball outline
        shapeRenderer.line(ballX - BALL_RADIUS, ballY, ballX + BALL_RADIUS, ballY); // horizontal seam
        shapeRenderer.line(ballX, ballY - BALL_RADIUS, ballX, ballY + BALL_RADIUS); // vertical seam

        float edgeX = BALL_RADIUS * 0.6f; // curve endpoints, sideways from center
        float edgeY = BALL_RADIUS * 0.8f; // curve endpoints, up and down from center
        float bendY = BALL_RADIUS * 0.4f; // control point height

        shapeRenderer.curve(ballX - edgeX, ballY + edgeY, ballX, ballY + bendY, ballX, ballY - bendY, ballX - edgeX, ballY - edgeY, 10); // left seam
        shapeRenderer.curve(ballX + edgeX, ballY + edgeY, ballX, ballY + bendY, ballX, ballY - bendY, ballX + edgeX, ballY - edgeY, 10); // right seam(left but mirrored)

        shapeRenderer.end();  

        //draw the rim so it is infront of the ball so the ball goes "through" the rim
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.90f, 0.15f, 0.05f, 1); // red
        shapeRenderer.rect(209, 310, 55, 7); // rim

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rect(209, 310, 55, 7); // rim outline

        shapeRenderer.end();
    }

    // arrow and path, nothing while ball is flying
    private void drawAiming() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // filled so arrow and dots are thick

        if (state == ShotState.AIMING_ANGLE) {
            drawArrow(80, 1, 1, 0); // fixed length and yellow
        } else if (state == ShotState.AIMING_POWER) {
            if (showPath) {
                drawPath(); // path first so arrow is on top
            }
            // length and color both show power, green low, red high
            drawArrow(ARROW_MIN + power * (ARROW_MAX - ARROW_MIN), power, 1 - power, 0);
        }

        shapeRenderer.end();
    }

    // arrow from ball at current angle
    private void drawArrow(float length, float r, float g, float b) {
        float arrowDeg = 180 - angle; // aim angle to screen angle, 180 is left, 90 is up
        float dirX = MathUtils.cosDeg(arrowDeg); // unit direction x (length 1)
        float dirY = MathUtils.sinDeg(arrowDeg); // unit direction y
        float tipX = ballX + length * dirX; // sharp point, ball + length along direction
        float tipY = ballY + length * dirY;
        float baseX = tipX - 18 * dirX; // 18 px back from tip, shaft ends and head starts
        float baseY = tipY - 18 * dirY;

        shapeRenderer.setColor(0, 0, 0, 1); // black
        shapeRenderer.rectLine(ballX, ballY, baseX, baseY, 8); // outline
        shapeRenderer.setColor(r, g, b, 1); // arrow color
        shapeRenderer.rectLine(ballX, ballY, baseX, baseY, 4); // fill in

        // arrow tip, triangle
        shapeRenderer.triangle(
                tipX, tipY,
                baseX - dirY * 9, baseY + dirX * 9,
                baseX + dirY * 9, baseY - dirX * 9
        );
    }

    // dotted path, same math as flight, future prediction
    private void drawPath() {
        float speed = getLaunchSpeed(); // speed the shot would get if clicked right now
        float pathX = ballX; // path starts at the ball
        float pathY = ballY; // path starts at the ball
        float pathVelX = -speed * MathUtils.cosDeg(angle); // same launch math as update()
        float pathVelY = speed * MathUtils.sinDeg(angle);
        float step = 1 / 60f; // seconds per step in the path

        shapeRenderer.setColor(1, 1, 1, 1); // white dots
        for (int i = 0; i < 180; i++) { // 180 steps = 3 seconds max
            pathVelY += GRAVITY * step; // same gravity as flight
            pathX += pathVelX * step; // same movement as flight
            pathY += pathVelY * step; 

            if (pathY < playerY || pathX < 0) break; // stop at floor or left edge
                shapeRenderer.circle(pathX, pathY, 2); // one dot, radius 2
        }
    }

// bounce off the right side of the backboard
    private void bounceOffBackboard() {
        if (ballY < 290 || ballY > 395) return; // does not hit the backboard because its too high or low
        if (ballX < 191) return; // already behind the board
        if (ballX > 219) return; // left edge of ball hasnt reached the board yet
        if (velX >= 0) return; // already movving right, ignore it

        ballX = 209 + BALL_RADIUS; //center of the ball could overlap because of frames, so push it out to the right
        velX = -velX * 0.8f; // flip left to right, 80% of the speed
        velY = velY * 0.8f; // same vertical direction, 80% of the speed
    }

// bounce off the right edge of the rim
private void bounceOfRim() {
    if (ballY < 300 || ballY > 327) return; // too high or low
    if (ballX < 254 || ballX > 274) return; // too far left or right of the edge

    if (ballX >= 264 && velX < 0) { // on the right side, moving left
        ballX = 264 + BALL_RADIUS; // push out to the right
        velX = -velX * 0.8f; // flip and 80%
        velY = velY * 0.8f; // same vert velocity, 80% of the speed
    } else if (ballX < 264 && velX > 0) { // on the left side, moving right
        ballX = 264 - BALL_RADIUS; // push out to the left
        velX = -velX * 0.8f; // flip to the left, 80%
        velY = velY * 0.8f; // same y vel, 80%
    }
}

private void bounceOffFloor() {
    if (ballY < playerY) { // at or below floor level
        ballY = playerY + BALL_RADIUS; // push it up to the floor
        velY = -velY * 0.7f; // flip to up, 70%
        velX = velX * 0.8f; // same x direction, 80%
        numberOfFloorBounces+= 1; //count the number of bounces off the floower
        if (numberOfFloorBounces == 2) resetShot(); // velocity is small
    }
    else return; // not below the floor, ignore it
}

    // free memory, called this on close
    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        shapeRenderer.dispose();
    }
}