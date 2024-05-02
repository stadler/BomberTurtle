package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class BomberTurtleGame extends Game {
    SpriteBatch batch;
    BitmapFont titleFont;
    BitmapFont textFont;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        batch = new SpriteBatch();

        titleFont = generateFont("fonts/Silkscreen/Silkscreen-Bold.ttf", 30);
        textFont = generateFont("fonts/Jersey_15_Charted/Jersey15Charted-Regular.ttf", 20);

        this.setScreen(new MainMenuScreen(this));
    }

    private BitmapFont generateFont(String path, int size) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = size;
        return fontGenerator.generateFont(fontParameter);
    }

    @Override
    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        textFont.dispose();
    }
}
