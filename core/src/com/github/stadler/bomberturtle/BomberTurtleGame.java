package com.github.stadler.bomberturtle;

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
        batch = new SpriteBatch();
        FreeTypeFontGenerator titleFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen/Silkscreen-Bold.ttf"));
        FreeTypeFontGenerator textFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Jersey_15_Charted/Jersey15Charted-Regular.ttf"));
        FreeTypeFontParameter bigFont = new FreeTypeFontParameter();
        bigFont.size = 30;
        FreeTypeFontParameter smallFont = new FreeTypeFontParameter();
        smallFont.size = 20;
        titleFont = titleFontGenerator.generateFont(bigFont);
        textFont = textFontGenerator.generateFont(smallFont);
        this.setScreen(new MainMenuScreen(this));
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
