package com.github.stadler.bomberturtle;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Level {
    List<Rectangle> walls = new ArrayList<>();
    Rectangle bombe;
    Rectangle schilki;

    public Rectangle getOtherEntity(Rectangle entity) {
        if (entity == bombe) {
            return schilki;
        } else {
            return bombe;
        }

    }
}
