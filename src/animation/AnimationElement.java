package animation;

public class AnimationElement {

    private int animationIndex;
    private float animationTime;
    private float blendedTime;
    public AnimationElement(int animationIndex, float animationTime) {
        super();
        this.animationIndex = animationIndex;
        this.animationTime = animationTime;
    }
    
    public void incAnimationIndex() {
        animationIndex++;
    }
    
    public void incAnimationTime() {
        animationTime++;
    }
    
    public void incBlendedTime() {
        blendedTime++;
    }
    
    public void incBlendedTime(int inc) {
        blendedTime+=inc;
    }
    
    public void resetAnimationTime() {
        animationTime = 0;
    }
    
    public int getAnimationIndex() {
        return animationIndex;
    }
    public void setAnimationIndex(int animationIndex) {
        this.animationIndex = animationIndex;
    }
    public float getAnimationTime() {
        return animationTime;
    }
    public void setAnimationTime(float animationTime) {
        this.animationTime = animationTime;
    }
    public float getBlendedTime() {
        return blendedTime;
    }
    public void setBlendedTime(float blendedTime) {
        this.blendedTime = blendedTime;
    }
    
    
    
    
    
}
