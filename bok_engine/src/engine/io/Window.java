package engine.io;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import renderEngine.MasterRendererT;

public class Window {
    private static int width, height;
    private String title;
    private long window;
    private int frames;
    private long time;
    private Input input;
    private Vector3f background = new Vector3f(0, 0, 0);
    private GLFWWindowSizeCallback sizeCallback;
    private boolean isResized;
    private boolean isFullscreen;
    private int[] windowPosX = new int[1], windowPosY = new int[1];
    private long lastFrameTime, currentFrameTime;
	private static float delta;
	private double mouseX, mouseY;
	private static double mouseDY, mouseDX;
    
    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }
    
    public void create() {
        if (!GLFW.glfwInit()) {
            System.err.println("ERROR: GLFW wasn't initialized");
            return;
        }
        
        input = new Input();
        window = GLFW.glfwCreateWindow(width, height, title, isFullscreen ? GLFW.glfwGetPrimaryMonitor() : 0, 0);
        
        if (window == 0) {
            System.err.println("ERROR: Window wasn't created");
            return;
        }
        
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        windowPosX[0] = (videoMode.width() - width) / 2;
        windowPosY[0] = (videoMode.height() - height) / 2;
        GLFW.glfwSetWindowPos(window, windowPosX[0], windowPosY[0]);
        GLFW.glfwGetCurrentContext();
        
        GLFW.glfwMakeContextCurrent(window);
        
        GL.createCapabilities();
        
        createCallbacks();
        
        GLFW.glfwShowWindow(window);
        
        GLFW.glfwSwapInterval(1);
        
        time = System.currentTimeMillis();
        currentFrameTime = getCurrentTime();
    }
    
    private static long getCurrentTime() {
    	long time_seconds = System.currentTimeMillis();
    	return time_seconds;
    }
    
    private void createCallbacks() {
        sizeCallback = new GLFWWindowSizeCallback() {
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                isResized = true;
            }
        };
        
        GLFW.glfwSetKeyCallback(window, input.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonsCallback());
        GLFW.glfwSetScrollCallback(window, input.getMouseScrollCallback());
        GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
    }
    
    public void update(MasterRendererT master_renderer) {
        if (isResized) {
            GL11.glViewport(0, 0, width, height);
            isResized = false;
            master_renderer.recreate_projection_matrix();
        }
        GLFW.glfwPollEvents();
        frames++;
        
        if (System.currentTimeMillis() > time + 1000) {
            GLFW.glfwSetWindowTitle(window, title + " FPS: " + frames + "");
            time = System.currentTimeMillis();
            frames = 0;
        }
        lastFrameTime = currentFrameTime;
        currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime)/1000f;
        calculateMouseChange();
    }
    
    public void calculateMouseChange() {
    	double oldMouseX = mouseX;
    	double oldMouseY = mouseY;
    	mouseX = Input.getMouseX();
    	mouseY = Input.getMouseY();
    	mouseDX = mouseX-oldMouseX;
    	mouseDY = mouseY-oldMouseY;
    }
    
    public static float getFrameTimeSeconds() {
    	return delta;
    }
    
    public void swapBuffers() {
        GLFW.glfwSwapBuffers(window);
    }
    
    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }
    
    public void destroy() {
        input.destroy();
        sizeCallback.free();
        GLFW.glfwWindowShouldClose(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }
    
    public void setBackgroundColor(float r, float g, float b) {
        background.set(r, g, b);
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        isResized = true;
        if (isFullscreen) {
            GLFW.glfwGetWindowPos(window,  windowPosX, windowPosY);
            GLFW.glfwSetWindowMonitor(window,  GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, 0);
        } else {
            GLFW.glfwSetWindowMonitor(window, 0, windowPosX[0], windowPosY[0], width, height, 0);
        }
    }
    
    public void mouseState(boolean lock) {
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, lock ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public long getWindow() {
        return window;
    }

    public long getTime() {
        return time;
    }

    public Input getInput() {
        return input;
    }

	public static double getMouseDY() {
		return mouseDY;
	}

	public static double getMouseDX() {
		return mouseDX;
	}
    
    
}
