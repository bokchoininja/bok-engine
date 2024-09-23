package animation;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;

import toolbox.Maths;

public class AnimatedModel
{
    private final int vaoID;
    private int count;
    private int textureID;

    Bone[] bones;
    AIAnimation[] animations;
    AINode root;
    
    public AnimatedModel(int vaoID, int count, int textureID)
    {
        this.vaoID = vaoID;
        this.count = count;
        this.textureID = textureID;
    }

    public int getVaoID()
    {
        return vaoID;
    }

    public int getCount()
    {
        return count;
    }

    public int getTextureID()
    {
        return textureID;
    }
    
    public void updateAnimationBlended(int animationIndex, int animationIndex2, float time, float time2, float blend)
    {
        assert animationIndex >= 0 && animationIndex < animations.length;
        updateBoneTransformationBlended(time/60f, time2/60f, animationIndex, animationIndex2, blend);
    }

    public void updateAnimation(int animationIndex, float time)
    {
        assert animationIndex >= 0 && animationIndex < animations.length;
        updateBoneTransformation(time/60f, animationIndex);
    }
    
    private void updateBoneTransformationBlended(float timeInSeconds, float timeInSeconds2, int animationIndex, int animationIndex2, float blend) {
        Matrix4f identity = new Matrix4f();

        AIAnimation target = animations[animationIndex];
        AIAnimation target2 = animations[animationIndex2];

        float ticksPerSecond = target.mTicksPerSecond() != 0 ? (float) target.mTicksPerSecond() : 60.0f;
        float ticks = timeInSeconds * ticksPerSecond;
        float animationTime = (ticks % (float) target.mDuration());
        
        ticksPerSecond = target2.mTicksPerSecond() != 0 ? (float) target2.mTicksPerSecond() : 60.0f;
        ticks = timeInSeconds2 * ticksPerSecond;
        float animationTime2 = (ticks % (float) target2.mDuration());

        processNode(target, target2, animationTime, animationTime2, root, identity, blend);
    }

    private void updateBoneTransformation(float timeInSeconds, int animationIndex)
    {
        Matrix4f identity = new Matrix4f();

        AIAnimation target = animations[animationIndex];

        float ticksPerSecond = target.mTicksPerSecond() != 0 ? (float) target.mTicksPerSecond() : 60.0f;
        float ticks = timeInSeconds * ticksPerSecond;
        float animationTime = (ticks % (float) target.mDuration());

        processNode(target, animationTime, root, identity);
    }
    
    private void processNode(AIAnimation target, AIAnimation target2, float animationTime, float animationTime2, AINode node, 
            Matrix4f parentTransform, float blend)
    {
        String nodeName = node.mName().dataString();

        Matrix4f nodeTransform = Maths.convertMatrix(node.mTransformation());

        AINodeAnim boneAnimation = findBoneAnimation(target, nodeName);
        AINodeAnim boneAnimation2 = findBoneAnimation(target2, nodeName);

        // If this node refers bone (contains animation), Do interpolate transforms.
        if (boneAnimation != null)
        {
            Vector3f interpolatedScale = calcInterpolatedScale(animationTime, boneAnimation);
            Vector3f interpolatedScale2 = calcInterpolatedScale(animationTime2, boneAnimation2);
            Vector3f blendedScale = new Vector3f();
            interpolatedScale.lerp(interpolatedScale2, blend, blendedScale);
            Matrix4f scaleMatrix = new Matrix4f().scale(blendedScale);

            Quaternionf interpolatedRotation = calcInterpolatedRotation(animationTime, boneAnimation);
            Quaternionf interpolatedRotation2 = calcInterpolatedRotation(animationTime2, boneAnimation2);
            Quaternionf blendedRotation = new Quaternionf();
            interpolatedRotation.slerp(interpolatedRotation2, blend, blendedRotation);
            Matrix4f rotationMatrix = new Matrix4f().rotate(blendedRotation);

            Vector3f interpolatedPosition = calcInterpolatedPosition(animationTime, boneAnimation);
            Vector3f interpolatedPosition2 = calcInterpolatedPosition(animationTime2, boneAnimation2);
            Vector3f blendedPosition = new Vector3f();
            interpolatedPosition.lerp(interpolatedPosition2, blend, blendedPosition);
            Matrix4f translationMatrix = new Matrix4f().translate(blendedPosition);

            nodeTransform = Maths.mul(translationMatrix, rotationMatrix, scaleMatrix);
        }

        Matrix4f toGlobalSpace = Maths.mul(parentTransform, nodeTransform);

        Bone bone = findBone(nodeName);

        if (bone != null)
            bone.setTransformation(Maths.mul(toGlobalSpace, bone.getOffsetMatrix()));

        // Recursively process the child nodes
        for (int i = 0; i < node.mNumChildren(); i++)
        {
            AINode childNode = AINode.create(node.mChildren().get(i));
            processNode(target, target2, animationTime, animationTime2, childNode, toGlobalSpace, blend);
        }
    }

