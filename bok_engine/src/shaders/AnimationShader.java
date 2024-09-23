package shaders;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import animation.Bone;
import entities.CameraT;
import entities.Light;
import toolbox.Maths;

public class AnimationShader extends ShaderProgram
{
    private final int MAX_BONES = 50;
    private static final int MAX_LIGHTS = 16;

    private final static String vsPath = "src/shaders/animationVertexShader.txt";
    private final static String fsPath = "src/shaders/animationFragmentShader.txt";

    // Uniform locations
    private int transformationMatrix;
    private int projectionMatrix;
    private int viewMatrix;
    private int location_lightPosition[];
    private int location_lightColour[];
    private int location_attenuation[];
    private int location_shineDamper;
    private int location_reflectivity;
    private int[] boneTransforms;
    private int location_skyColour;

    public AnimationShader()
    {
        super(vsPath, fsPath);
    }
    
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations()
    {
        transformationMatrix = super.getUniformLocation("transformationMatrix");
        projectionMatrix = super.getUniformLocation("projectionMatrix");
        viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColour = super.getUniformLocation("skyColour");
        
        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        for(int i = 0; i < MAX_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }

        boneTransforms = new int[MAX_BONES];
        for (int i = 0; i < MAX_BONES; i++)
            boneTransforms[i] = super.getUniformLocation("bones[" + i + "]");
    }
    
    public void loadSkyColour(float r, float g, float b) {
        super.loadVector(location_skyColour, new Vector3f(r,g,b));
    }
    
    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }
    
    public void loadLights(List<Light> lights) {
        for(int i = 0; i < MAX_LIGHTS; i++) {
            if(i<lights.size()) {
              super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
              super.loadVector(location_lightColour[i], lights.get(i).getColour());
              super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
            } else {
                super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
                super.loadVector(location_lightColour[i], new Vector3f(0,0,0));
                super.loadVector(location_attenuation[i], new Vector3f(1,0,0));
            }
        }
    }

    public void loadTransformationMatrix(Matrix4f matrix)
    {
        super.loadMatrix(this.transformationMatrix, matrix);
    }

    public void loadViewMatrix(CameraT camera)
    {
        super.loadMatrix(viewMatrix, Maths.createViewMatrix(camera));
    }

    public void loadProjectionMatrix(Matrix4f matrix)
    {
        super.loadMatrix(this.projectionMatrix, matrix);
    }

    public void loadBoneTransforms(Bone[] bones)
    {
        for (int i = 0; i < bones.length; i++)
            super.loadMatrix(this.boneTransforms[i], bones[i].getTransformation());
    }
    
    
}
