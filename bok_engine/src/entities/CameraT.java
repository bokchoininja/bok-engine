package entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.io.Input;
import engine.io.Window;

public class CameraT {
	
	private float distanceFromPlayer = 40;
	private float angleAroundPlayer = 0;
    
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch = 20;
    private float yaw = 0;
    private float roll;
    
    private PlayerA player;
    
    public CameraT(PlayerA player) {
    	this.player = player;
    }
    
    public void move() {
    	//calculateZoom();
    	calculatePitch();
    	calculateAngleAroundPlayer();
    	float horizontalDistance = calculateHorizontalDistance();
    	float verticalDistance = calculateVerticalDistance();
    	calculateCameraPosition(horizontalDistance, verticalDistance);
    	this.yaw = 180 - (player.getRotation().y + angleAroundPlayer);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
    
    private void calculateCameraPosition(float horizDistance, float verticDistance) {
    	float theta = player.getRotation().y + angleAroundPlayer;
    	float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
    	float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
    	position.x = player.getPosition().x - offsetX;
    	position.z = player.getPosition().z - offsetZ;
    	position.y = player.getPosition().y + verticDistance;
    }
    
    private float calculateHorizontalDistance() {
    	return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }
    
    private float calculateVerticalDistance() {
    	return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)) + 3);
    }
    
    /*
    private void calculateZoom() {
    	float zoomLevel = Mouse.getDWheel() * 0.1f;
    	distanceFromPlayer -= zoomLevel;
    }*/
    
    private void calculatePitch() {
    	if(Input.isButtonDown(1)) {
    		double pitchChange = Window.getMouseDY() * 0.1f;
    		pitch -= pitchChange;
    	}
    }
    
    private void calculateAngleAroundPlayer() {
    	if(Input.isButtonDown(0)) {
    		double angleChange = Window.getMouseDX() * 0.3f;
    		angleAroundPlayer -= angleChange;
    	}
    }
    
    

}
