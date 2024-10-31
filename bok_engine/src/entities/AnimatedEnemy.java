package entities;

import org.joml.Vector3f;

import animation.AnimatedModel;
import animation.AnimationElement;
import textures.ModelTexture;

public class AnimatedEnemy extends AnimatedPhysicalEntity {

    protected boolean agro = false;
    
    public AnimatedEnemy(AnimatedModel animatedModel, ModelTexture model, Vector3f position, Vector3f rotation, float scale) {
        super(animatedModel, model, position, rotation, scale);
    }
    
    public void setAgro(boolean agro) {
        this.agro = agro;
    }
    
    public boolean getAgro() {
        return agro;
    }
}
