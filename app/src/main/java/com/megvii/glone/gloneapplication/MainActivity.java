package com.megvii.glone.gloneapplication;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity implements GLSurfaceView.Renderer{

    GLSurfaceView surfaceView;
    String vertexShader = "attribute vec4 vPosition;\n" +
            "uniform mat4 vMatrix;\n" +
            "void main() {\n" +
            "    gl_Position = vPosition;\n" +
            "}";
    String fragmentShade = "precision mediump float;\n" +
            "uniform vec4 vColor;\n" +
            "void main() {\n" +
            "    gl_FragColor = vColor;\n" +
            "}";

    float[] triangleCoords = {
            0f, 0.5f, 0.0f,//top
            -0.5f, 0f, 0.0f,//bottom left
            0.5f, 0f, 0.0f //bottom right
    };

    FloatBuffer vertexBuffer;
    float color[] = {1.0f, 0f, 0f, 1.0f};//白色
    int program;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new GLSurfaceView(this);
        setContentView(surfaceView);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //设置背景包为灰色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //申请底层空间
        ByteBuffer buffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        //将坐标转换为FloatBuffer，用以传给OpenglGL ES程序
        vertexBuffer = buffer.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        int verShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(verShader, vertexShader);
        GLES20.glCompileShader(verShader);

        try{

        }catch(Exception e){
            e.printStackTrace();
        }
        int fraShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fraShader, fragmentShade);
        GLES20.glCompileShader(fraShader);

        //创建一个空的OPENGL程序
        program = GLES20.glCreateProgram();
        //将顶点着色器加入到程序里,
        GLES20.glAttachShader(program, verShader);
        //将片元着色器加入到程序里
        GLES20.glAttachShader(program, fraShader);
        //连接到着色器程序
        GLES20.glLinkProgram(program);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES20.glViewport(0, 0, i, i1);

        float ratio = (float) i/i1;
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7, 0, 0, 0, 0, 1, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(program);

        int position = GLES20.glGetAttribLocation(program, "vPosition");

        GLES20.glEnableVertexAttribArray(position);

        GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        int colors = GLES20.glGetUniformLocation(program, "vColor");

        GLES20.glUniform4fv(colors, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        GLES20.glDisableVertexAttribArray(position);
    }
}
