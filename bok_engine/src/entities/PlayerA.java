package entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import animation.AnimatedModel;
import animation.AnimationElement;
import engine.io.Input;
import engine.io.Window;
import models.TexturedModel;
import terrains.Terrain;
import textures.ModelTexture;

public class PlayerA extends AnimatedEntity {
    
    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 100;
    private static float GRAVITY = -50;
    private static final float JUMP_POWER = 30;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private boolean isInAir = false;
    private boolean isMoving = false;
    private float idle_time = 0;
    private float run_time = 0;
    private float jump_time = 0;
    private float look_around_time = 0;
    private int old_animation_index = 0;
    private int current_animation = 0;
    private boolean isLooking = false;
    
    private List<AnimationElement> animation_list = new ArrayList<AnimationElement>();


    public PlayerA(AnimatedModel animatedModel, ModelTexture model, Vector3f position, Vector3f rotation, float scale) {
        super(animatedModel, model, position, rotation, scale);
        animation_list.add(new AnimationElement(0,0));
    }

    public void move(Terrain terrain) {
        isMoving = false;
        old_animation_index = current_animation;
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * Window.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * Window.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotation().y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotation().y)));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * Window.getFrameTimeSeconds();
        super.increasePosition(0, upwardsSpeed*Window.getFrameTimeSeconds(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if(super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
        if(isInAir) {
            if (jump_time < 20) {
                current_animation = 1;
                if(old_animation_index != current_animation) {
                    animation_list.add(new AnimationElement(current_animation, 0));
                }
                jump_time++;
            }
        } else if (isMoving) {
            current_animation = 3;
            if(old_animation_index != current_animation) {
                animation_list.add(new AnimationElement(current_animation, 0));
                
            }
            run_time++;
        } else if (!isLooking) {
            current_animation = 0;
            if(old_animation_index != current_animation) {
                animation_list.add(new AnimationElement(current_animation, 0));
            }
            idle_time++;
        } else {
            current_animation = 2;
            if(old_animation_index != current_animation) {
                animation_list.add(new AnimationElement(current_animation, 0));
            }
            look_around_time++;
        }
        if(!isInAir) {
            jump_time = 0;
        }
        if(isMoving) {
            idle_time = 0;
        } else {
            run_time = 0;
        }
        if(idle_time > 768) {
            isLooking = true;
            idle_time = 0;
        }
        if(look_around_time > 256) {
            isLooking = false;
            look_around_time = 0;
        }
        if (animation_list.size() == 1) {
            super.getAnimatedModel().updateAnimation(animation_list.get(0).getAnimationIndex(), 
                    animation_list.get(0).getAnimationTime());
            if (animation_list.get(0).getAnimationIndex() != 1 || animation_list.get(0).getAnimationTime() < 20) {
                animation_list.get(0).incAnimationTime();
            }
        } else if (animation_list.size() > 1) {
            super.getAnimatedModel().updateAnimationBlended(animation_list.get(0).getAnimationIndex(), 
                    animation_list.get(1).getAnimationIndex(),
                    animation_list.get(0).getAnimationTime(),
                    animation_list.get(1).getAnimationTime(),
                    animation_list.get(0).getBlendedTime()/20f);
            if (animation_list.get(0).getAnimationIndex() != 1 || animation_list.get(0).getAnimationTime() < 20) {
                animation_list.get(0).incAnimationTime();
            }
            if (animation_list.get(1).getAnimationIndex() != 1 || animation_list.get(1).getAnimationTime() < 20) {
                animation_list.get(1).incAnimationTime();
            }
            animation_list.get(0).incBlendedTime();
            if(animation_list.get(0).getBlendedTime()>=20) {
                animation_list.remove(0);
            }
            if(animation_list.size() > 2) {
                animation_list.get(0).incBlendedTime(5);
            }
        }
    }
    
    private void jump() {
        if(!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }
    
    private void checkInputs() {
        if(Input.isKeyDown(GLFW.GLFW_KEY_W)) {
            this.currentSpeed = RUN_SPEED;
            isMoving = true;
        }else if(Input.isKeyDown(GLFW.GLFW_KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
            isMoving = true;
        }else{
            this.currentSpeed = 0;
        }
        
        if(Input.isKeyDown(GLFW.GLFW_KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        }else if(Input.isKeyDown(GLFW.GLFW_KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        }else{
            this.currentTurnSpeed = 0;
        }
        
        if(Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
            jump();
        }
    }

    public boolean isMoving() {
        return isMoving;
    }
    
    

}
