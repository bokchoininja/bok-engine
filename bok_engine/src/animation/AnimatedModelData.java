package animation;

import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINode;

public class AnimatedModelData {
    private final int vao;
    private final int indicesLength;
    private final AIAnimation[] animations;
    private final AINode rootNode;

    public AnimatedModelData(int vao, int indicesLength, AIAnimation[] animations, AINode rootNode) {
        this.vao = vao;
        this.indicesLength = indicesLength;
        this.animations = animations;
        this.rootNode = rootNode;
    }

    public int getVao() {
        return vao;
    }

    public int getIndicesLength() {
        return indicesLength;
    }

    public AIAnimation[] getAnimations() {
        return animations;
    }

    public AINode getRootNode() {
        return rootNode;
    }
}