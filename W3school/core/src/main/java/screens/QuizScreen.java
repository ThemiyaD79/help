package com.quizgame.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quizgame.data.Question;

public class QuizScreen extends ScreenAdapter {
    private Stage stage;
    private Skin skin;
    private Game game;

    private Texture backgroundTexture;
    private Image backgroundImage;

    private Label questionLabel;
    private ButtonGroup<CheckBox> answerGroup;
    private CheckBox[] answerButtons;
    private TextButton submitButton;
    private Label feedbackLabel;

    private Question[] questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    public QuizScreen(Game game) {
        this.game = game;

        // Initialize questions
        questions = new Question[] {
            new Question(
                "Which method is often used to print text in Java?",
                new String[]{"println()", "printline()", "printtext()", "echo()"},
                0
            ),
            new Question(
                "What is the correct syntax for a Java main method?",
                new String[]{
                    "public static void main(String[] args)",
                    "public void main(String[] args)",
                    "static void main(String[] args)",
                    "public main(String[] args)"
                },
                0
            ),
            new Question(
                "Which keyword is used to create a class in Java?",
                new String[]{"class", "Class", "new", "create"},
                0
            )
        };
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load assets
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        setupUI();
        loadQuestion();
    }

    private void setupUI() {
        // Background
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Main table for layout
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Question label
        questionLabel = new Label("", skin, "default");
        questionLabel.setWrap(true);
        questionLabel.setColor(Color.WHITE);
        mainTable.add(questionLabel).width(600).padTop(50).padBottom(30).row();

        // Answer buttons
        answerGroup = new ButtonGroup<>();
        answerButtons = new CheckBox[4];

        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new CheckBox("", skin);
            answerButtons[i].getLabel().setColor(Color.WHITE);
            answerGroup.add(answerButtons[i]);
            mainTable.add(answerButtons[i]).width(400).pad(10).left().row();
        }

        // Submit button
        submitButton = new TextButton("Submit Answer", skin);
        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                checkAnswer();
            }
        });
        mainTable.add(submitButton).width(200).pad(20).row();

        // Feedback label
        feedbackLabel = new Label("", skin, "default");
        feedbackLabel.setColor(Color.YELLOW);
        mainTable.add(feedbackLabel).pad(20).row();

        // Next/Finish button (initially hidden)
        TextButton nextButton = new TextButton("Next Question", skin);
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                nextQuestion();
            }
        });
        nextButton.setVisible(false);
        mainTable.add(nextButton).width(200).pad(10);
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questions.length) {
            Question question = questions[currentQuestionIndex];
            questionLabel.setText(question.getQuestionText());

            String[] options = question.getOptions();
            for (int i = 0; i < answerButtons.length; i++) {
                answerButtons[i].setText(options[i]);
                answerButtons[i].setChecked(false);
            }

            feedbackLabel.setText("");
            submitButton.setVisible(true);
        } else {
            // Quiz finished
            questionLabel.setText("Quiz Complete!");
            feedbackLabel.setText("Your final score: " + score + "/" + questions.length);
            submitButton.setVisible(false);

            for (CheckBox button : answerButtons) {
                button.setVisible(false);
            }
        }
    }

    private void checkAnswer() {
        int selectedIndex = answerGroup.getCheckedIndex();
        if (selectedIndex == -1) {
            feedbackLabel.setText("Please select an answer!");
            return;
        }

        Question currentQuestion = questions[currentQuestionIndex];
        if (selectedIndex == currentQuestion.getCorrectAnswerIndex()) {
            feedbackLabel.setText("Correct! ✓");
            feedbackLabel.setColor(Color.GREEN);
            score++;
        } else {
            feedbackLabel.setText("Wrong! The correct answer is: " +
                currentQuestion.getOptions()[currentQuestion.getCorrectAnswerIndex()]);
            feedbackLabel.setColor(Color.RED);
        }

        submitButton.setVisible(false);

        // Show next button after 2 seconds
        Gdx.app.postRunnable(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            nextQuestion();
        });
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        loadQuestion();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        skin.dispose();
    }
}
