package entities;

import org.joml.Vector3f;

import animation.AnimatedModel;
import models.TexturedModel;
import textures.ModelTexture;

public class AnimatedEntity {
    
    private AnimatedModel animatedModel;
    private ModelTexture model;
    
    private Vector3f position;
    private Vector3f rotation;
    private float scale;
    
    public AnimatedEntity(AnimatedModel animatedModel, ModelTexture model, Vector3f position, Vector3f rotation, float scale) {
        this.model = model;
        this.animatedModel = animatedModel;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
    
    public void increasePosition(float dx, float dy, float dz) {
        this.position.x+=dx;
        this.position.y+=dy;
        this.position.z+=dz;
    }
    
    public void increaseRotation(float dx, float dy, float dz) {
        this.rotation.x += dx;
        this.rotation.y += dy;
        this.rotation.z += dz;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public AnimatedModel getAnimatedModel() {
        return animatedModel;
    }

    public ModelTexture getModel() {
        return model;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
    
    
    
    
    
    

}
