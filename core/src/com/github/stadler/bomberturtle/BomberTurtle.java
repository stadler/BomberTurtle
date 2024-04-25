package com.github.stadler.bomberturtle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class BomberTurtle extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;
	private Rectangle bomb;
	private OrthographicCamera camera;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("Bombe.png");

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);

		// create a Rectangle to logically represent the bucket
		bomb = new Rectangle();
		bomb.x = (800f / 2f) - (275f / 2f); // center the bucket horizontally
		bomb.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
		bomb.width = 275;
		bomb.height = 329;
	}

	@Override
	public void render () {
		// clear the screen with a dark blue color. The arguments to clear are the red, green
		// blue and alpha component in the range [0,1] of the color to be used to clear the screen.
		ScreenUtils.clear(1, 1, 1, 1);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, bomb.x, bomb.y);
		batch.end();

		handleInputs();
	}

	private void handleInputs() {
		// process user input
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			bomb.x -= 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			bomb.x += 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			bomb.y -= 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			bomb.y += 200 * Gdx.graphics.getDeltaTime();
		}

		// make sure the bucket stays within the screen bounds
		if(bomb.x < 0) bomb.x = 0;
		if(bomb.x > 800 - bomb.width) bomb.x = 800 - bomb.width;
		if(bomb.y < 0) bomb.y = 0;
		if(bomb.y > 600 - bomb.height) bomb.y = 600 - bomb.height;
	}

	@Override
	public void dispose () {
		// dispose of all the native resources
		batch.dispose();
		img.dispose();
	}
}
