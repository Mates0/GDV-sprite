package cz.educanet;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Square {
    private float[] vertices;
    private int squareColorId;
    private int textureId;
    private int squareEboId;
    private float size;
    private float x;
    private float y;
    private int speed;
    public Matrix4f matrix;
    public FloatBuffer matrixFloatBuffer;
    public int squareVaoId;
    public int squareVboId;
    public float[] textureIndices;
    public float frame;
    public FloatBuffer tb = BufferUtils.createFloatBuffer(8);
    public static int uniformMatrixLocation;

    private final int[] indices = {
            0, 1, 3,
            1, 2, 3
    };

    public void update() {
        speed = (int) frame % 6;
        switch (speed) {
            case 0 -> textureIndices = new float[]{
                    0.16f, 0.0f,
                    0.16f, 1f,
                    0.0f, 1f,
                    0.0f, 0.0f,
            };
        }
        switch (speed) {
            case 1 -> textureIndices = new float[]{
                    0.32f, 0.0f,
                    0.32f, 1f,
                    0.16f, 1f,
                    0.16f, 0.0f,
            };
        }
        switch (speed) {
            case 2 -> textureIndices = new float[]{
                    0.48f, 0.0f,
                    0.48f, 1f,
                    0.32f, 1f,
                    0.32f, 0.0f,
            };
        }
        switch (speed) {
            case 3 -> textureIndices = new float[]{
                    0.64f, 0.0f,
                    0.64f, 1f,
                    0.48f, 1f,
                    0.48f, 0.0f,
            };
        }
        switch (speed) {
            case 4 -> textureIndices = new float[]{
                    0.80f, 0.0f,
                    0.80f, 1f,
                    0.64f, 1f,
                    0.64f, 0.0f,
            };
        }
        switch (speed) {
            case 5 -> textureIndices = new float[]{
                    0.96f, 0.0f,
                    0.96f, 1f,
                    0.8f, 1f,
                    0.8f, 0f,
            };
        }
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureId);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, tb, GL33.GL_STATIC_DRAW);
        tb.clear().put(textureIndices).flip();
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);
        frame += 0.15;
    }

    public Square(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
        matrix = new Matrix4f().identity();
        matrixFloatBuffer = BufferUtils.createFloatBuffer(16);

        float[] vertices = {
                x + size, y, 0.0f,
                x + size, y - size, 0.0f,
                x, y - size, 0.0f,
                x, y, 0.0f,
        };

        textureIndices = new float[]{
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f
        };

        float[] colors = {
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
        };


        this.vertices = vertices;
        squareVboId = GL33.glGenBuffers();
        squareVaoId = GL33.glGenVertexArrays();
        squareEboId = GL33.glGenBuffers();
        squareColorId = GL33.glGenBuffers();
        textureId = GL33.glGenBuffers();
        loadImg();
        uniformMatrixLocation = GL33.glGetUniformLocation(Shaders.shaderProgramId, "matrix");
        GL33.glBindVertexArray(squareVaoId);
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, squareEboId);
        IntBuffer ib = BufferUtils.createIntBuffer(indices.length)
                .put(indices)
                .flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareColorId);
        FloatBuffer cfb = BufferUtils.createFloatBuffer(colors.length).put(colors).flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cfb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(1, 4, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(1);
        MemoryUtil.memFree(cfb);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareVboId);
        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length)
                .put(vertices)
                .flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);
        GL33.glUseProgram(Shaders.shaderProgramId);
        matrix.get(matrixFloatBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixFloatBuffer);
        MemoryUtil.memFree(fb);
        MemoryUtil.memFree(ib);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureId);
        tb.put(textureIndices).flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, tb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);
    }

    public void render() {
        matrix.get(matrixFloatBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixFloatBuffer);
        GL33.glUseProgram(Shaders.shaderProgramId);
        GL33.glBindVertexArray(squareVaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, indices.length, GL33.GL_UNSIGNED_INT, 0);
    }

    private void loadImg() {
        MemoryStack stack = MemoryStack.stackPush();
        IntBuffer width = stack.mallocInt(1);
        IntBuffer height = stack.mallocInt(1);
        IntBuffer comp = stack.mallocInt(1);
        ByteBuffer img = STBImage.stbi_load("resources/Cyborg_run.png", width, height, comp, 3);
        if (img != null) {
            img.flip();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, width.get(), height.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, img);
            GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
        }
    }
}
