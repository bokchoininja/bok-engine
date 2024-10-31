package entities;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import animation.AnimatedModel;
import animation.AnimationElement;
import models.TexturedModel;
import textures.ModelTexture;

public class AnimatedPhysicalEntity extends AnimatedEntity {
    
    protected boolean is_colliding = false;
    protected RigidBody physicsBody;

    protected List<AnimationElement> animation_list = new ArrayList<AnimationElement>();
    
    public AnimatedPhysicalEntity(AnimatedModel animatedModel, ModelTexture model, Vector3f position, Vector3f rotation, float scale) {
        super(animatedModel, model, position, rotation, scale);
    }
    
    public RigidBody getPhysicsBody() {
        return physicsBody;
    }
    
    public void setIsColliding(boolean is_colliding) {
        this.is_colliding = is_colliding;
    }

    public boolean getIsColliding() {
        return is_colliding;
    }
    
    
    
}
