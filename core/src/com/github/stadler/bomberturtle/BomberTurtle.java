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
	private Texture bombeImg;
	private Texture schilkiImg;
	private Rectangle bomb;
	private Rectangle schilki;
	private OrthographicCamera camera;

	@Override
	public void create () {
		batch = new SpriteBatch();
		bombeImg = new Texture("Bombe.png");
		schilkiImg = new Texture("Schilki.png");

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);

		// create a Rectangle to logically represent the bucket
		schilki = new Rectangle();
		schilki.width = 120;
		schilki.height = 120;
		schilki.x = 20;
		schilki.y = 20;

		bomb = new Rectangle();
		bomb.width = 120;
		bomb.height = 120;
		bomb.x = camera.viewportWidth - 20 - (bomb.width / 2f);
		bomb.y = 20;
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
		batch.draw(bombeImg, bomb.x, bomb.y);
		batch.draw(schilkiImg, schilki.x, schilki.y);
		batch.end();

		handleInputs();
	}

	private void handleInputs() {
		// process user input
		handleDirectionsForEntity(bomb, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.DOWN, Input.Keys.UP);
		handleDirectionsForEntity(schilki, Input.Keys.A, Input.Keys.D, Input.Keys.S, Input.Keys.W);
	}

	private void handleDirectionsForEntity(Rectangle entity, int left, int right, int down, int up) {
		if (Gdx.input.isKeyPressed(left)) {
			entity.x -= 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(right)) {
			entity.x += 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(down)) {
			entity.y -= 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(up)) {
			entity.y += 200 * Gdx.graphics.getDeltaTime();
		}

		// make sure the bucket stays within the screen bounds
		if(entity.x < 0) entity.x = 0;
		if(entity.x > camera.viewportWidth - entity.width) entity.x = camera.viewportWidth - entity.width;
		if(entity.y < 0) entity.y = 0;
		if(entity.y > camera.viewportHeight - entity.height) entity.y = camera.viewportHeight - entity.height;
	}

	@Override
	public void dispose () {
		// dispose of all the native resources
		batch.dispose();
		bombeImg.dispose();
		schilkiImg.dispose();
	}
}
