package texture;

import org.joml.Vector3f;

public class Material
{
	private TextureT texture;

	private Vector3f ambient;
	private Vector3f diffuse;
	private Vector3f specular;
	private float shininess;

	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;

	public Material(TextureT texture)
	{
		this(texture, new Vector3f(1), new Vector3f(1), new Vector3f(0), 10);
	}

	public Material(TextureT texture, Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess)
	{
		this.texture = texture;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
	}

	public boolean isUseFakeLighting()
	{
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting)
	{
		this.useFakeLighting = useFakeLighting;
	}

	public boolean isHasTransparency()
	{
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency)
	{
		this.hasTransparency = hasTransparency;
	}

	public Vector3f getAmbient()
	{
		return ambient;
	}

	public void setAmbient(Vector3f ambient)
	{
		this.ambient = ambient;
	}

	public Vector3f getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(Vector3f diffuse)
	{
		this.diffuse = diffuse;
	}

	public Vector3f getSpecular()
	{
		return specular;
	}

	public void setSpecular(Vector3f specular)
	{
		this.specular = specular;
	}

	public float getShininess()
	{
		return shininess;
	}

	public void setShininess(float shininess)
	{
		this.shininess = shininess;
	}

	public TextureT getTexture()
	{
		return texture;
	}
}
