package com.github.stadler.bomberturtle;

import java.util.ArrayList;
import java.util.List;

public class Level {
    List<Entity> walls = new ArrayList<>();
    List<PlayerEntity> players = new ArrayList<>();
    List<MovableEntity> enemies = new ArrayList<>();
    List<Entity> miniBombs = new ArrayList<>();

}
