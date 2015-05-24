package com.ragego.gui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ragego.gui.RageGoAssetManager;
import com.ragego.gui.RageGoGame;
import com.ragego.gui.elements.HexagonalButton;
import com.ragego.gui.elements.HexagonalMenu;

import java.util.HashMap;

/**
 * Manages the display of the Main Menu Screen.
 */
public class MenuScreen extends ScreenAdapter{
    private static final String TAG = "MenuScreen";
    private static final int BUTTONS_NB = 7;
    private HashMap<Integer, Button> buttons = new HashMap<Integer, Button>(BUTTONS_NB);
    private OrthographicCamera backCamera, hudCamera;
    private ScreenViewport hudViewport;
    private FillViewport backViewport;
    private Stage backStage, hudStage;
    private HexagonalMenu menu;
    private RageGoAssetManager manager;
    private Skin menuSkin;
    private ScreenAdapter nextScreen = null;
    private Texture backTex;
    private Image backGroundImg;

    public MenuScreen() {
        super();
    }

    @Override
    public void show () {
        backCamera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();
        backViewport = new FillViewport(2048, 1380, backCamera);
        hudViewport = new ScreenViewport(hudCamera);

        backStage = new Stage(backViewport);
        hudStage = new Stage(hudViewport);
        //hudStage.setDebugAll(true);
        Gdx.input.setInputProcessor(hudStage);

        manager = RageGoGame.getAssetManager();
        manager.load("com/ragego/gui/menu/menu.json", Skin.class);
        manager.load("com/ragego/gui/splash/island_background.png", Texture.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Assets loaded");
        menuSkin = manager.get("com/ragego/gui/menu/menu.json");

        backTex = manager.get("com/ragego/gui/splash/island_background.png");
        backTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backGroundImg = new Image(backTex);
        backStage.addActor(backGroundImg);

        menu = new HexagonalMenu(menuSkin, "menu_frame");
        menu.setPosition((hudViewport.getScreenWidth() - menu.getWidth()) * 0.5f,
                (hudViewport.getScreenHeight() - menu.getHeight()) * 0.5f);

        // Play Button
        final HexagonalButton playButton = new HexagonalButton(menu, HexagonalMenu.Position.CENTER, menuSkin, "play");
        buttons.put(0, playButton);
        playButton.setDisabled(true);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isAButtonChecked())
                    play();
            }
        });

        //Return Button
        final HexagonalButton returnButton = new HexagonalButton(menu, HexagonalMenu.Position.BOTTOM, menuSkin, "return");
        buttons.put(1, returnButton);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                uncheckOtherButtons(1);
                buttons.get(0).setDisabled(true);
                Gdx.app.exit();
            }
        });

        //Solo Button
        final HexagonalButton soloButton = new HexagonalButton(menu, HexagonalMenu.Position.TOP, menuSkin, "solo");
        buttons.put(2, soloButton);
        soloButton.setDisabled(true);
        soloButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (soloButton.isChecked()) {
                    uncheckOtherButtons(2);
                    buttons.get(0).setDisabled(false);
                    Gdx.app.log(TAG, "Solo Button clicked");
                } else
                    buttons.get(0).setDisabled(true);
            }
        });

        //Online Button
        final HexagonalButton onlineButton = new HexagonalButton(menu, HexagonalMenu.Position.RIGHT_TOP, menuSkin, "online");
        buttons.put(3, onlineButton);
        onlineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onlineButton.isChecked()) {
                    uncheckOtherButtons(3);
                    buttons.get(0).setDisabled(false);
                    nextScreen = new OnlineScreen();
                } else
                    buttons.get(0).setDisabled(true);
            }
        });

        //Credits Button
        final HexagonalButton creditsButton = new HexagonalButton(menu, HexagonalMenu.Position.RIGHT_BOTTOM, menuSkin, "credits");
        buttons.put(4, creditsButton);
        creditsButton.setDisabled(true);
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (creditsButton.isChecked()) {
                    uncheckOtherButtons(4);
                    buttons.get(0).setDisabled(false);
                    Gdx.app.log(TAG, "Credits Button clicked");
                } else
                    buttons.get(0).setDisabled(true);
            }
        });

        //Multiplayer Button
        final HexagonalButton multiPlayerButton = new HexagonalButton(menu, HexagonalMenu.Position.LEFT_TOP, menuSkin, "multiplayer");
        buttons.put(5, multiPlayerButton);
        multiPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (multiPlayerButton.isChecked()) {
                    uncheckOtherButtons(5);
                    buttons.get(0).setDisabled(false);
                    nextScreen = new SimpleGoGameScreen();
                } else
                    buttons.get(0).setDisabled(true);
            }
        });

        //Parameters Button
        final HexagonalButton parametersButton = new HexagonalButton(menu, HexagonalMenu.Position.LEFT_BOTTOM, menuSkin, "settings");
        buttons.put(6, parametersButton);
        parametersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (parametersButton.isChecked()) {
                    uncheckOtherButtons(6);
                    buttons.get(0).setDisabled(false);
                    nextScreen = new GuiTestScreen();
                } else
                    buttons.get(0).setDisabled(true);
            }
        });
        hudStage.addActor(menu);
    }

    private void play() {
        if (nextScreen != null) {
            RageGoGame.getInstance().load(nextScreen);
        }
    }

    private void uncheckOtherButtons(int buttonValue) {
        for (int i = 2; i < buttons.size(); i++) {
            if (i != buttonValue)
                buttons.get(i).setChecked(false);
        }
    }

    private boolean isAButtonChecked() {
        for (int i = 2; i < buttons.size(); i++) {
            if (buttons.get(i).isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        backViewport.update(width, height);
        hudViewport.update(width, height, true);
        menu.setPosition((hudViewport.getScreenWidth() - menu.getWidth()) * 0.5f,
                (hudViewport.getScreenHeight() - menu.getHeight()) * 0.5f);

    }

    @Override
    public void dispose() {
        menuSkin.dispose();
        hudStage.dispose();
        backStage.dispose();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        backStage.draw();

        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        hudStage.draw();
    }

    @Override
    public void resume() {
        super.resume();
        this.nextScreen = null;
    }
}