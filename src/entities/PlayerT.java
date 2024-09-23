package entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.io.Input;
import engine.io.Window;
import models.TexturedModel;
import terrains.Terrain;

public class PlayerT extends EntityT {
	
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 100;
	private static float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	private static final float TERRAIN_HEIGHT = 0;
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;
	
	public PlayerT(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
		super(model, position, rotation, scale);
	}

	public void move(Terrain terrain) {
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
		}else if(Input.isKeyDown(GLFW.GLFW_KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
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
}
