package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import animation.AnimatedModel;
import engine.io.Window;
import entities.AnimatedEntity;
import entities.CameraT;
import entities.EntityT;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import shaders.AnimationShader;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import toolbox.GLUtills;
import toolbox.Maths;

public class MasterRendererT {
    
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;
    private static final float RED = 173/255f;
    private static final float GREEN = 216/255f;
    private static final float BLUE = 230/255f;
    private Matrix4f projectionMatrix;
    private StaticShader shader = new StaticShader();
    private EntityRendererT renderer;
    
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    
    private Map<TexturedModel,List<EntityT>> entities = new HashMap<TexturedModel, List<EntityT>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();
    
    private SkyboxRenderer skyboxRenderer;
    
    private AnimationShader animationShader = new AnimationShader();
    private AnimationRenderer animationRenderer;
    private AnimatedEntity animatedEntity;
    
    private List<AnimatedEntity> animated_entities = new ArrayList<AnimatedEntity>();
    
    public MasterRendererT(Loader loader) {
        GLUtills.initOpenGLSettings();
        createProjectionMatrix();
        animationRenderer = new AnimationRenderer(animationShader, projectionMatrix);
        enableCulling();
        renderer = new EntityRendererT(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
    }
    
    public static void enableCulling() {
    	GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }
    
    public static void disableCulling() {
    	GL11.glDisable(GL11.GL_CULL_FACE);
    }
    
    public void render(RawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
    
    public void render(List<Light> lights, CameraT camera) {
        prepare();
        shader.start();
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(camera, RED, GREEN, BLUE);
        terrains.clear();
        entities.clear();
        
        animationShader.start();
        animationShader.loadSkyColour(RED, GREEN, BLUE);
        animationShader.loadLights(lights);
        animationShader.loadViewMatrix(camera);
        //animationRenderer.setTransformationMatrix(animatedEntity);
        animationRenderer.render(animatedEntity);
        animationRenderer.render(animated_entities);
        animationShader.stop();
    }
    
    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }
    
    public void processEntity(EntityT entity) {
        TexturedModel entityModel = entity.getModel();
        List<EntityT> batch = entities.get(entityModel);
        if(batch!=null) {
            batch.add(entity);
        } else {
            List<EntityT> newBatch = new ArrayList<EntityT>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }
    
    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
        animationShader.cleanUp();
    }
    
    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        //GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT|GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
    }
    
    private void createProjectionMatrix() {
        float aspectRatio = (float) Window.getWidth() / (float) Window.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        
        projectionMatrix = new Matrix4f();
        projectionMatrix.set(0,0,x_scale);
        projectionMatrix.set(1,1,y_scale);
        projectionMatrix.set(2,2,-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix.set(2,3,-1);
        projectionMatrix.set(3,2,-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix.set(3,3,0);
    }
    
    public void recreate_projection_matrix() {
        createProjectionMatrix();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        terrainShader.start();
        terrainShader.loadProjectionMatrix(projectionMatrix);
        terrainShader.stop();
        animationShader.start();
        animationShader.loadProjectionMatrix(projectionMatrix);
        animationShader.stop();
    }
    
    public void processAnimatedPlayer(AnimatedEntity animatedEntity) {
        this.animatedEntity = animatedEntity;
    }
    
    public void processAnimatedEntity(AnimatedEntity animatedEntity) {
        animated_entities.add(animatedEntity);
    }
}
