package renderer;

import org.lwjgl.assimp.*;

import models.RawModel;
import renderEngine.Loader;



public class AssimpLoader
{
    public static RawModel load(Loader loader, String fileName)
    {
        AIScene scene = Assimp.aiImportFile("./res/models/" + fileName,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_GenSmoothNormals |
                        Assimp.aiProcess_FlipUVs |
                        Assimp.aiProcess_CalcTangentSpace |
                        Assimp.aiProcess_OptimizeMeshes |
                        Assimp.aiProcess_JoinIdenticalVertices
        );

        assert scene != null;
        assert scene.mNumMeshes() == 1;
        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        final int vertexSize = 3 + 2 + 3 + 3;

        float[] vertices = new float[mesh.mNumVertices() * vertexSize];

        int i = 0;
        for (int v = 0; v < mesh.mNumVertices(); v++)
        {
            AIVector3D position = mesh.mVertices().get(v);
            AIVector3D tex = mesh.mTextureCoords(0).get(v);
            AIVector3D normal = mesh.mNormals().get(v);
            AIVector3D tangent = mesh.mTangents().get(v);

            vertices[i++] = position.x();
            vertices[i++] = position.y();
            vertices[i++] = position.z();

            vertices[i++] = tex.x();
            vertices[i++] = tex.y();

            vertices[i++] = normal.x();
            vertices[i++] = normal.y();
            vertices[i++] = normal.z();

            vertices[i++] = tangent.x();
            vertices[i++] = tangent.y();
            vertices[i++] = tangent.z();
        }

        int[] indices = new int[mesh.mNumFaces() * 3];

        i = 0;
        for (int f = 0; f < mesh.mNumFaces(); f++)
        {
            AIFace face = mesh.mFaces().get(f);

            indices[i++] = (face.mIndices().get(0));
            indices[i++] = (face.mIndices().get(1));
            indices[i++] = (face.mIndices().get(2));
        }

        //printNode(scene.mRootNode(), "");

        return loader.loadVAO(vertices, indices);
    }

    private static void printNode(AINode node, String tabs)
    {
        System.out.println(tabs + node.mName().dataString());

        for (int i = 0; i < node.mNumChildren(); i++)
            printNode(AINode.create(node.mChildren().get(i)), tabs + "    ");
    }
}