    private void processNode(AIAnimation target, float animationTime, AINode node, Matrix4f parentTransform)
    {
        String nodeName = node.mName().dataString();

        Matrix4f nodeTransform = Maths.convertMatrix(node.mTransformation());

        AINodeAnim boneAnimation = findBoneAnimation(target, nodeName);

        // If this node refers bone (contains animation), Do interpolate transforms.
        if (boneAnimation != null)
        {
            Vector3f interpolatedScale = calcInterpolatedScale(animationTime, boneAnimation);
            Matrix4f scaleMatrix = new Matrix4f().scale(interpolatedScale);

            Quaternionf interpolatedRotation = calcInterpolatedRotation(animationTime, boneAnimation);
            Matrix4f rotationMatrix = new Matrix4f().rotate(interpolatedRotation);

            Vector3f interpolatedPosition = calcInterpolatedPosition(animationTime, boneAnimation);
            Matrix4f translationMatrix = new Matrix4f().translate(interpolatedPosition);

            nodeTransform = Maths.mul(translationMatrix, rotationMatrix, scaleMatrix);
        }

        Matrix4f toGlobalSpace = Maths.mul(parentTransform, nodeTransform);

        Bone bone = findBone(nodeName);

        if (bone != null)
            bone.setTransformation(Maths.mul(toGlobalSpace, bone.getOffsetMatrix()));

        // Recursively process the child nodes
        for (int i = 0; i < node.mNumChildren(); i++)
        {
            AINode childNode = AINode.create(node.mChildren().get(i));
            processNode(target, animationTime, childNode, toGlobalSpace);
        }
    }

    // Each node has a name. If that node is bone, the node name equals to bone name.
    private AINodeAnim findBoneAnimation(AIAnimation target, String nodeName)
    {
        for (int i = 0; i < target.mNumChannels(); i++)
        {
            AINodeAnim nodeAnim = AINodeAnim.create(target.mChannels().get(i));

            if (nodeAnim.mNodeName().dataString().equals(nodeName))
                return nodeAnim;
        }

        return null;
    }

    private Vector3f calcInterpolatedScale(float timeAt, AINodeAnim boneAnimation)
    {
        if (boneAnimation.mNumScalingKeys() == 1)
            return Maths.convertVector(boneAnimation.mScalingKeys().get(0).mValue());

        int index0 = findScaleIndex(timeAt, boneAnimation);
        int index1 = index0 + 1;
        float time0 = (float) boneAnimation.mScalingKeys().get(index0).mTime();
        float time1 = (float) boneAnimation.mScalingKeys().get(index1).mTime();
        float deltaTime = time1 - time0;
        float percentage = (timeAt - time0) / deltaTime;

        Vector3f start = Maths.convertVector(boneAnimation.mScalingKeys().get(index0).mValue());
        Vector3f end = Maths.convertVector(boneAnimation.mScalingKeys().get(index1).mValue());
        Vector3f delta = Maths.sub(end, start);

        return Maths.sum(start, delta.mul(percentage));
    }

    private int findScaleIndex(float timeAt, AINodeAnim boneAnimation)
    {
        assert boneAnimation.mNumScalingKeys() > 0;

        for (int i = 0; i < boneAnimation.mNumScalingKeys() - 1; i++)
        {
            if (timeAt < boneAnimation.mScalingKeys().get(i + 1).mTime())
                return i;
        }

        return 0;
    }

    private Quaternionf calcInterpolatedRotation(float timeAt, AINodeAnim boneAnimation)
    {
        if (boneAnimation.mNumRotationKeys() == 1)
            return Maths.convertQuaternion(boneAnimation.mRotationKeys().get(0).mValue());

        int index0 = findRotationIndex(timeAt, boneAnimation);
        int index1 = index0 + 1;
        float time0 = (float) boneAnimation.mRotationKeys().get(index0).mTime();
        float time1 = (float) boneAnimation.mRotationKeys().get(index1).mTime();
        float deltaTime = time1 - time0;
        float percentage = (timeAt - time0) / deltaTime;

        Quaternionf start = Maths.convertQuaternion(boneAnimation.mRotationKeys().get(index0).mValue());
        Quaternionf end = Maths.convertQuaternion(boneAnimation.mRotationKeys().get(index1).mValue());

        return Maths.slerp(start, end, percentage);
    }

    private int findRotationIndex(float timeAt, AINodeAnim boneAnimation)
    {
        assert boneAnimation.mNumRotationKeys() > 0;

        for (int i = 0; i < boneAnimation.mNumRotationKeys() - 1; i++)
        {
            if (timeAt < boneAnimation.mRotationKeys().get(i + 1).mTime())
                return i;
        }

        return 0;
    }

    private Vector3f calcInterpolatedPosition(float timeAt, AINodeAnim boneAnimation)
    {
        if (boneAnimation.mNumPositionKeys() == 1)
            return Maths.convertVector(boneAnimation.mPositionKeys().get(0).mValue());

        int index0 = findPositionIndex(timeAt, boneAnimation);
        int index1 = index0 + 1;
        float time0 = (float) boneAnimation.mPositionKeys().get(index0).mTime();
        float time1 = (float) boneAnimation.mPositionKeys().get(index1).mTime();
        float deltaTime = time1 - time0;
        float percentage = (timeAt - time0) / deltaTime;

        Vector3f start = Maths.convertVector(boneAnimation.mPositionKeys().get(index0).mValue());
        Vector3f end = Maths.convertVector(boneAnimation.mPositionKeys().get(index1).mValue());
        Vector3f delta = end.sub(start);

        return Maths.sum(start, delta.mul(percentage));
    }

    private int findPositionIndex(float timeAt, AINodeAnim boneAnimation)
    {
        assert boneAnimation.mNumPositionKeys() > 0;

        for (int i = 0; i < boneAnimation.mNumPositionKeys() - 1; i++)
        {
            if (timeAt < boneAnimation.mPositionKeys().get(i + 1).mTime())
                return i;
        }

        return 0;
    }

    private Bone findBone(String nodeName)
    {
        for (Bone b : bones)
            if (b.getName().equals(nodeName))
                return b;

        return null;
    }

    public Bone[] getBones()
    {
        return bones;
    }

    public void setBones(Bone[] bones) {
        this.bones = bones;
    }

    public void setAnimations(AIAnimation[] animations) {
        this.animations = animations;
    }

    public void setRoot(AINode root) {
        this.root = root;
    }
    
    
    
    
}
