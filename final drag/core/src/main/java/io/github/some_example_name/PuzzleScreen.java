package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PuzzleScreen implements Screen {

    private final Game game;
    private final Stage stage;
    private final DragAndDrop dragAndDrop;

    // Asset Textures & Skin
    private final Skin skin;
    private final Texture frameTexture;
    private final Texture snippetBgTexture;
    private final Texture successTexture;
    private final Texture failureTexture;

    // UI Elements
    private final Image feedbackImage;
    private final Table dropTarget;

    public PuzzleScreen(final Game game) {
        this.game = game;
        stage = new Stage(new FitViewport(640, 960));
        Gdx.input.setInputProcessor(stage);

        // --- 1. Load Assets ---
        Skin tempSkin;
        try {
            tempSkin = new Skin(Gdx.files.internal("uiskin.json"));
        } catch (Exception e) {
            Gdx.app.error("PuzzleScreen", "Could not load skin. Please ensure all 5 skin files are in your assets folder.", e);
            tempSkin = new Skin();
        }
        skin = tempSkin;

        frameTexture = new Texture(Gdx.files.internal("FRame.png"));
        snippetBgTexture = new Texture(Gdx.files.internal("helloworldbutton.png"));
        successTexture = new Texture(Gdx.files.internal("won.png"));
        failureTexture = new Texture(Gdx.files.internal("faild.png"));

        // --- 2. Create UI Elements ---
        Image frameImage = new Image(frameTexture);
        Image snippetBgImage = new Image(snippetBgTexture);

        feedbackImage = new Image(successTexture);
        feedbackImage.setVisible(false);

        Label.LabelStyle codeStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        skin.getFont("default-font").getData().setScale(1.3f); // Adjust font scale for readability

        Label draggableLabel = new Label("System.out.println(\"Hello world\");", codeStyle);
        draggableLabel.setAlignment(Align.center);

        // --- 3. Build the FINAL Layout ---
        Stack draggableStack = new Stack();
        draggableStack.add(snippetBgImage);
        draggableStack.add(draggableLabel);

        Table contentTable = new Table();
        contentTable.setFillParent(true);

        // **FIX:** Use a more robust layout structure.
        // This empty cell takes up the top part of the screen, pushing everything down.
        contentTable.add().height(550);
        contentTable.row();

        // **FIX:** Add the button, making it larger and preserving its shape.
        contentTable.add(draggableStack).width(500).height(130);
        contentTable.row();

        // Add the feedback image below the button, with space.
        contentTable.add(feedbackImage).size(100, 100).padTop(50);
        contentTable.row();

        // Add an expanding cell to fill the remaining space at the bottom.
        contentTable.add().expandY();

        Stack finalStack = new Stack();
        finalStack.setFillParent(true);
        finalStack.add(frameImage);
        finalStack.add(contentTable);

        stage.addActor(finalStack);

        // --- 4. Drag and Drop Logic ---
        dragAndDrop = new DragAndDrop();

        // Source (the draggable button)
        dragAndDrop.addSource(new DragAndDrop.Source(draggableStack) {
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(getActor());

                Image dragActor = new Image(snippetBgTexture);
                // **FIX:** Make the dragged actor smaller
                dragActor.setSize(getActor().getWidth() * 0.8f, getActor().getHeight() * 0.8f);
                payload.setDragActor(dragActor);
                dragAndDrop.setDragActorPosition(-dragActor.getWidth() / 2, dragActor.getHeight() / 2);

                getActor().setVisible(false);
                feedbackImage.setVisible(false);
                return payload;
            }

            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target == null) {
                    getActor().setVisible(true);
                    showFeedback(false);
                }
            }
        });

        // Target (the invisible drop zone)
        dropTarget = new Table();
        stage.addActor(dropTarget);
        // To see the drop zone, uncomment the next line
        // dropTarget.setDebug(true);

        dragAndDrop.addTarget(new DragAndDrop.Target(dropTarget) {
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true;
            }

            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                showFeedback(true);
                ((Actor) payload.getObject()).remove();
            }
        });
    }

    private void showFeedback(boolean success) {
        if (success) {
            feedbackImage.setDrawable(new Image(successTexture).getDrawable());
        } else {
            feedbackImage.setDrawable(new Image(failureTexture).getDrawable());
        }
        feedbackImage.setVisible(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        // **FIX:** Position the drop zone correctly inside the main() method area
        dropTarget.setSize(350, 70);
        dropTarget.setPosition(
            stage.getWidth() / 2 - dropTarget.getWidth() / 2,
            stage.getHeight() / 2 + 50
        );
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        frameTexture.dispose();
        snippetBgTexture.dispose();
        successTexture.dispose();
        failureTexture.dispose();
    }

    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
