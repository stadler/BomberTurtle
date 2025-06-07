package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

import java.time.LocalDateTime;

import static com.badlogic.gdx.Input.Keys.*;

public class MainMenuScreen implements Screen {

    private final BomberTurtleGame game;
    private final OrthographicCamera camera;
    private LocalDateTime showTime;

    public MainMenuScreen(BomberTurtleGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
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
        game.titleFont.draw(game.batch, "Willkommen bei Bomber Turtle ", 50, 500);

        game.batch.draw(game.enemyTexture, 200, 300, 100, 100);
        game.batch.draw(game.playerTextures.get(0), 600 - 60, 300, 100, 100);
        for (int currentX = 0; currentX < camera.viewportWidth; currentX += 50) {
            game.batch.draw(game.wallTexture, currentX, 250, 50, 50);
        }

        game.textFont.draw(game.batch, isPlayerSelected(1) + " 1 Spieler", 350, 200);
        game.textFont.draw(game.batch, isPlayerSelected(2) + " 2 Spieler", 350, 150);
        game.textFont.draw(game.batch, isPlayerSelected(3) + " 3 Spieler", 350, 100);
        game.batch.end();

        if (Gdx.input.isKeyPressed(ESCAPE)) {
            dispose();
            System.exit(0);
        }
        if (Gdx.input.isKeyJustPressed(UP)) {
            game.setSelectedPlayers(((Math.max(0, (game.getSelectedPlayers() - 1) - 1)) % 3) + 1);
        }
        if (Gdx.input.isKeyJustPressed(DOWN)) {
            game.setSelectedPlayers(((Math.min(2, (game.getSelectedPlayers() - 1) + 1)) % 3) + 1);
        }
        if (showTime.plusSeconds(1L).isBefore(LocalDateTime.now())) {
            if (Gdx.input.isKeyPressed(ENTER)) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        }
    }

    private String isPlayerSelected(int player) {
        if (player == (game.getSelectedPlayers())) {
            return "->";
        }
        return "  ";
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
