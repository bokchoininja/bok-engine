package entities;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import animation.AnimatedModel;
import animation.AnimationElement;
import engine.io.Input;
import engine.io.Window;
import terrains.Terrain;
import textures.ModelTexture;

public class Zombie extends AnimatedEnemy {
    
    private final float RUN_SPEED = 15;
    private final float TURN_SPEED = 100;
    private float GRAVITY = -50;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private boolean isInAir = false;
    private boolean isMoving = false;
    private float idle_time = 0;
    private float run_time = 0;
    private int old_animation_index = 0;
    private int current_animation = 0;
    private int jump_timer = 0;
    private PlayerA player;
    
    public Zombie(AnimatedModel animatedModel, ModelTexture model, Vector3f position, Vector3f rotation, float scale, PlayerA player) {
        super(animatedModel, model, position, rotation, scale);
        DefaultMotionState motion = new DefaultMotionState(new Transform(
                new javax.vecmath.Matrix4f(new javax.vecmath.Quat4f(0, 0, 0, 1), 
                new javax.vecmath.Vector3f(position.x, position.y, position.z), 1.0f)));
        CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(1f,4,1f));
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(0.1f, motion, shape);
        this.physicsBody = new RigidBody(constructionInfo);
        physicsBody.setDamping(0.99f, 0.5f);
        animation_list.add(new AnimationElement(0,0));
        physicsBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        this.player = player;
    }
    
    private int look_around_counter = 0;

    public void move(Terrain terrain) {
        isMoving = false;
        checkIsMoving();
        old_animation_index = current_animation;
        //super.increaseRotation(0, currentTurnSpeed * Window.getFrameTimeSeconds(), 0);

        //super.increasePosition(dx, 0, dz);
        //upwardsSpeed += GRAVITY * Window.getFrameTimeSeconds();
        //super.increasePosition(0, upwardsSpeed*Window.getFrameTimeSeconds(), 0);
        
        Transform newTransform = new Transform();
        physicsBody.getWorldTransform(newTransform);
        //upwardsSpeed += GRAVITY * Window.getFrameTimeSeconds();
        
        //javax.vecmath.Quat4f currentRotation = new javax.vecmath.Quat4f();
        //newTransform.getRotation(currentRotation);
        
        Vector3f position_to_player = new Vector3f(player.getPosition());
        position_to_player.sub(super.getPosition());
        float angle_to_player;
        if (agro) {
            angle_to_player = (float)Math.atan2(position_to_player.x, position_to_player.z);
        } else {
            angle_to_player = 0;
        }
        javax.vecmath.Quat4f currentRotation = new javax.vecmath.Quat4f();
        
        currentRotation.set(new javax.vecmath.AxisAngle4f(0, 1, 0, angle_to_player));
        //currentRotation.mul(additionalRotation);
        //super.increasePosition(0, upwardsSpeed*Window.getFrameTimeSeconds(), 0);
        
        jump_timer++;
        
        float distance = currentSpeed * Window.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(angle_to_player));
        float dz = (float) (distance * Math.cos(angle_to_player));
        newTransform.origin.set(newTransform.origin.x+dx, newTransform.origin.y, newTransform.origin.z+dz);
        newTransform.setRotation(currentRotation);
        physicsBody.setWorldTransform(newTransform);
        
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if(super.getPosition().y < terrainHeight+0.2f || is_colliding) {
            if (jump_timer > 20) {
                upwardsSpeed = 0;
                isInAir = false;
                //super.getPosition().y = terrainHeight;
                //newTransform.origin.set(newTransform.origin.x, newTransform.origin.y, newTransform.origin.z);
            }
        }
        if (isMoving) {
            current_animation = 1;
            if(old_animation_index != current_animation) {
                animation_list.add(new AnimationElement(current_animation, 0));
            }
            run_time++;
        } else {
            current_animation = 0;
            if(old_animation_index != current_animation) {
                animation_list.add(new AnimationElement(current_animation, 0));
            }
            idle_time++;
        }
        if(isMoving) {
            idle_time = 0;
        } else {
            run_time = 0;
        }
        if (animation_list.size() == 1) {
            super.getAnimatedModel().updateAnimation(animation_list.get(0).getAnimationIndex(), 
                    animation_list.get(0).getAnimationTime());
            animation_list.get(0).incAnimationTime();
        } else if (animation_list.size() > 1) {
            super.getAnimatedModel().updateAnimationBlended(animation_list.get(0).getAnimationIndex(), 
                    animation_list.get(1).getAnimationIndex(),
                    animation_list.get(0).getAnimationTime(),
                    animation_list.get(1).getAnimationTime(),
                    animation_list.get(0).getBlendedTime()/10f);
            animation_list.get(0).incAnimationTime();
            animation_list.get(1).incAnimationTime();
            animation_list.get(0).incBlendedTime();
            if(animation_list.get(0).getBlendedTime()>=10) {
                animation_list.remove(0);
            }
            if(animation_list.size() > 2) {
                animation_list.get(0).incBlendedTime(5);
            }
        }
    }
    
    public void checkIsMoving() {
        if (agro) {
            isMoving = true;
            currentSpeed = RUN_SPEED;
        } else {
            currentSpeed = 0;
        }
    }

    public boolean isMoving() {
        return isMoving;
    }
    
}
