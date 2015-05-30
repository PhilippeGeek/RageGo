package com.ragego.gui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ragego.gui.screens.MenuScreen;
import com.ragego.gui.screens.MusicalScreen;
import com.ragego.utils.InternetCheckThread;

/**
 * General entry point for all devices. Describes how to start the game.
 */
public class RageGoGame extends Game {
    private static final String TAG = "RageGoGame";
    private static final RageGoGame instance = new RageGoGame();
    private static final RageGoAssetManager manager = new RageGoAssetManager();
    private static final Preferences preferences = null;
    private static Skin uiSkin;
    private MenuScreen homeScreen = null;
    private InternetCheckThread checkInternetConnection = new InternetCheckThread();

    private RageGoGame() {
        super();
        checkInternetConnection.start();
    }

    public static RageGoGame getInstance() {
        return instance;
    }

    public static RageGoAssetManager getAssetManager() {
        return manager;
    }

    public static void loadScreen(ScreenAdapter nextScreen) {
        getInstance().load(nextScreen);
    }

    public static void goHome() {
        getInstance().load(getInstance().getHomeScreen());
    }

    public static Skin getUiSkin() {
        return uiSkin;
    }

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        manager.load("com/ragego/gui/ui/ui.json", Skin.class);
        manager.finishLoading();
        Gdx.app.log(TAG, "Skin loaded");
        uiSkin = manager.get("com/ragego/gui/ui/ui.json");

        homeScreen = new MenuScreen();
        loadScreen(homeScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    /**
     * Loads the screen to be displayed
     *
     * @param nextScreen The screen to be displayed
     */
    public void load(ScreenAdapter nextScreen) {
        if (nextScreen != null) {
            if(screen!=null)
                if(screen instanceof MusicalScreen) ((MusicalScreen) screen).stopMusic();
            if(nextScreen instanceof MusicalScreen) ((MusicalScreen) nextScreen).playMusic();
            setScreen(nextScreen);
            screen.resume();
        }
    }

    /**
     * Gets the home screen.
     *
     * @return The home Screen
     */
    public MenuScreen getHomeScreen() {
        return homeScreen;
    }

    /**
     * Checks for the internet connection status
     *
     * @return Whether the game has or hasn't access to the internet
     */
    public boolean isConnected() {
        return checkInternetConnection.isConnected();
    }
}
