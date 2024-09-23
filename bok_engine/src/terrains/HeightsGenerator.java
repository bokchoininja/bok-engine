package terrains;

import java.util.Random;

public class HeightsGenerator {
    
    private static final float AMPLITUDE = 10f;
    
    
    private Random random = new Random();
    private int seed;
    private int xOffset = 100000;
    private int zOffset = 100000;
    
    public HeightsGenerator(int seed) {
        this.seed = seed;
    }
    
    /*
    public float generateHeight(int x, int z) {
        float total = getInterpolatedNoise((x+xOffset)/32f, (z+zOffset)/32f, 1) * AMPLITUDE;
        //total += getInterpolatedNoise((x+xOffset)/16f, (z+zOffset)/16f, 10) * AMPLITUDE;
        return total;
    }
    */
    
    /*
    public void generateHeight(int[][] heights) {
        for (int i = 0; i < heights.length; i++) {
            for (int j = 0; j < heights[0].length; j++) {
                //heights[i][j] = (int) (getInterpolatedNoise((i+xOffset)/32f, (j+zOffset)/32f, 1) * AMPLITUDE);
                heights[i][j] = (int) getNoise(i + xOffset, j + zOffset);
            }
        }
    }
    */
    
    public int generateHeight(int x, int z, float[][] randomNumbers) {
        return (int) (getInterpolatedNoise(x/50f, z/50f, randomNumbers) * AMPLITUDE);
    }
    
    public int generateMountains(int x, int z, float[][] randomNumbers) {
        return (int) (getInterpolatedNoise(x/50f, z/50f, randomNumbers) * AMPLITUDE * 2);
    }
    
    /*
    public void generateHeight(int[][] heights, float[][] randomNumbers) {
        
        for (int i = 0; i < heights.length; i++) {
            for (int j = 0; j < heights[0].length; j++) {
                heights[i][j] = (int) (getInterpolatedNoise(i/50f, j/50f, randomNumbers) * AMPLITUDE);
            }
        }
    }
    */
    
    public void generateRandomNumbers(float[][] randomNumbers) {
        for (int i = 0; i < randomNumbers.length; i++) {
            for (int j = 0; j < randomNumbers.length; j++) {
                randomNumbers[i][j] = getNoise((i + xOffset), j + zOffset);
            }
        }
    }
    
    public void generateRandomNumbersRocks(float[][] randomNumbers) {
        for (int i = 0; i < randomNumbers.length; i++) {
            for (int j = 0; j < randomNumbers.length; j++) {
                randomNumbers[i][j] = getNoiseRocks((i + xOffset), j + zOffset);
            }
        }
    }
    
    /*
    public float generateHeightOctaves(int x, int z) {
        float total = 0;
        float d = (float) Math.pow(2, OCTAVES-1);
        for(int i=0;i<OCTAVES;i++){
            float freq = (float) (Math.pow(2, i) / d);
            float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
            total += getInterpolatedNoise((x+xOffset)*freq, (z + zOffset)*freq) * amp;
        }
        return total;
    }*/
    
    private float getInterpolatedNoise(float x, float z, float[][] randomNumbers) {
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;
        
        float v1 = getSmoothNoise(intX, intZ, randomNumbers);
        float v2 = getSmoothNoise(intX + 1, intZ, randomNumbers);
        float v3 = getSmoothNoise(intX, intZ + 1, randomNumbers);
        float v4 = getSmoothNoise(intX + 1, intZ + 1, randomNumbers);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);
        return interpolate(i1,i2,fracZ);
    }
    
    /*
    private float getInterpolatedLinear(float x, float z, float[][] randomNumbers) {
        int xWhole = (int) x;
        int zWhole = (int) z;
        float lin1 = interpolate(x, randomNumbers[xWhole][zWhole], randomNumbers[xWhole][zWhole]);
        float lin2 = interpolate(z, randomNumbers[xWhole][zWhole], randomNumbers[xWhole][zWhole]);
        
        float linResult = interpolate()
    }
    */
    
    private float interpolate(float a, float b, float blend) {
        double theta = blend * Math.PI;
        float f = (float)((1f - Math.cos(theta)) * 0.5f);
        return a * (1f-f) + b*f;
    }
    
    /*
    private float interpolateLinear(float x, float a, float b) {
        return (1f-x) * a + x * b;
    }
    */
    
    /*
    private float getSmoothNoise(int x, int z) {
        float corners = (getNoise(x - 1, z - 1) +
                getNoise(x + 1, z - 1) + 
                getNoise(x - 1, z + 1) + 
                getNoise(x + 1, z + 1))/16f;
        float sides = (getNoise(x-1, z)+
                getNoise(x+1, z)+
                getNoise(x, z-1)+
                getNoise(x, z+1))/8f;
        float center = getNoise(x,z)/4f;
        return corners + sides + center;
    }
    */
    
    private float getSmoothNoise(int x, int z, float[][] randomNumbers) {
        int offset = 1;
        x += offset;
        z += offset;
        float corners = (randomNumbers[x-1][z-1] +
                randomNumbers[x+1][z-1] +
                randomNumbers[x-1][z+1] +
                randomNumbers[x+1][z+1])/4f;
        float sides = (randomNumbers[x-1][z] +
                randomNumbers[x+1][z] +
                randomNumbers[x][z-1] +
                randomNumbers[x][z+1])/4f;
        float center = randomNumbers[x][z]/1f;
        return corners + sides + center;
    }
    //int counter = 0;
    
    public float getNoise(int x, int z) {
        random.setSeed(x * 5983743 + z * 7123532 + seed);
        float result = random.nextFloat() * 2f - 1f;
        
        /*
        if (random.nextFloat() > 0.99) {
            counter = 20;
        }
        if (counter > 1) {
            counter--;
            result = 0.9f;
        }
        */
        
        return result;
    }
    
    public float getNoiseRocks(int x, int z) {
        random.setSeed(x * 3847264 + z * 9925173 + seed);
        float result = random.nextFloat() * 2f - 1f;
        
        
        return result;
    }
}
