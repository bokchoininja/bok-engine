package renderEngine;

import org.joml.Matrix4f;

import animation.AnimatedModel;
import entities.AnimatedEntity;
import entities.EntityT;
import shaders.AnimationShader;
import toolbox.Maths;

import static org.lwjgl.opengl.GL30.*;

public class AnimationRenderer
{
    private AnimationShader shader;

    public AnimationRenderer(AnimationShader animationShader, Matrix4f projectionMatrix)
    {
        this.shader = animationShader;
        shader.start();
        setProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(AnimatedEntity animatedEntity)
    {
        AnimatedModel model = animatedEntity.getAnimatedModel();
        shader.loadBoneTransforms(model.getBones());
        
        shader.loadShineVariables(10, 1);
        
        glBindVertexArray(model.getVaoID());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getTextureID());

        glDrawElements(GL_TRIANGLES, model.getCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix)
    {
        shader.loadProjectionMatrix(projectionMatrix);
    }
    
    public void setTransformationMatrix(AnimatedEntity animatedEntity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(animatedEntity.getPosition(), 
                animatedEntity.getRotation().x, animatedEntity.getRotation().y, animatedEntity.getRotation().z, animatedEntity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
