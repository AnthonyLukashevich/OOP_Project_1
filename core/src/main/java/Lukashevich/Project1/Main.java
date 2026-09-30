package Lukashevich.Project1;

//imports
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

//main class inheriting from ApplicationAdapter
public class Main extends ApplicationAdapter {

    private SpriteBatch batch; //used for drawing sprites
    private Texture background; //background image of the court
    private ShapeRenderer shapeRenderer; //used for drawing shapes like the court, backboard, rim, and support

    @Override
    public void create() {
        batch = new SpriteBatch();

        background = new Texture(Gdx.files.internal("court_background.png"));
        background.setFilter(
                Texture.TextureFilter.Nearest, 
                Texture.TextureFilter.Nearest
        );

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        batch.begin();
        batch.draw(background, 0, 0, 960, 540);
        batch.end();

        // Draw court
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); //starts new shape renderer for filled shapes
        
        // Set color for the court
        shapeRenderer.setColor(0.72f, 0.45f, 0.20f, 1);

        // Triangles to create the perspective of the court
        shapeRenderer.triangle(
                0, 0,
                960, 0,
                960, 175
        );

        shapeRenderer.triangle(
                0, 0,
                960, 175,
                190, 175
        );

        // backboard
        shapeRenderer.setColor(0.95f, 0.95f, 0.95f, 1);
        shapeRenderer.rect(201, 300, 8, 85);

        // Rim
        shapeRenderer.setColor(0.90f, 0.15f, 0.05f, 1);
        shapeRenderer.rect(208, 310, 55, 7);

        // Ceiling support
        shapeRenderer.setColor(0.30f, 0.30f, 0.30f, 1);
        shapeRenderer.rect(166, 350, 12, 190);

        // Support bends toward the backboard
        shapeRenderer.rect(166, 340, 35, 12);

        shapeRenderer.end();

        // Court lines and outlines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line); //starts new shape renderer for lines

        shapeRenderer.setColor(1, 1, 1, 1);

        // left edge of court
        shapeRenderer.line(0, 0, 190, 175);

        // Back edge of court
        shapeRenderer.line(190, 175, 960, 175);

        // Center line
        shapeRenderer.line(700, 0, 700, 175);

        // Center ellipse(for perspective)
        shapeRenderer.ellipse(570, 65, 260, 70);

        // Rim outline
        shapeRenderer.rect(209, 310, 55, 7);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        shapeRenderer.dispose();
    }
}