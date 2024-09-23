package engineTester;

import org.lwjgl.glfw.GLFW;

import engine_test.Input_test;
import engine_test.Loader_test;
import engine_test.RawModel_test;
import engine_test.Renderer_test;
import engine_test.Window_test;

/**
 * This class contains the main method and is used to test the engine.
 * 
 * @author Karl
 *
 */
public class MainGameLoop_test implements Runnable{
    
    private Thread game;
    private Window_test window;
    private final int WIDTH = 1280, HEIGHT = 720;
    
    public void start() {
        game = new Thread(this, "game");
        game.start();
    }
    
    public void init() {
        System.out.println("Initializing Game!");
        window = new Window_test(WIDTH, HEIGHT, "triworld");
        
        window.setBackgroundColor(173/255f, 216/255f, 230/255f);
        window.create();
    }
    
    public void run() {
        //init();
        second_init();
        while(!window.shouldClose() && !Input_test.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            //update();
            //render();
            second_loop();
            if (Input_test.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
            //if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) window.mouseState(true);
            //window.mouseState(true);
            //if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) return;
        }
        close2();
    }

    Loader_test loader = new Loader_test();
    Renderer_test master_renderer;

    private void second_init() {
        window = new Window_test(WIDTH, HEIGHT, "bok survival");
        window.setBackgroundColor(173/255f, 216/255f, 230/255f);
        window.create();
        master_renderer = new Renderer_test();
        model = loader.loadToVAO(vertices);
    }
    
    float[] vertices = {
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f,
            -0.5f, 0.5f, 0f
          };
    
    RawModel_test model;
    
    private void second_loop() {
        master_renderer.prepare();
        master_renderer.render(model);
        window.update();
        window.swapBuffers();
    }
    
    private void close2() {
        clean_up_loader2();
        //master_renderer.cleanUp();
        window.destroy();
    }
    
    private void clean_up_loader2() {
        loader.cleanUp();
    }

    public static void main(String[] args) {
        new MainGameLoop_test().start();
    }

}
