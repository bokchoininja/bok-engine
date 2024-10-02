package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

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
import terrains.Terrain;
import texture.Material;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.PlayerT;
import guis.GuiRenderer;
import guis.GuiTexture;

public class MainT3 implements Runnable{
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
            second_loop();
            if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) {
                window.setFullscreen(!window.isFullscreen());
            }
            //if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) window.mouseState(true);
            //window.mouseState(true);
            //if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) return;
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*
            if (glfwGetWindowAttrib(window.getWindow(), GLFW_FOCUSED) == GLFW_TRUE) {
                // The window is focused
                System.out.println("Window is focused.");
            } else {
                // The window is not focused
                System.out.println("Window is not focused.");
            }*/
            window.swapBuffers();
        }
        close2();
    }

    Loader loader = new Loader();
    MasterRendererT master_renderer;
    
    RawModel model;
    RawModel person_model;
    RawModel bunny_model;
    RawModel bok_club_model;
    RawModel title_model;
    RawModel tree_model;
    RawModel house_model;
    //RawModel zombie_model;
    ModelData data;
    ModelData test_person;
    ModelData bunny_data;
    ModelData bok_club_data;
    ModelData title_data;
    ModelData tree_data;
    ModelData house_data;
    //ModelData zombie_data;
    ModelTexture texture;
    ModelTexture texture_test;
    ModelTexture textureGrass;
    ModelTexture fernTextureAtlas;
    TexturedModel texturedModel;
    EntityT entity;
    Light light;
    PlayerA player;
    CameraT cameraT;
    Terrain terrain; 
    TexturedModel grass;
    TexturedModel fern;
    TexturedModel flower;
    TerrainTexture backgroundTexture;
    TerrainTexture rTexture;
    TerrainTexture gTexture;
    TerrainTexture bTexture;
    TerrainTexturePack texturePack;
    TerrainTexture blendMap;
    
    AnimatedModel animatedModel;
    AnimatedEntity animatedEntity;
    Material runner;
    
    AnimatedModel zombie_model;
    AnimatedEntity zombie_entity;
    Material zombie;
    
    
    Random random = new Random();
    
    List<EntityT> allEntites = new ArrayList<EntityT>();
    List<Terrain> allTerrains = new ArrayList<Terrain>();
    List<GuiTexture> guis = new ArrayList<GuiTexture>();
    List<Light> allLights = new ArrayList<Light>();
    List<AnimatedEntity> animated_entities = new ArrayList<AnimatedEntity>();
    GuiTexture gui;
    GuiRenderer guiRenderer;
    
    private void create_lights() {
        light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1f,1f,1f));
        allLights.add(light);
        //allLights.add(new Light(new Vector3f(85, terrain.getHeightOfTerrain(75, 75)+15, 75), new Vector3f(2,0,0), new Vector3f(1,0.0f,0.002f)));
        //allLights.add(new Light(new Vector3f(70, terrain.getHeightOfTerrain(70, 100)+15, 100), new Vector3f(0,2,0), new Vector3f(1,0.0f,0.002f)));
        //allLights.add(new Light(new Vector3f(90, terrain.getHeightOfTerrain(90, 75)+15, 75), new Vector3f(0,0,2), new Vector3f(1,0.0f,0.002f)));
        
        /*
        for (int i = 0; i < 100; i++) {
            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * 200;
            float y = terrain.getHeightOfTerrain(x, z) + 5;
            allLights.add(new Light(new Vector3f(x,y,z), new Vector3f(random.nextFloat()*2,random.nextFloat()*2,random.nextFloat()*2), new Vector3f(1,0.0f,0.002f)));
        }*/
    }
    
    
    private void create_player() {
        texture = new ModelTexture(loader.loadTexture("blue"));
        animatedModel = AnimationLoader.load(loader, "lpm27092024_4.fbx");
        player = new PlayerA(animatedModel, texture, new Vector3f(65,0,65), new Vector3f(0,0,0), 2);
    }
    
    private void create_guis() {
        guiRenderer = new GuiRenderer(loader);
        gui = new GuiTexture(loader.loadTexture("fern"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        guis.add(gui);
    }
    
    private void create_lamps() {
        tree_data = OBJFileLoader.loadOBJ("lamp");
        tree_model = loader.loadToVAO(tree_data.getVertices(), tree_data.getTextureCoords(), tree_data.getNormals(), tree_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("lamp"));
        texturedModel = new TexturedModel(tree_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        for (int i = 0; i < 10; i++) {
            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * 200;
            float y = terrain.getHeightOfTerrain(x, z);
            allEntites.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                    new Vector3f(0f, random.nextFloat() * 180f, 0f), 1f));
            allLights.add(new Light(new Vector3f(x,y+6,z), 
                    new Vector3f(random.nextFloat(),random.nextFloat(),random.nextFloat()).normalize(), 
                    new Vector3f(0.2f, 0.02f, 0.002f)));
        }
    }
    
    /*
    private void create_zombie() {
        zombie = new Material(loader.loadTextureS("zombie29092024.png"));
        zombie_model = AnimationLoader.load(loader, "zombie29092024.fbx");
        texture = new ModelTexture(loader.loadTexture("zombie29092024"));
        float x = random.nextFloat() * 200;
        float z = random.nextFloat() * 200;
        float y = terrain.getHeightOfTerrain(x, z);
        animated_entities.add(new AnimatedEntity(zombie_model, texture, new Vector3f(x,y,z), new Vector3f(0,0,0), 1f));
    }*/
    
    /*
    private void create_house() {
        house_data = OBJFileLoader.loadOBJ("lph28092024");
        house_model = loader.loadToVAO(house_data.getVertices(), house_data.getTextureCoords(), house_data.getNormals(), house_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("lph28092024"));
        texturedModel = new TexturedModel(house_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        float x = random.nextFloat() * 200;
        float z = random.nextFloat() * 200;
        float y = terrain.getHeightOfTerrain(x, z);
        allEntites.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                new Vector3f(0f, random.nextFloat() * 180f, 0f), 4f));
    }*/
    
    private void create_trees() {
        tree_data = OBJFileLoader.loadOBJ("ball_tree");
        tree_model = loader.loadToVAO(tree_data.getVertices(), tree_data.getTextureCoords(), tree_data.getNormals(), tree_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("ball_tree"));
        texturedModel = new TexturedModel(tree_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        for (int i = 0; i < 10; i++) {
            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * 200;
            float y = terrain.getHeightOfTerrain(x, z);
            allEntites.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                    new Vector3f(0f, random.nextFloat() * 180f, 0f), 2f));
        }
    }
    
    private void create_title() {
        title_data = OBJFileLoader.loadOBJ("bok title 09092024");
        title_model = loader.loadToVAO(title_data.getVertices(), title_data.getTextureCoords(), title_data.getNormals(), title_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("blue"));
        texturedModel = new TexturedModel(title_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        float x = 90;
        float z = 75;
        float y = terrain.getHeightOfTerrain(x, z);
        allEntites.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                new Vector3f(0, 210f, 0f), 5f));
    }
    
    /*
    private void create_bok() {
    	bok_club_data = OBJFileLoader.loadOBJ("bok_club");
        bok_club_model = loader.loadToVAO(bok_club_data.getVertices(), bok_club_data.getTextureCoords(), bok_club_data.getNormals(), bok_club_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("bok_club"));
        texturedModel = new TexturedModel(bok_club_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        float x = 75;
        float z = 100;
        float y = terrain.getHeightOfTerrain(x, z);
        allEntites.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                new Vector3f(0, 0, 0f), 3f));
    }*/
    
    /*
    private void create_bunny() {
    	bunny_data = OBJFileLoader.loadOBJ("bunny");
        bunny_model = loader.loadToVAO(bunny_data.getVertices(), bunny_data.getTextureCoords(), bunny_data.getNormals(), bunny_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("blue"));
        texturedModel = new TexturedModel(bunny_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        player = new PlayerT(texturedModel, new Vector3f(0,0,-10), 
                new Vector3f(0, 0, 0f), 1f);
    }*/
    
    private void create_person() {
    	test_person = OBJFileLoader.loadOBJ("homem_base2");
        person_model = loader.loadToVAO(test_person.getVertices(), test_person.getTextureCoords(), test_person.getNormals(), test_person.getIndices());
        texture = new ModelTexture(loader.loadTexture("blue"));
        texturedModel = new TexturedModel(person_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        float x = 100;
        float z = 75;
        float y = terrain.getHeightOfTerrain(x, z);
        allEntites.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                new Vector3f(0, 0, 0f), 10f));
    }
    
    private void create_dragons() {
    	data = OBJFileLoader.loadOBJ("dragon");
        model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        texture = new ModelTexture(loader.loadTexture("blue"));
        texturedModel = new TexturedModel(model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        float x = 75;
        float z = 75;
        float y = terrain.getHeightOfTerrain(x, z);
        allEntites.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                new Vector3f(0, 0, 0f), 1f));
    }
    
    private void create_grass() {
    	grass = new TexturedModel(OBJLoader.loadObjModel("single_flower", loader),
        		new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        for (int i = 0; i < 100; i++) {
            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * 200;
            float y = terrain.getHeightOfTerrain(x, z);
            allEntites.add(new EntityT(grass, new Vector3f(x,y,z),
                    new Vector3f(0f, 0f, 0f), 3f));
        }
    }
    
    private void create_fern() {
        fernTextureAtlas = new ModelTexture(loader.loadTexture("fern_atlas"));
        fernTextureAtlas.setNumberOfRows(2);
    	fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
        		fernTextureAtlas);
    	fern.getTexture().setHasTransparency(true);
    	fern.getTexture().setUseFakeLighting(true);
        for (int i = 0; i < 100; i++) {
            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * 200;
            float y = terrain.getHeightOfTerrain(x, z);
            allEntites.add(new EntityT(fern, random.nextInt(4), new Vector3f(x,y,z), 
                    new Vector3f(0f, 0f, 0f), 1f));
        }
    }
    
    private void create_flower() {
    	flower = new TexturedModel(OBJLoader.loadObjModel("single_flower", loader),
        		new ModelTexture(loader.loadTexture("flower")));
    	flower.getTexture().setHasTransparency(true);
    	flower.getTexture().setUseFakeLighting(true);
        for (int i = 0; i < 100; i++) {
            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * 200;
            float y = terrain.getHeightOfTerrain(x, z);
            allEntites.add(new EntityT(flower, new Vector3f(x,y,z), 
                    new Vector3f(0f, 0f, 0f), 3f));
        }
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

    private void second_init() {
        window = new Window(WIDTH, HEIGHT, "bok survival");
        window.setBackgroundColor(173/255f, 216/255f, 230/255f);
        window.create();
        model = OBJLoader.loadObjModel("dragon", loader);
        texture = new ModelTexture(loader.loadTexture("blue"));
        texturedModel = new TexturedModel(model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        entity = new EntityT(texturedModel, new Vector3f(0,0,-10), new Vector3f(0,0,0),1);
        create_terrains();
        create_trees();
        create_title();
        //create_bok();
        create_person();
        create_dragons();
        create_grass();
        create_fern();
        create_flower();
        create_lights();
        create_player();
        create_lamps();
        //create_house();
        //create_zombie();
        cameraT = new CameraT(player);
        master_renderer = new MasterRendererT(loader);
    }
    
    float time = 0f;
    
    private void second_loop() {
        player.move(terrain);
    	cameraT.move();
        
        window.update(master_renderer);
        for (EntityT entity : allEntites) {
            master_renderer.processEntity(entity);
        }
        for (Terrain terrain : allTerrains) {
        	master_renderer.processTerrain(terrain);
        }
        master_renderer.processAnimatedPlayer(player);
        for (AnimatedEntity animated_entity : animated_entities) {
            animated_entity.getAnimatedModel().updateAnimation(0, time++);
            master_renderer.processAnimatedEntity(animated_entity);

        }
        master_renderer.render(allLights, cameraT);
    }
    
    private void close2() {
        //guiRenderer.cleanUp();
        clean_up_loader2();
        master_renderer.cleanUp();
        window.destroy();
    }
    
    private void clean_up_loader2() {
        loader.cleanUp();
    }

    public static void main(String[] args) {
        new MainT3().start();
    }

}
