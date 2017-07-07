package org.andresoviedo.app.model3D.view;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;

import org.andresoviedo.app.model3D.controller.TouchController;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author：Administrator on 2017/7/6 11:24
 * company: xxxx
 * email：1032324589@qq.com
 */

public class TestActivity extends Activity{
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private float[] mProjectionMatrix = new float[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ModelTestSurfaceView(this));

    }
    public class ModelTestSurfaceView extends GLSurfaceView {

        private TestActivity parent;
        private ModelTestRenderer mRenderer;
        private TouchController touchHandler;

        public ModelTestSurfaceView(TestActivity parent) {
            super(parent);

            // parent component
            this.parent = parent;

            // Create an OpenGL ES 2.0 context.
            setEGLContextClientVersion(2);

            // This is the actual renderer of the 3D space
            mRenderer = new ModelTestRenderer(this);
            setRenderer(mRenderer);

            // Render the view only when there is a change in the drawing data
            // TODO: enable this again
            // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

            //touchHandler = new TouchController(this, mRenderer);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }

        public TestActivity getModelActivity() {
            return parent;
        }

        public ModelTestRenderer getRenderer() {
            return mRenderer;
        }
    }
    public class ModelTestRenderer implements GLSurfaceView.Renderer {
        private final float[] modelViewMatrix = new float[16];
        float[] vertexArray = new float[]{
                -0.8f , -0.4f * 1.732f , 0.0f ,
                0.8f , -0.4f * 1.732f , 0.0f ,
                0.0f , 0.4f * 1.732f , 0.0f ,
        };
        private static final String VERTEX_SHADER = "attribute vec4 vPosition;\n"
                + "void main() {\n"
                + "  gl_Position = vPosition;\n"
                + "}";
        private static final String FRAGMENT_SHADER = "precision mediump float;\n"
                + "void main() {\n"
                + "  gl_FragColor = vec4(0.5,0,0,1);\n"
                + "}";
        public ModelTestRenderer(ModelTestSurfaceView modelTestSurfaceView) {

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//            GLES20.glClearColor(0.6f,0.6f,0.3f,1.0f);
            // Enable depth testing for hidden-surface elimination.
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            // Enable blending for combining colors when there is transparency
            GLES20.glEnable(GLES20.GL_BLEND);

            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // Adjust the viewport based on geometry changes, such as screen rotation
            GLES20.glViewport(0, 0, width, height);

            // INFO: Set the camera position (View matrix)
            // The camera has 3 vectors (the position, the vector where we are looking at, and the up position (sky)
            Matrix.setLookAtM(modelViewMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);

            float ratio = (float) width / height;
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

//            mProgram = GLES20.glCreateProgram();
//            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
//            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
//            GLES20.glAttachShader(mProgram, vertexShader);
//            GLES20.glAttachShader(mProgram, fragmentShader);
//            GLES20.glLinkProgram(mProgram);
//
//            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClearColor(0.6f,0.6f,0.3f,1.0f);
            // Draw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

//            ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length*4);
//            vbb.order(ByteOrder.nativeOrder());
//            FloatBuffer vertex = vbb.asFloatBuffer();
//            vertex.put(vertexArray);
//            vertex.position(0);
//
//            GLES20.glUseProgram(mProgram);
//
//            GLES20.glEnableVertexAttribArray(mPositionHandle);
//            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertex);
//
//            GLES20.glDrawArrays(GLES20.GL_LINES, 0, 3);
//
//            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
    static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
