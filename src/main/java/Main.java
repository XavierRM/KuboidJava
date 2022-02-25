import Kuboid.manager.WindowManager;

public class Main {
    public static void main(String[] args) {
        WindowManager window = new WindowManager("Kuboid", 1280, 720, true);
        window.init();

        while(!window.windowShouldClose()) {
            window.update();
        }

        window.cleanup();
    }
}