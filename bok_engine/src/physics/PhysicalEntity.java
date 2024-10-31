package physics;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import entities.EntityT;
import models.TexturedModel;

import org.joml.Matrix3f;
import org.joml.Quaternionf;

public class PhysicalEntity extends EntityT {
    private final RigidBody physicsBody;
    private float offset_y = 0;

    public PhysicalEntity(TexturedModel model, org.joml.Vector3f position, org.joml.Vector3f rotation, float scale) {
        super(model, position, rotation, scale);
        DefaultMotionState motion = new DefaultMotionState(new Transform(
                new javax.vecmath.Matrix4f(new javax.vecmath.Quat4f(0, 0, 0, 1), 
                new javax.vecmath.Vector3f(position.x, position.y, position.z), 1.0f)));
        CollisionShape shape = new BoxShape(new Vector3f(5,5,5));
        Vector3f inertia = new Vector3f(0,0,0);
        shape.calculateLocalInertia(1f, inertia);
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(0.1f, motion, shape, inertia);
        constructionInfo.restitution = 0.1f;
        this.physicsBody = new RigidBody(constructionInfo);
        this.physicsBody.setDamping(0.75f, 0.99f);
    }
    
    public PhysicalEntity(TexturedModel model, org.joml.Vector3f position, org.joml.Vector3f rotation, float scale, float mass, Vector3f gravity) {
        super(model, position, rotation, scale);
        DefaultMotionState motion = new DefaultMotionState(new Transform(
                new javax.vecmath.Matrix4f(new javax.vecmath.Quat4f(0, 0, 0, 1), 
                new javax.vecmath.Vector3f(position.x, position.y, position.z), 1.0f)));
        CollisionShape shape = new BoxShape(new Vector3f(5,5,5));
        this.physicsBody = new RigidBody(mass, motion, shape);
        this.physicsBody.setGravity(gravity);
    }
    
    public PhysicalEntity(TexturedModel model, org.joml.Vector3f position, org.joml.Vector3f rotation, float scale, CollisionShape shape, float offset_y) {
        super(model, position, rotation, scale);
        this.offset_y = offset_y;
        float mass = 0.0f;
        Vector3f gravity = new Vector3f(0,0,0);
        javax.vecmath.Quat4f body_rotation = eulerToQuaternion(
                rotation.x, 
                rotation.y, 
                rotation.z);
        DefaultMotionState motion = new DefaultMotionState(new Transform(
                new javax.vecmath.Matrix4f(body_rotation, 
                new javax.vecmath.Vector3f(position.x, position.y+offset_y, position.z), 1.0f)));
        this.physicsBody = new RigidBody(mass, motion, shape);
        this.physicsBody.setGravity(gravity);
    }
    
    public static Quat4f eulerToQuaternion(double roll, double pitch, double yaw) {
        // Convert degrees to radians
        roll = Math.toRadians(roll);
        pitch = Math.toRadians(pitch);
        yaw = Math.toRadians(yaw);

        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);

        double w = cr * cp * cy + sr * sp * sy;
        double x = sr * cp * cy - cr * sp * sy;
        double y = cr * sp * cy + sr * cp * sy;
        double z = cr * cp * sy - sr * sp * cy;

        return new Quat4f((float)x, (float)y, (float)z, (float)w);
    }
    
    /*
    public static javax.vecmath.Quat4f eulerToQuaternion(javax.vecmath.Vector3f euler) {
        // Create rotation matrices for each axis
        javax.vecmath.Matrix3f rotX = new javax.vecmath.Matrix3f();
        javax.vecmath.Matrix3f rotY = new javax.vecmath.Matrix3f();
        javax.vecmath.Matrix3f rotZ = new javax.vecmath.Matrix3f();
        
        // Set rotation for X axis (pitch)
        rotX.rotX(euler.x);
        
        // Set rotation for Y axis (yaw)
        rotY.rotY(euler.y);
        
        // Set rotation for Z axis (roll)
        rotZ.rotZ(euler.z);
        
        // Combine rotations (order: Z * Y * X)
        javax.vecmath.Matrix3f rotMatrix = new javax.vecmath.Matrix3f();
        rotMatrix.mul(rotZ, rotY);
        rotMatrix.mul(rotMatrix, rotX);
        
        // Convert to quaternion
        javax.vecmath.Quat4f quaternion = new javax.vecmath.Quat4f();
        quaternion.set(rotMatrix);
        
        return quaternion;
    }*/

    public RigidBody getPhysicsBody() {
        return physicsBody;
    }
    
    public float getOffsetY() {
        return offset_y;
    }
}
