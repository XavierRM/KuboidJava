package test;

import Kuboid.manager.EngineManager;
import Kuboid.manager.WindowManager;

import static Kuboid.manager.utils.Constants.TITLE;

public class Launcher {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) throws Exception {
        window = new WindowManager(TITLE, 1280, 720, false);
        game = new TestGame();
        EngineManager engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}