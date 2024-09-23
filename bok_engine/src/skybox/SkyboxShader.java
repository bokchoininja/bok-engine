package skybox;


import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.io.Window;
import entities.CameraT;
import shaders.ShaderProgram;
import toolbox.Maths;


public class SkyboxShader extends ShaderProgram{


    private static final String VERTEX_FILE = "src/skybox/skyboxVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/skybox/skyboxFragmentShader.txt";
    
    private static final float ROTATION_SPEED = 1f;
    
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColour;
    private int location_cubeMap;
    private int location_cubeMap2;
    private int location_blendFactor;
    
    private float rotation = 0;
    
    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
    
    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }


    public void loadViewMatrix(CameraT camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.set(3,0,0);
        matrix.set(3,1,0);
        matrix.set(3,2,0);
        //rotation += ROTATION_SPEED * Window.getFrameTimeSeconds();
        //Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0,1,0), matrix, matrix);
        super.loadMatrix(location_viewMatrix, matrix);
    }
    
    public void loadFogColour(float r, float g, float b) {
        super.loadVector(location_fogColour, new Vector3f(r, g, b));
    }
    
    public void connectTextureUnits() {
        super.loadInt(location_cubeMap, 0);
        super.loadInt(location_cubeMap2, 1);
    }
    
    public void loadBlendFactor(float blend) {
        super.loadFloat(location_blendFactor, blend);
    }
    
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColour = super.getUniformLocation("fogColour");
        location_blendFactor = super.getUniformLocation("blendFactor");
        location_cubeMap = super.getUniformLocation("cubeMap");
        location_cubeMap2 = super.getUniformLocation("cubeMap2");
    }


    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }


}
