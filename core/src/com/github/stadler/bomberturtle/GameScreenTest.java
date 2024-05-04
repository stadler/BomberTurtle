package com.github.stadler.bomberturtle;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameScreenTest {

    @Test
    void testRotation() {
        assertEquals(45, GameScreen.calculateDegree(new Vector2(1f, 1f)));
        assertEquals(0, GameScreen.calculateDegree(new Vector2(1f, 0f)));
        assertEquals(90, GameScreen.calculateDegree(new Vector2(0f, 1f)));
    }

}