package entities;


import org.joml.Vector3f;

import models.TexturedModel;

public class EntityT {

    private TexturedModel model;
    private Vector3f position;
    private Vector3f rotation;
    private float scale;
    
    private int textureIndex = 0;
    
    public EntityT(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
    
    public EntityT(TexturedModel model, int index, Vector3f position, Vector3f rotation, float scale) {
        this.textureIndex = index;
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
    
    public float getTextureXOffset() {
        int column = textureIndex%model.getTexture().getNumberOfRows();
        return (float)column/(float)model.getTexture().getNumberOfRows();
    }
    
    public float getTextureYOffset() {
        int row = textureIndex/model.getTexture().getNumberOfRows();
        return (float)row/(float)model.getTexture().getNumberOfRows();
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

    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }
    
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
    
    
    
    
}
