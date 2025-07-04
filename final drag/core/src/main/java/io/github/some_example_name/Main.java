package io.github.some_example_name;

import com.badlogic.gdx.Game;

public class Main extends Game {

    @Override
    public void create() {
        // Start the game by showing the PuzzleScreen
        setScreen(new PuzzleScreen(this));
    }
}
