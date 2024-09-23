package animation;

import org.joml.Matrix4f;

public class Bone
{
    private String name;
    private final Matrix4f offsetMatrix;
    private Matrix4f transformation;

    public Bone(String name, Matrix4f offsetMatrix)
    {
        this.name = name;
        this.offsetMatrix = offsetMatrix;
    }

    public String getName()
    {
        return name;
    }

    public Matrix4f getOffsetMatrix()
    {
        return offsetMatrix;
    }

    public Matrix4f getTransformation()
    {
        return transformation;
    }

    public void setTransformation(Matrix4f transformation)
    {
        this.transformation = transformation;
    }
}
