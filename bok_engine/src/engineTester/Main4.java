package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import animation.AnimatedModel;
import animation.AnimationLoader;
import engine.io.Input;
import engine.io.Window;
import entities.AnimatedEntity;
import entities.CameraT;
import entities.EntityT;
import entities.Light;
import entities.PlayerA;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.Loader;
import renderEngine.MasterRendererT;
import renderEngine.OBJLoader;
import renderer.AssimpLoader;
import shaders.AnimationShader;
import terrains.Terrain;
import texture.Material;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.PlayerT;
import guis.GuiRenderer;
import guis.GuiTexture;

public class Main4 implements Runnable{
    private Thread game;
    private Window window;
    private final int WIDTH = 1280, HEIGHT = 720;
    
    public void start() {
        game = new Thread(this, "game");
        game.start();
    }
    
    public void init() {
        System.out.println("Initializing Game!");
        window = new Window(WIDTH, HEIGHT, "triworld");
        
        window.setBackgroundColor(173/255f, 216/255f, 230/255f);
        window.create();
    }
    
    public void run() {
        //init();
        second_init();
        while(!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            //update();
            //render();
            second_loop();
            if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
            //if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) window.mouseState(true);
            //window.mouseState(true);
            //if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) return;
        }
        close2();
    }

    Loader loader;
    MasterRendererT master_renderer;
    RawModel dragon_model;
    RawModel bunny_model;
    ModelData dragon_data;
    ModelData bunny_data;
    ModelTexture model_texture;
    ModelTexture texture;
    TexturedModel dragon_textured;
    TexturedModel texturedModel;
    CameraT camera;
    PlayerA player;
    Terrain terrain;
    ModelTexture textureGrass;
    TerrainTexture backgroundTexture;
    TerrainTexture rTexture;
    TerrainTexture gTexture;
    TerrainTexture bTexture;
    TerrainTexturePack texturePack;
    TerrainTexture blendMap;
    Light light;
    Material runner;
    
    AnimatedModel animatedModel;
    AnimatedEntity animatedEntity;
    
    List<EntityT> allEntites = new ArrayList<EntityT>();
    List<Terrain> allTerrains = new ArrayList<Terrain>();
    List<Light> allLights = new ArrayList<Light>();
    
    private void create_lights() {
        light = new Light(new Vector3f(20000,20000,2000),new Vector3f(0.8f,0.8f,0.8f));
        allLights.add(light);
        allLights.add(new Light(new Vector3f(85, terrain.getHeightOfTerrain(75, 75)+15, 75), new Vector3f(2,0,0), new Vector3f(1,0.0f,0.002f)));
        allLights.add(new Light(new Vector3f(70, terrain.getHeightOfTerrain(70, 100)+15, 100), new Vector3f(0,2,0), new Vector3f(1,0.0f,0.002f)));
        allLights.add(new Light(new Vector3f(90, terrain.getHeightOfTerrain(90, 75)+15, 75), new Vector3f(0,0,2), new Vector3f(1,0.0f,0.002f)));
    }
    
    private void create_dragons() {
        dragon_data = OBJFileLoader.loadOBJ("dragon");
        dragon_model = loader.loadToVAO(dragon_data.getVertices(), dragon_data.getTextureCoords(), dragon_data.getNormals(), dragon_data.getIndices());
        model_texture = new ModelTexture(loader.loadTexture("blue"));
        dragon_textured = new TexturedModel(dragon_model, model_texture);
        model_texture.setShineDamper(10);
        model_texture.setReflectivity(1);
        float x = 75;
        float z = 75;
        float y = terrain.getHeightOfTerrain(x, z);
        allEntites.add(new EntityT(dragon_textured, new Vector3f(x,y,z), 
                new Vector3f(0, 0, 0f), 1f));
    }
    
    private void create_terrains() {
        backgroundTexture = new TerrainTexture(loader.loadTexture("grass_texture"));
        rTexture = new TerrainTexture(loader.loadTexture("dry_texture"));
        gTexture = new TerrainTexture(loader.loadTexture("flowery_grass"));
        bTexture = new TerrainTexture(loader.loadTexture("path_texture"));
        texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blend_map"));
        textureGrass = new ModelTexture(loader.loadTexture("grass_texture"));
        terrain = new Terrain(0,0,loader,texturePack, blendMap, "heightMap");
        allTerrains.add(terrain);
    }
    
    private void create_bunny() {
        bunny_data = OBJFileLoader.loadOBJ("bunny");
        bunny_model = loader.loadToVAO(bunny_data.getVertices(), bunny_data.getTextureCoords(), bunny_data.getNormals(), bunny_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("blue"));
        texturedModel = new TexturedModel(bunny_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        //player = new PlayerA(texturedModel, new Vector3f(0,0,-10), 
                //new Vector3f(0, 0, 0f), 1f);
    }
    
    private void create_player() {
        runner = new Material(loader.loadTextureS("blue.png"));
        animatedModel = AnimationLoader.load(loader, "lpm17092024.fbx", runner.getTexture());
        player = new PlayerA(animatedModel, new Vector3f(65,0,65), new Vector3f(0,0,0), 2);
    }

    private void second_init() {
        window = new Window(WIDTH, HEIGHT, "bok survival");
        window.setBackgroundColor(173/255f, 216/255f, 230/255f);
        window.create();
        loader = new Loader();
        create_terrains();
        create_dragons();
        //create_bunny();
        create_lights();
        create_player();
        camera = new CameraT(player);
        master_renderer = new MasterRendererT(loader);
        //modelS = new TexturedModelS(AssimpLoader.load(loaderS, "runner.dae"), loaderS.runner);
        animatedModel.updateAnimation(0, 0);
    }
    
    private void second_loop() {
        player.move(terrain);
        camera.move();
        window.update(master_renderer);
        for (EntityT entity : allEntites) {
            master_renderer.processEntity(entity);
        }
        for (Terrain terrain : allTerrains) {
            master_renderer.processTerrain(terrain);
        }
        master_renderer.processAnimatedModel(player);
        master_renderer.render(allLights, camera);
        window.swapBuffers();
    }
    
    private void close2() {
        clean_up_loader2();
        master_renderer.cleanUp();
        window.destroy();
    }
    
    private void clean_up_loader2() {
        loader.cleanUp();
    }

    public static void main(String[] args) {
        new Main4().start();
    }

}
