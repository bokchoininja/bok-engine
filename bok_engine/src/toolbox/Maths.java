package toolbox;

import javax.vecmath.Matrix3f;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIVector3D;
//import org.sopiro.game.entities.Camera;

import entities.CameraT;

public class Maths {
    
    
    
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(new Vector3f(translation.x, translation.y, 0), matrix);
        matrix.scale(new Vector3f(scale.x, scale.y, 1), matrix);
        return matrix;
    }
    
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(translation, matrix);
        matrix.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix);
        matrix.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix);
        matrix.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix);
        matrix.scale(new Vector3f(scale,scale,scale), matrix);
        return matrix;
    }
    
    
    public static Matrix4f createViewMatrix(CameraT camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        viewMatrix.translate(negativeCameraPos, viewMatrix);
        return viewMatrix;
    }
    
    
    /*
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale)
    {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(translation.x, translation.y, 0);
        matrix.scaleXY(scale.x, scale.y);

        return matrix;
    }
*/
    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale)
    {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(translation);
        //rotation = new Vector3f(rotation).mul((float) (Math.PI / 180));
        //matrix.rotateXYZ(rotation);
        matrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1,0,0), matrix);
        matrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(0,1,0), matrix);
        matrix.rotate((float) Math.toRadians(rotation.z), new Vector3f(0,0,1), matrix);
        matrix.scale(scale);

        return matrix;
    }
    
    /*
    public static Matrix4f createViewMatrixS(CameraT camera)
    {
        Matrix4f matrix = new Matrix4f();
        Vector3f negative = new Vector3f(camera.getRotation()).mul(-1);
        //Vector3f negative = new Vector3f(new Vector3f((float)Math.toRadians(camera.getPitch()),
                //(float)Math.toRadians(camera.getYaw()),
                //0));
        matrix.rotateXYZ(negative);
        negative = new Vector3f(camera.getPosition()).mul(-1);
        matrix.translate(negative);

        // matrix = new Matrix4f().lookAt(new Vector3f(-300, 300, 300), new
        // Vector3f(0), new Vector3f(0, 1, 0));
//      matrix = new Matrix4f().lookAlong(new Vector3f(300, -300, -300), new Vector3f(0,1,0));
//      matrix.translate(negative);

//      matrix = new Matrix4f().lookAt(Maths.sum(camera.getPosition(), new Vector3f(-300, 300, 300).mul(-1)), camera.getPosition(), new Vector3f(0, 1, 0));

        return matrix;
    }*/
    

    public static Vector3f mul(Vector3f a, float b)
    {
        return copy(a).mul(b);
    }

    public static Vector3f sum(Vector3f... sequence)
    {
        Vector3f res = new Vector3f();

        for (Vector3f v : sequence)
            res.add(v);

        return res;
    }

    public static Vector2f sum(Vector2f... sequence)
    {
        Vector2f res = new Vector2f();

        for (Vector2f v : sequence)
            res.add(v);

        return res;
    }

    public static Vector3f sub(Vector3f... sequence)
    {
        assert sequence.length > 0;

        Vector3f res = new Vector3f(sequence[0]);

        for (int i = 1; i < sequence.length; i++)
            res.sub(sequence[i]);

        return res;
    }

    public static Vector2f sub(Vector2f... sequence)
    {
        assert sequence.length > 0;

        Vector2f res = new Vector2f(sequence[0]);

        for (int i = 1; i < sequence.length; i++)
            res.sub(sequence[i]);

        return res;
    }

    public static Matrix4f mul(Matrix4f... sequence)
    {
        Matrix4f res = new Matrix4f();

        for (Matrix4f m : sequence)
            res.mul(m);

        return res;
    }

    public static Vector3f copy(Vector3f c)
    {
        return new Vector3f(c);
    }

    public static Vector4f mul(Matrix4f left, Vector4f right)
    {
        return right.mul(left);
    }

    public static Vector3f mul(Matrix4f left, Vector3f right)
    {
        return convert(mul(left, convert(right)));
    }

    public static Matrix4f invert(Matrix4f original)
    {
        return new Matrix4f(original).invert();
    }

    public static Vector3f convert(Vector4f v)
    {
        return new Vector3f(v.x(), v.y(), v.z());
    }

    public static Vector4f convert(Vector3f v)
    {
        return new Vector4f(v.x(), v.y(), v.z(), 1);
    }

    public static void mul(float[] array, float s)
    {
        for (int i = 0; i < array.length; i++)
            array[i] *= s;
    }

    public static Matrix4f convertMatrix(AIMatrix4x4 assimp)
    {
//        return new Matrix4f(
//                assimp.a1(), assimp.a2(), assimp.a3(), assimp.a4(),
//                assimp.b1(), assimp.b2(), assimp.b3(), assimp.b4(),
//                assimp.c1(), assimp.c2(), assimp.c3(), assimp.c4(),
//                assimp.d1(), assimp.d2(), assimp.d3(), assimp.d4()
//        );
        return new Matrix4f(
                assimp.a1(), assimp.b1(), assimp.c1(), assimp.d1(),
                assimp.a2(), assimp.b2(), assimp.c2(), assimp.d2(),
                assimp.a3(), assimp.b3(), assimp.c3(), assimp.d3(),
                assimp.a4(), assimp.b4(), assimp.c4(), assimp.d4()
        );
    }

    public static Vector3f convertVector(AIVector3D assimp)
    {
        return new Vector3f(assimp.x(), assimp.y(), assimp.z());
    }

    public static Quaternionf convertQuaternion(AIQuaternion assimp)
    {
        return new Quaternionf(assimp.x(), assimp.y(), assimp.z(), assimp.w());
    }

    public static Quaternionf slerp(Quaternionf start, Quaternionf end, float alpha)
    {
        Quaternionf a = new Quaternionf(start);
        Quaternionf b = new Quaternionf(end);

        a.slerp(b, alpha);

        return a;
    }
}
