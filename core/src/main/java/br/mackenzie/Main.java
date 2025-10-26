package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Main implements ApplicationListener {
    SpriteBatch batch;
    OrthographicCamera camera;

    // Character textures
    Texture cyclistSideTexture;
    Texture cyclistFrontTexture;
    Texture cyclistBackTexture;
    TextureRegion currentSprite;

    // Background
    Texture backgroundTexture;

    // Player position in world coordinates
    Vector2 worldPosition;
    float speed = 200f;
    int direction = 0;
    float scale = 0.25f;

    // World boundaries
    float worldWidth = 3200f;
    float worldHeight = 2400f;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Setup camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Load character textures
        cyclistSideTexture = new Texture("cyclist_side.png");
        cyclistFrontTexture = new Texture("cyclist_front.png");
        cyclistBackTexture = new Texture("cyclist_back.png");
        currentSprite = new TextureRegion(cyclistSideTexture);

        backgroundTexture = createGrassTexture();
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        worldPosition = new Vector2(worldWidth / 2f, worldHeight / 2f);
    }

    private Texture createGrassTexture() {
        return new Texture("tall_grass.png");
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        handleInput(delta);

        updateCamera();

        Gdx.gl.glClearColor(0.2f, 0.3f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        drawTiledBackground();

        drawPlayer();

        batch.end();
    }

    private void drawTiledBackground() {
        float camX = camera.position.x - camera.viewportWidth / 2;
        float camY = camera.position.y - camera.viewportHeight / 2;

        int tileSize = 64;
        int tilesX = (int)(camera.viewportWidth / tileSize) + 2;
        int tilesY = (int)(camera.viewportHeight / tileSize) + 2;

        int startX = (int)(camX / tileSize) * tileSize;
        int startY = (int)(camY / tileSize) * tileSize;

        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                float drawX = startX + x * tileSize;
                float drawY = startY + y * tileSize;


                if (drawX >= 0 && drawX < worldWidth && drawY >= 0 && drawY < worldHeight) {
                    batch.draw(backgroundTexture, drawX, drawY, tileSize, tileSize);
                }
            }
        }
    }

    private void drawPlayer() {
        float width = currentSprite.getRegionWidth() * scale;
        float height = currentSprite.getRegionHeight() * scale;

        if (direction == 2) {
            batch.draw(currentSprite, worldPosition.x + width, worldPosition.y, -width, height);
        } else {
            batch.draw(currentSprite, worldPosition.x, worldPosition.y, width, height);
        }
    }

    private void updateCamera() {

        float targetX = worldPosition.x + (currentSprite.getRegionWidth() * scale) / 2;
        float targetY = worldPosition.y + (currentSprite.getRegionHeight() * scale) / 2;

        float halfWidth = camera.viewportWidth / 2;
        float halfHeight = camera.viewportHeight / 2;

        targetX = Math.max(halfWidth, Math.min(targetX, worldWidth - halfWidth));
        targetY = Math.max(halfHeight, Math.min(targetY, worldHeight - halfHeight));

        camera.position.set(targetX, targetY, 0);
    }

    void handleInput(float delta) {
        Vector2 movement = new Vector2(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.x += speed * delta;
            direction = 0;
            currentSprite.setRegion(cyclistSideTexture);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.x -= speed * delta;
            direction = 2;
            currentSprite.setRegion(cyclistSideTexture);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.y += speed * delta;
            direction = 1;
            currentSprite.setRegion(cyclistBackTexture);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.y -= speed * delta;
            direction = 3;
            currentSprite.setRegion(cyclistFrontTexture);
        }

        worldPosition.add(movement);

        float width = currentSprite.getRegionWidth() * scale;
        float height = currentSprite.getRegionHeight() * scale;
        worldPosition.x = Math.max(0, Math.min(worldPosition.x, worldWidth - width));
        worldPosition.y = Math.max(0, Math.min(worldPosition.y, worldHeight - height));
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        cyclistSideTexture.dispose();
        cyclistFrontTexture.dispose();
        cyclistBackTexture.dispose();
        backgroundTexture.dispose();
    }
}
