package com.quizgame.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.quizgame.QuizGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Java Quiz Game");
        config.setWindowedMode(800, 600);
        config.setResizable(true);
        new Lwjgl3Application(new QuizGame(), config);
    }
}
