package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

import java.time.LocalDateTime;

import static com.badlogic.gdx.Input.Keys.ANY_KEY;

public class MainMenuScreen implements Screen {

    private final BomberTurtleGame game;
    private final OrthographicCamera camera;
    private final Texture bombeImg;
    private final Texture schilkiImg;

    private LocalDateTime showTime;

    public MainMenuScreen(BomberTurtleGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        bombeImg = new Texture("Bombe.png");
        schilkiImg = new Texture("Schilki.png");
    }

    @Override
    public void show() {
        showTime = LocalDateTime.now();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.7f, 0.8f, 0.9f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.titleFont.draw(game.batch, "Willkommen bei Bomber Turtle!!! ", 50, 200);
        game.textFont.draw(game.batch, "Drücke eine beliebige Taste zum starten!", 50, 100);
        game.batch.draw(schilkiImg, 200, 300);
        game.batch.draw(bombeImg, 600-60, 300);
        game.batch.end();

        if ((Gdx.input.isTouched() || Gdx.input.isKeyPressed(ANY_KEY))
            && showTime.plusSeconds(1L).isBefore(LocalDateTime.now())) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
