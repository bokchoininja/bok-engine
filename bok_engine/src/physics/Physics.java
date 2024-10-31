package physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import entities.AnimatedPhysicalEntity;
import entities.PlayerA;
import terrains.Terrain;

import toolbox.Maths;

/*
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
*/
import org.lwjglx.util.glu.GLU;
import org.lwjglx.util.glu.Sphere;
//import utility.EulerCamera;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
//import org.joml.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

public class Physics {
    private static DiscreteDynamicsWorld dynamicsWorld;
    private Set<RigidBody> bodies = new HashSet<RigidBody>();
    private List<PhysicalEntity> physical_entities = new ArrayList<PhysicalEntity>();
    private List<AnimatedPhysicalEntity> animated_physical_entities = new ArrayList<AnimatedPhysicalEntity>();
    private List<PhysicalEntity> static_physical_entities = new ArrayList<PhysicalEntity>();
    private PlayerA player;
    private RigidBody controlBall;
    private Terrain terrain;
    private int width = 100;
    private int length = 100;
    private BvhTriangleMeshShape terrainShape;
    float[] heightData = new float[width * length];
    
    public Physics(Terrain terrain) {
        this.terrain = terrain;
        setUpPhysics();
        create_terrain();
    }
    
    public void create_terrain() {
        float[] vertices = terrain.getVertices();
        int[] indices = terrain.getIndices();
        // Create an IndexedMesh
        IndexedMesh mesh = new IndexedMesh();
        mesh.numTriangles = indices.length / 3;
        mesh.numVertices = vertices.length / 3;
        // Set up vertex data
        mesh.vertexStride = 3 * 4; // 3 floats * 4 bytes per float
        mesh.vertexBase = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder());
        mesh.vertexBase.asFloatBuffer().put(vertices);
        // Set up index data
        mesh.triangleIndexStride = 3 * 4; // 3 ints * 4 bytes per int
        mesh.triangleIndexBase = ByteBuffer.allocateDirect(indices.length * 4)
                                           .order(ByteOrder.nativeOrder());
        mesh.triangleIndexBase.asIntBuffer().put(indices);
        // Create a TriangleIndexVertexArray
        TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray();
        indexVertexArrays.addIndexedMesh(mesh);
        // Create a BvhTriangleMeshShape
        terrainShape = new BvhTriangleMeshShape(indexVertexArrays, true);
        // Create a rigid body for the terrain
        Transform terrainTransform = new Transform();
        terrainTransform.setIdentity();
        DefaultMotionState myMotionState = new DefaultMotionState(terrainTransform);
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, myMotionState, terrainShape, new Vector3f(0, 0, 0));
        //RigidBody terrainBody = new RigidBody(rbInfo);
        RigidBody terrain_body = new RigidBody(rbInfo);
        // Add the terrain to the dynamics world
        dynamicsWorld.addRigidBody(terrain_body);
    }
    

    public void setUpPhysics() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        ConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, -50, 0));
    }
    
    public void logic() {
        dynamicsWorld.stepSimulation(1 / 60.0f);
        Set<RigidBody> bodiesToBeRemoved = new HashSet<RigidBody>();
        for (RigidBody body : bodies) {
            Vector3f position = body.getMotionState().getWorldTransform(new Transform()).origin;
            if (position.x < -50 || position.x > 50 || position.z < -50 || position.z > 50) {
                bodiesToBeRemoved.add(body);
            }
        }
        for (RigidBody body : bodiesToBeRemoved) {
            dynamicsWorld.removeRigidBody(body);
            bodies.remove(body);
        }
        
        for (PhysicalEntity physical_entity : physical_entities) {
            Transform transform = new Transform();
            
            physical_entity.getPhysicsBody().getMotionState().getWorldTransform(transform);
            
            physical_entity.setPosition(new org.joml.Vector3f(transform.origin.x,
                    transform.origin.y,
                    transform.origin.z));
            
            Matrix3f rotationMatrix = transform.basis;
            float pitch = (float)Math.asin(rotationMatrix.getElement(2, 1));
            float yaw = (float)Math.atan2(-rotationMatrix.getElement(2, 0), rotationMatrix.getElement(0, 0));
            float roll = (float)Math.atan2(rotationMatrix.getElement(1, 0), rotationMatrix.getElement(1, 1));
            physical_entity.setRotation(new org.joml.Vector3f(
                    (float)Math.toDegrees(pitch), 
                    (float)Math.toDegrees(yaw), 
                    (float)Math.toDegrees(roll)));
        }
        for (AnimatedPhysicalEntity entity : animated_physical_entities) {
            Transform transform = new Transform();
            entity.getPhysicsBody().getMotionState().getWorldTransform(transform);
            entity.setPosition(new org.joml.Vector3f(transform.origin.x,
                    transform.origin.y-4f,
                    transform.origin.z));
            
            Matrix3f rotationMatrix = transform.basis;
            float pitch = (float)Math.asin(rotationMatrix.getElement(2, 1));
            float yaw = (float)Math.atan2(-rotationMatrix.getElement(2, 0), rotationMatrix.getElement(0, 0));
            float roll = (float)Math.atan2(rotationMatrix.getElement(1, 0), rotationMatrix.getElement(1, 1));
            entity.setRotation(new org.joml.Vector3f(
                    (float)Math.toDegrees(pitch), 
                    (float)Math.toDegrees(yaw), 
                    (float)Math.toDegrees(roll)));
            entity.getPhysicsBody().applyCentralForce(new Vector3f(0, -20, 0));
        }
        Transform transform = new Transform();
        player.getPhysicsBody().getMotionState().getWorldTransform(transform);
        player.setPosition(new org.joml.Vector3f(transform.origin.x,
                transform.origin.y-4f,
                transform.origin.z));
        
        Matrix3f rotationMatrix = transform.basis;
        float pitch = (float)Math.asin(rotationMatrix.getElement(2, 1));
        float yaw = (float)Math.atan2(-rotationMatrix.getElement(2, 0), rotationMatrix.getElement(0, 0));
        float roll = (float)Math.atan2(rotationMatrix.getElement(1, 0), rotationMatrix.getElement(1, 1));
        player.setRotation(new org.joml.Vector3f(
                (float)Math.toDegrees(pitch), 
                (float)Math.toDegrees(yaw), 
                (float)Math.toDegrees(roll)));
        player.getPhysicsBody().applyCentralForce(new Vector3f(0, -20, 0));
        checkIsColliding();
    }
    
    public static Vector3f convertMatrixToVector(Matrix3f rotationMatrix) {
        // Extract the rotation angles from the rotation matrix
        double yaw = Math.atan2(rotationMatrix.getElement(1, 0), rotationMatrix.getElement(0, 0));
        double pitch = Math.asin(-rotationMatrix.getElement(2, 0));
        double roll = Math.atan2(rotationMatrix.getElement(2, 1), rotationMatrix.getElement(2, 2));

        // Create a Vector3d object representing the rotation angles
        Vector3f rotationVector = new Vector3f((float)yaw, (float)pitch, (float)roll);
        return rotationVector;
    }
    
    public void checkIsColliding() {
        if (isColliding(player.getPhysicsBody(), dynamicsWorld)) {
            player.setIsColliding(true);
        } else {
            player.setIsColliding(false);
        }
    }
    
    public boolean isColliding(RigidBody body, DynamicsWorld dynamicsWorld) {
        int numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();
        
        for (int i = 0; i < numManifolds; i++) {
            PersistentManifold contactManifold = (PersistentManifold) dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);
            
            CollisionObject obj0 = (CollisionObject) contactManifold.getBody0();
            CollisionObject obj1 = (CollisionObject) contactManifold.getBody1();
            
            // Check if our body is involved in this collision
            if (obj0 == body || obj1 == body) {
                // Check number of contacts in this manifold
                int numContacts = contactManifold.getNumContacts();
                if (numContacts > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void add_player(PlayerA player) {
        this.player = player;
        dynamicsWorld.addRigidBody(player.getPhysicsBody());
    }
    
    public void add_physical_entity(AnimatedPhysicalEntity physical_entity) {
        animated_physical_entities.add(physical_entity);
        dynamicsWorld.addRigidBody(physical_entity.getPhysicsBody());
    }

    public void add_physical_entity(PhysicalEntity physical_entity) {
        physical_entities.add(physical_entity);
        dynamicsWorld.addRigidBody(physical_entity.getPhysicsBody());
    }
    
    public void add_static_physical_entity(PhysicalEntity physical_entity) {
        static_physical_entities.add(physical_entity);
        dynamicsWorld.addRigidBody(physical_entity.getPhysicsBody());
    }
    
    public void add_rigid_body(RigidBody rigid_body) {
        dynamicsWorld.addRigidBody(rigid_body);
    }
    
    public static DynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }
    
    public List<PhysicalEntity> get_physical_entities() {
        return physical_entities;
    }
    
    public List<PhysicalEntity> getStaticPhysicalEntities() {
        return static_physical_entities;
    }
}
