package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    SpriteBatch batch;
    Texture cyclistSideTexture;
    Texture cyclistFrontTexture;
    Texture cyclistBackTexture;
    TextureRegion currentSprite;
    Vector2 position;
    float speed = 200f;
    int direction = 0;
    float scale = 0.25f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        cyclistSideTexture = new Texture("cyclist_side.png");
        cyclistFrontTexture = new Texture("cyclist_front.png");
        cyclistBackTexture = new Texture("cyclist_back.png");
        position = new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        currentSprite = new TextureRegion(cyclistSideTexture);
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.3f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        batch.begin();
        float width = currentSprite.getRegionWidth() * scale;
        float height = currentSprite.getRegionHeight() * scale;
        if (direction == 2) {
            batch.draw(currentSprite, position.x + width, position.y, -width, height);
        } else {
            batch.draw(currentSprite, position.x, position.y, width, height);
        }
        batch.end();
    }

    void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += speed * delta;
            direction = 0;
            currentSprite.setRegion(cyclistSideTexture);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= speed * delta;
            direction = 2;
            currentSprite.setRegion(cyclistSideTexture);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += speed * delta;
            direction = 1;
            currentSprite.setRegion(cyclistBackTexture);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= speed * delta;
            direction = 3;
            currentSprite.setRegion(cyclistFrontTexture);
        }
        float width = currentSprite.getRegionWidth() * scale;
        float height = currentSprite.getRegionHeight() * scale;
        position.x = Math.max(0, Math.min(position.x, Gdx.graphics.getWidth() - width));
        position.y = Math.max(0, Math.min(position.y, Gdx.graphics.getHeight() - height));
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
    }
}
