package com.quizgame;

import com.badlogic.gdx.Game;
import com.quizgame.screens.QuizScreen;

public class QuizGame extends Game {
    @Override
    public void create() {
        setScreen(new QuizScreen(this));
    }
}
