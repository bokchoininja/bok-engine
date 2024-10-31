package engineTester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import animation.AnimatedModel;
import animation.AnimationLoader;
import engine.io.Input;
import engine.io.Window;
import entities.AnimatedEnemy;
import entities.AnimatedEntity;
import entities.AnimatedPhysicalEntity;
import entities.CameraT;
import entities.EntityT;
import entities.Light;
import entities.PlayerA;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import physics.PhysicalEntity;
import physics.Physics;
import renderEngine.Loader;
import renderEngine.MasterRendererT;
import renderEngine.OBJLoader;
import spatial.SpatialHashing;
import terrains.Terrain;
import texture.Material;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.PlayerT;
import entities.Zombie;
import guis.GuiRenderer;
import guis.GuiTexture;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;

public class MainT3 implements Runnable{
    private Thread game;
    private static Window window;
    private final int WIDTH = 1280, HEIGHT = 720;
    
    private static final double TARGET_FRAME_TIME = 1/60.0;
    
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
            double startTime = GLFW.glfwGetTime();
            second_loop();
            if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) {
                window.setFullscreen(!window.isFullscreen());
            }
            //if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) window.mouseState(true);
            //window.mouseState(true);
            //if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) return;
            /*
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
            /*
            if (glfwGetWindowAttrib(window.getWindow(), GLFW_FOCUSED) == GLFW_TRUE) {
                // The window is focused
                System.out.println("Window is focused.");
            } else {
                // The window is not focused
                System.out.println("Window is not focused.");
            }*/
            window.swapBuffers();
            double endTime = GLFW.glfwGetTime();
            double frameTime = endTime - startTime;
            if (frameTime < TARGET_FRAME_TIME) {
                try {
                    Thread.sleep((long) ((TARGET_FRAME_TIME - frameTime) * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
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
    RawModel cube_model;
    RawModel lamp_model;
    //RawModel zombie_model;
    ModelData data;
    ModelData test_person;
    ModelData bunny_data;
    ModelData bok_club_data;
    ModelData title_data;
    ModelData tree_data;
    ModelData house_data;
    ModelData lamp_data;
    //ModelData zombie_data;
    ModelData cube_data;
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
    
    AnimationLoader animation_loader;
    
    Physics physics;
    SpatialHashing<AnimatedEnemy> spatial_hashing = new SpatialHashing<>(10.0f);
    
    Random random = new Random();
    
    List<EntityT> allEntities = new ArrayList<EntityT>();
    //List<PhysicalEntity> physical_entities = new ArrayList<PhysicalEntity>();
    List<Terrain> allTerrains = new ArrayList<Terrain>();
    List<GuiTexture> guis = new ArrayList<GuiTexture>();
    List<Light> allLights = new ArrayList<Light>();
    //List<AnimatedEntity> animated_entities = new ArrayList<AnimatedEntity>();
    List<Zombie> zombies = new ArrayList<Zombie>();
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
        animatedModel = animation_loader.loadPlayer(loader);
        player = new PlayerA(animatedModel, texture, new Vector3f(65,10,65), new Vector3f(0,0,0), 2);
        physics.add_player(player);
        //spatial_hashing.insert(player, 65, 10, 65);
    }
    
    private void create_guis() {
        guiRenderer = new GuiRenderer(loader);
        gui = new GuiTexture(loader.loadTexture("fern"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        guis.add(gui);
    }
    
    private void create_lamps() {
        lamp_data = OBJFileLoader.loadOBJ("lamp");
        lamp_model = loader.loadToVAO(lamp_data.getVertices(), lamp_data.getTextureCoords(), lamp_data.getNormals(), lamp_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("lamp"));
        texturedModel = new TexturedModel(lamp_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        for (int i = 0; i < 14; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            CollisionShape shape = new CylinderShape(new javax.vecmath.Vector3f(1f, 10, 1f));
            PhysicalEntity lamp = new PhysicalEntity(texturedModel, new Vector3f(x,y,z), 
                    new Vector3f(0f, random.nextFloat() * 180f, 0f), 1f, shape, 5f);
            allLights.add(new Light(new Vector3f(x,y+6,z), 
                    new Vector3f(random.nextFloat(),random.nextFloat(),random.nextFloat()).normalize(), 
                    new Vector3f(0.2f, 0.02f, 0.002f)));
            physics.add_static_physical_entity(lamp);
        }
    }
    
    private void create_trees() {
        tree_data = OBJFileLoader.loadOBJ("ball_tree");
        tree_model = loader.loadToVAO(tree_data.getVertices(), tree_data.getTextureCoords(), tree_data.getNormals(), tree_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("ball_tree"));
        texturedModel = new TexturedModel(tree_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        CollisionShape shape = new CylinderShape(new javax.vecmath.Vector3f(1.5f,5,1.5f));
        for (int i = 0; i < 50; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            PhysicalEntity tree = new PhysicalEntity(texturedModel, new Vector3f(x,y,z), 
                    new Vector3f(0f, random.nextFloat() * 180f, 0f), 2f, shape, 5f);
            physics.add_static_physical_entity(tree);
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
        allEntities.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                new Vector3f(0, 210f, 0f), 5f));
    }
    
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
        allEntities.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
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
        allEntities.add(new EntityT(texturedModel, new Vector3f(x,y,z), 
                new Vector3f(0, 0, 0f), 1f));
    }
    
    private void create_grass() {
    	grass = new TexturedModel(OBJLoader.loadObjModel("single_flower", loader),
        		new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        for (int i = 0; i < 400; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            allEntities.add(new EntityT(grass, new Vector3f(x,y,z),
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
        for (int i = 0; i < 200; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            allEntities.add(new EntityT(fern, random.nextInt(4), new Vector3f(x,y,z), 
                    new Vector3f(0f, 0f, 0f), 1f));
        }
    }
    
    private void create_flower() {
    	flower = new TexturedModel(OBJLoader.loadObjModel("single_flower", loader),
        		new ModelTexture(loader.loadTexture("flower")));
    	flower.getTexture().setHasTransparency(true);
    	flower.getTexture().setUseFakeLighting(true);
        for (int i = 0; i < 200; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            allEntities.add(new EntityT(flower, new Vector3f(x,y,z), 
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
    
    private void create_test_sphere() {
        cube_data = OBJFileLoader.loadOBJ("cube");
        cube_model = loader.loadToVAO(cube_data.getVertices(), cube_data.getTextureCoords(), cube_data.getNormals(), cube_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("path_texture"));
        texturedModel = new TexturedModel(cube_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        for (int i = 0; i < 10; i++) {
            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * 200;
            //float y = terrain.getHeightOfTerrain(x, z) + 20;
            float y = random.nextFloat() * 200 + 50;
            PhysicalEntity physical_entity = new PhysicalEntity(texturedModel, new Vector3f(x,y,z), 
                    new Vector3f(0, 0, 0f), 5f);
            physics.add_physical_entity(physical_entity);
        }
    }
    
    private void create_test_sphere2() {
        cube_data = OBJFileLoader.loadOBJ("cube");
        cube_model = loader.loadToVAO(cube_data.getVertices(), cube_data.getTextureCoords(), cube_data.getNormals(), cube_data.getIndices());
        texture = new ModelTexture(loader.loadTexture("path_texture"));
        texturedModel = new TexturedModel(cube_model, texture);
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        for (int i = 0; i < 100; i++) {
            float x = random.nextFloat() * 200;
            float z = random.nextFloat() * 200;
            float y = terrain.getHeightOfTerrain(x, z);
            //float y = random.nextFloat() * 200 + 50;
            PhysicalEntity physical_entity = new PhysicalEntity(texturedModel, new Vector3f(x,y,z), 
                    new Vector3f(0, 0, 0f), 5f, 0, new javax.vecmath.Vector3f(0,0,0));
            physics.add_physical_entity(physical_entity);
        }
    }

    private void second_init() {
        window = new Window(WIDTH, HEIGHT, "bok survival");
        window.setBackgroundColor(173/255f, 216/255f, 230/255f);
        window.create();
        create_terrains();
        physics = new Physics(terrain);
        create_trees();
        create_title();
        create_person();
        create_dragons();
        create_grass();
        create_fern();
        create_flower();
        create_lights();
        create_lamps();
        
        animation_loader = new AnimationLoader(loader);
        master_renderer = new MasterRendererT(loader);
        create_test_sphere();
        create_test_sphere2();
        create_player();
        cameraT = new CameraT(player);
    }
    
    int step = 0;
    
    private void second_loop() {
        player.move(terrain);
    	cameraT.move();
        
        window.update(master_renderer);
        for (EntityT entity : allEntities) {
            master_renderer.processEntity(entity);
        }
        for (PhysicalEntity entity : physics.get_physical_entities()) {
            master_renderer.processEntity(entity);
        }
        for (Terrain terrain : allTerrains) {
        	master_renderer.processTerrain(terrain);
        }
        master_renderer.processAnimatedPlayer(player);
        for (Zombie zombie : zombies) {
            zombie.move(terrain);
            master_renderer.processAnimatedEntity(zombie);
        }
        for (PhysicalEntity entity : physics.getStaticPhysicalEntities()) {
            master_renderer.processEntity(entity);
        }
        master_renderer.render(allLights, cameraT);
        physics.logic();
        // Batch update multiple objects
        if (step%6 == 0) {
            for (Zombie zombie : zombies) {
                zombie.setAgro(false);
            }
            Map<AnimatedEnemy, float[]> updates = new HashMap<>();
            for (Zombie zombie : zombies) {
                updates.put(zombie, new float[] {zombie.getPosition().x, zombie.getPosition().y, zombie.getPosition().z});
            }
            //updates.put(player, new float[] {player.getPosition().x, player.getPosition().y, player.getPosition().z});
            Set<AnimatedEnemy> updatedObjects = spatial_hashing.batchUpdatePositions(updates);
            List<AnimatedEnemy> nearbyObjects = spatial_hashing.findNearby(player.getPosition().x,
                    player.getPosition().y,
                    player.getPosition().z, 
                    40.0f);
            for (AnimatedEnemy enemy : nearbyObjects) {
                enemy.setAgro(true);
            }
        }
        step++;
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
        MainT3 main = new MainT3();
        main.start();
        //FocusedMulti fm = new FocusedMulti(main.window);
        //fm.start();
    }
}

class FocusedMulti implements Runnable {
    
    private Thread focused;
    private Window window;
    
    public FocusedMulti(Window window) {
        this.window = window;
    }
    
    public void start() {
        focused = new Thread(this, "focused");
        focused.start();
    }
    
    public void run() {
        if (glfwGetWindowAttrib(window.getWindow(), GLFW_FOCUSED) == GLFW_TRUE) {
            // The window is focused
            System.out.println("Window is focused.");
        } else {
            // The window is not focused
            System.out.println("Window is not focused.");
        }
    }
}
