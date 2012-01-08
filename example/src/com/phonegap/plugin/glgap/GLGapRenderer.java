// WebGLGap
// Copyright (c) 2012 Scirra Ltd
// Written by Ashley Gullen
package com.phonegap.plugin.glgap;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class GLGapRenderer implements GLSurfaceView.Renderer {
	
	public AssetManager amgr;
	int width;
	int height;
	float[] cam = {0, 0, 100};
	float[] look = {0, 0, 0};
	float[] up = {0, 1, 0};
	float[] worldScale = {1, 1, 1};
	float[] matP = new float[16];
	float[] matMV = new float[16];
	float[] lastMV = new float[16];
	
	float lastOpacity = 1;
	int lastTexture = 0;
	int lastSrcBlend = GLES20.GL_ONE;
	int lastDestBlend = GLES20.GL_ONE_MINUS_SRC_ALPHA;
	
	int vertexBuffer;
	int texcoordBuffer;
	int indexBuffer;
	
	int MAX_VERTICES = 8000;
	int MAX_INDICES = (MAX_VERTICES / 2) * 3;
	FloatBuffer vertexData = FloatBuffer.allocate(MAX_VERTICES * 2);
	ShortBuffer indexData = ShortBuffer.allocate(MAX_INDICES);
	FloatBuffer texcoordData = FloatBuffer.allocate(MAX_VERTICES * 2);
	
	String fragSrc = "#ifdef GL_ES\n" +
			"precision mediump float;\n" +
			"#endif\n" +
			"varying vec2 vTex;\n" +
			"uniform float opacity;\n" +
			"uniform sampler2D sampler;\n" +

			"void main(void) {\n" +
			"	gl_FragColor = texture2D(sampler, vTex);\n" +
			"	gl_FragColor *= opacity;\n" +
			"}";
	
	String vsSrc = "attribute vec2 aPos;\n" +
			"attribute vec2 aTex;\n" +
			"varying vec2 vTex;\n" +
			"uniform mat4 matP;\n" +
			"uniform mat4 matMV;\n" +

			"void main(void) {\n" +
			"	gl_Position = matP * matMV * vec4(aPos.x, aPos.y, 0.0, 1.0);\n" +
			"	vTex = aTex;\n" +
			"}";
	
	int fragmentShader;
	int vertexShader;
	int shaderProgram;
	
	int locAPos;
	int locATex;
	int locMatP;
	int locMatMV;
	int locOpacity;
	int locSampler;
	
	public HashMap<Integer, Integer> textureIDs = new HashMap<Integer, Integer>();
	public String renderCommand;
	public Lock renderLock = new ReentrantLock();
	public Condition renderedCondition = renderLock.newCondition();
	public boolean rendered = true;
	
	public interface IBatch {
		public void run();
		
		public boolean isQuads();
	}
	
	public class BatchUpdateModelView implements IBatch {
		float[] m;
		
		public BatchUpdateModelView(float[] m_) {
			m = m_.clone();
		}
		
		public boolean isQuads() { return false; }
		
		public void run() {
			GLES20.glUniformMatrix4fv(locMatMV, 1, false, m, 0);
		}
	}
	
	public class BatchSetTexture implements IBatch {
		int tex;
		
		public BatchSetTexture(int tex_) {
			tex = tex_;
		}
		
		public boolean isQuads() { return false; }
		
		public void run() {
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs.get(tex));
		}
	}
	
	public class BatchSetOpacity implements IBatch {
		float opacity;
		
		public BatchSetOpacity(float opacity_) {
			opacity = opacity_;
		}
		
		public boolean isQuads() { return false; }
		
		public void run() {
			GLES20.glUniform1f(locOpacity, opacity);
		}
	}
	
	public class BatchQuad implements IBatch {
		public int indexCount;
		public int startIndex;
		
		public BatchQuad(int startIndex_, int indexCount_) {
			indexCount = indexCount_;
			startIndex = startIndex_;
		}
		
		public boolean isQuads() { return true; }
		
		public void run() {
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, startIndex * 2);
		}
	}
	
	public class BatchSetBlend implements IBatch {
		int src;
		int dest;
		
		public BatchSetBlend(int src_, int dest_) {
			src = src_;
			dest = dest_;
		}
		
		public boolean isQuads() { return false; }
		
		public void run() {
			GLES20.glBlendFunc(src, dest);
		}
	}
	
	public class BatchClear implements IBatch {
		float r, g, b, a;
		
		public BatchClear(float r_, float g_, float b_, float a_) {
			r = r_;
			g = g_;
			b = b_;
			a = a_;
		}
		
		public boolean isQuads() { return false; }
		
		public void run() {
			GLES20.glClearColor(r, g, b, a);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		}
	}
	
	public boolean isPOT(int x) {
		return x > 0 && ((x - 1) & x) == 0;
	}
	
	public class BatchCreateTexture implements IBatch {
		String src;
		boolean tiling;
		boolean linearsampling;
		int mapToID;
		
		public BatchCreateTexture(String src_, boolean tiling_, boolean linearsampling_, int mapToID_)
		{
			src = src_;
			tiling = tiling_;
			linearsampling = linearsampling_;
			mapToID = mapToID_;
			
			if (src.startsWith("file:///android_asset/"))
				src = src.substring(22);
		}
		
		public boolean isQuads() { return false; }
		
		public void run() {
			IntBuffer ib = IntBuffer.allocate(1);
			GLES20.glGenTextures(1, ib);
			int tex = ib.get(0);
			textureIDs.put(mapToID, tex);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
			
			Bitmap bmp = null;
			try {
				bmp = BitmapFactory.decodeStream(amgr.open(src));
			} catch (IOException e) {}
			
			if (bmp == null)
			{
				Log.e("WebGLGap", "Error loading texture (returned null) for " + src);
				return;
			}
			
			boolean pot = (isPOT(bmp.getWidth()) && isPOT(bmp.getHeight()));
			
			if (!pot && tiling)
			{
				// TODO
			}
			else
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLUtils.getInternalFormat(bmp), bmp, GLUtils.getType(bmp), 0);
			
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, tiling ? GLES20.GL_REPEAT : GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, tiling ? GLES20.GL_REPEAT : GLES20.GL_CLAMP_TO_EDGE);
			
			if (linearsampling)
			{
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
				
				if (pot)
				{
					GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
					GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
				}
				else
					GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			}
			else
			{
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			}

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		}
	}
	
	private ArrayList<IBatch> batch = new ArrayList<IBatch>();
	
	// Reference back to the plugin.
	public static GLGapPlugin glgap;
	
	public GLGapRenderer(GLGapPlugin glgap_) {
		super();
		glgap = glgap_;
	}
	
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    
    	// Clear black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        // Set up mode
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        // Set up buffers
        IntBuffer buffers = IntBuffer.allocate(3);
        GLES20.glGenBuffers(3, buffers);
        vertexBuffer = buffers.get(0);
        texcoordBuffer = buffers.get(1);
        indexBuffer = buffers.get(2);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texcoordBuffer);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        
        // Set up shader program
        fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragSrc);
        GLES20.glCompileShader(fragmentShader);
        
        vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vsSrc);
        GLES20.glCompileShader(vertexShader);
        
        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glLinkProgram(shaderProgram);
        GLES20.glUseProgram(shaderProgram);
        
        // Get locations
        locAPos = GLES20.glGetAttribLocation(shaderProgram, "aPos");
        GLES20.glEnableVertexAttribArray(locAPos);
        
        locATex = GLES20.glGetAttribLocation(shaderProgram, "aTex");
        GLES20.glEnableVertexAttribArray(locATex);
        
        locMatP = GLES20.glGetUniformLocation(shaderProgram, "matP");
        locMatMV = GLES20.glGetUniformLocation(shaderProgram, "matMV");
        locOpacity = GLES20.glGetUniformLocation(shaderProgram, "opacity");
        locSampler = GLES20.glGetUniformLocation(shaderProgram, "sampler");
        GLES20.glUniform1f(locOpacity, 1);
        
        // Finalise buffers and texture support
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer);
        GLES20.glVertexAttribPointer(locAPos, 2, GLES20.GL_FLOAT, false, 0, 0);
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texcoordBuffer);
        GLES20.glVertexAttribPointer(locATex, 2, GLES20.GL_FLOAT, false, 0, 0);
        
        GLES20.glActiveTexture(0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUniform1i(locSampler, 0);
    }
    
    public void endBatch()
    {
    	if (batch.isEmpty())
    		return;
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer);
    	GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.position() * 4, vertexData, GLES20.GL_DYNAMIC_DRAW);
    	vertexData.position(0);
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texcoordBuffer);
    	GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texcoordData.position() * 4, texcoordData, GLES20.GL_DYNAMIC_DRAW);
    	texcoordData.position(0);
    	
    	GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexData.position() * 2, indexData, GLES20.GL_DYNAMIC_DRAW);
    	indexData.position(0);
    	
    	for (IBatch b : batch)
    	{
    		b.run();
    	}
    	
    	batch.clear();
    }
    
    Pattern pipeSplitter = Pattern.compile("\\|");
    Pattern commaSplitter = Pattern.compile("\\,");
    
    // Only called by requestRender since we're in dirty mode
    public void onDrawFrame(GL10 unused) {
    	
    	// Run the draw queue then clear it
        renderLock.lock();
        
        if (renderCommand == null)
        {
        	renderLock.unlock();
        	return;
        }
        
        // Parse the render command string (super hack)
        String[] cmds = pipeSplitter.split(renderCommand);
        String[] temp;
        
        // indicate early that draw queue has been rendered since we're done using renderCommand.
       	rendered = true;
        renderedCondition.signal();
        renderLock.unlock();
        
        for (int i = 0, len = cmds.length; i < len; ++i)
        {
        	String cmd = cmds[i];
        	char c = cmd.charAt(0);
        	cmd = cmd.substring(1);
        	//Log.i("GLGap", cmd);
        	
        	switch (c) {
        	case 'A':	// resetModelView
        		resetModelView();
        		break;
        	case 'B':	// translate
        		temp = commaSplitter.split(cmd);
        		translate(new Float(temp[0]), new Float(temp[1]));
        		break;
        	case 'C':	// scale
        		temp = commaSplitter.split(cmd);
        		scale(new Float(temp[0]), new Float(temp[1]));
        		break;
        	case 'D':	// rotateZ
        		rotateZ(new Float(cmd));
        		break;
        	case 'E':	// updateModelView
        		updateModelView();
        		break;
        	case 'F':	// endBatch
        		endBatch();
        		break;
        	case 'G':	// setOpacity
        		batch.add(new BatchSetOpacity(new Float(cmd)));
        		break;
        	case 'H':	// setTexture
        		int tex = new Integer(cmd);
        		batch.add(new BatchSetTexture(tex));
        		break;
        	case 'I':	// setBlend
        		temp = commaSplitter.split(cmd);
        		batch.add(new BatchSetBlend(new Integer(temp[0]), new Integer(temp[1])));
        		break;
        	case 'J':	// quad
        		temp = commaSplitter.split(cmd);
        		quad(new Float(temp[0]), new Float(temp[1]), new Float(temp[2]), new Float(temp[3]), new Float(temp[4]), new Float(temp[5]), new Float(temp[6]), new Float(temp[7]));
        		break;
        	case 'K':	// quadTex
        		temp = commaSplitter.split(cmd);
        		quadTex(new Float(temp[0]), new Float(temp[1]), new Float(temp[2]), new Float(temp[3]), new Float(temp[4]), new Float(temp[5]), new Float(temp[6]), new Float(temp[7]),
        				new Float(temp[8]), new Float(temp[9]), new Float(temp[10]), new Float(temp[11]));
        		break;
        	case 'L':	// clear
        		temp = commaSplitter.split(cmd);
        		batch.add(new BatchClear(new Float(temp[0]), new Float(temp[1]), new Float(temp[2]), new Float(temp[3])));
        		break;
        	case 'M':	// loadTexture
        		temp = commaSplitter.split(cmd);
        		batch.add(new BatchCreateTexture(temp[0], new Integer(temp[1]) != 0, new Integer(temp[2]) != 0, new Integer(temp[3])));
        		break;
        	case 'N':	// createEmptyTexture
        		// todo
        		break;
        	case 'O':	// deleteTexture
        		// todo
        		break;
        	default:
        		Log.e("GLGap", "Unknown command '" + c + "'");
        	}
        }
        
        endBatch();
        GLES20.glFlush();
    }
        
    int LAST_VERTEX = MAX_VERTICES * 2 - 8;
    
    public void quad(float tlx, float tly, float trx, float try_, float brx, float bry, float blx, float bly) {
		if (vertexData.position() >= LAST_VERTEX)
			endBatch();
		
		short fv = (short)(vertexData.position() / 2);
		
		if (batch.isEmpty() || !batch.get(batch.size() - 1).isQuads())
		{
			batch.add(new BatchQuad(indexData.position(), 6));
		}
		else
			((BatchQuad)batch.get(batch.size() - 1)).indexCount += 6;
		
		vertexData.put(tlx);
		vertexData.put(tly);
		vertexData.put(trx);
		vertexData.put(try_);
		vertexData.put(brx);
		vertexData.put(bry);
		vertexData.put(blx);
		vertexData.put(bly);
		
		texcoordData.put(0);
		texcoordData.put(0);
		texcoordData.put(1);
		texcoordData.put(0);
		texcoordData.put(1);
		texcoordData.put(1);
		texcoordData.put(0);
		texcoordData.put(1);
		
		indexData.put(fv);
		indexData.put((short)(fv + 1));
		indexData.put((short)(fv + 2));
		indexData.put(fv);
		indexData.put((short)(fv + 2));
		indexData.put((short)(fv + 3));
	}
    
    public void quadTex(float tlx, float tly, float trx, float try_, float brx, float bry, float blx, float bly,
    		float texLeft, float texTop, float texRight, float texBottom) {
		if (vertexData.position() >= LAST_VERTEX)
			endBatch();
		
		short fv = (short)(vertexData.position() / 2);
		
		if (batch.isEmpty() || !batch.get(batch.size() - 1).isQuads())
		{
			batch.add(new BatchQuad(indexData.position(), 6));
		}
		else
			((BatchQuad)batch.get(batch.size() - 1)).indexCount += 6;
		
		vertexData.put(tlx);
		vertexData.put(tly);
		vertexData.put(trx);
		vertexData.put(try_);
		vertexData.put(brx);
		vertexData.put(bry);
		vertexData.put(blx);
		vertexData.put(bly);
		
		texcoordData.put(texLeft);
		texcoordData.put(texTop);
		texcoordData.put(texRight);
		texcoordData.put(texTop);
		texcoordData.put(texRight);
		texcoordData.put(texBottom);
		texcoordData.put(texLeft);
		texcoordData.put(texBottom);
		
		indexData.put(fv);
		indexData.put((short)(fv + 1));
		indexData.put((short)(fv + 2));
		indexData.put(fv);
		indexData.put((short)(fv + 2));
		indexData.put((short)(fv + 3));
	}

	public void onSurfaceChanged(GL10 unused, int width_, int height_) {
        GLES20.glViewport(0, 0, width_, height_);
        width = width_;
        height = height_;
        
        float top = (float)Math.tan(45.0 * Math.PI / 360.0);
        float right = top * ((float)width / (float)height);
        Matrix.frustumM(matP, 0, -right, right, -top, top, 1.0f, 1000.0f);
        Matrix.setLookAtM(matMV, 0, cam[0], cam[1], cam[2], look[0], look[1], look[2], up[0], up[1], up[2]);
        
        float[] tl = {0, 0, 0, 0};
        float[] br = {0, 0, 0, 0};
        int[] view = {0, 0, width, height};
        GLU.gluProject(0.0f, 0.0f, 0.0f, matMV, 0, matP, 0, view, 0, tl, 0);
        GLU.gluProject(1.0f, 1.0f, 0.0f, matMV, 0, matP, 0, view, 0, br, 0);
        worldScale[0] = 1.0f / (br[0] - tl[0]);
        worldScale[1] = -1.0f / (br[1] - tl[1]);
        
        GLES20.glUniformMatrix4fv(locMatP, 1, false, matP, 0);
    }
    
    public void resetModelView() {
    	Matrix.setLookAtM(matMV, 0, cam[0], cam[1], cam[2], look[0], look[1], look[2], up[0], up[1], up[2]);
    	Matrix.scaleM(matMV, 0, worldScale[0], worldScale[1], worldScale[2]);
    }
    
    public void translate(float x, float y) {
    	Matrix.translateM(matMV, 0, x, y, 0.0f);
    }
    
    public void scale(float x, float y) {
    	Matrix.scaleM(matMV, 0, x, y, 0.0f);
    }
    
    public void rotateZ(float a) {
    	Matrix.rotateM(matMV, 0, a, 0.0f, 0.0f, 1.0f);
    }
    
    public void updateModelView() {
    	if (this.lastMV.equals(this.matMV))
    		return;		// no change in model view
    	
    	batch.add(new BatchUpdateModelView(this.matMV));
    	this.lastMV = this.matMV.clone();
    }
    
    public void setOpacity(float op) {
    	if (op == lastOpacity)
    		return;
    	
    	batch.add(new BatchSetOpacity(op));
    	lastOpacity = op;
    }
    
    public void setTexture(int tex) {
    	if (tex == lastTexture)
    		return;
    	
    	batch.add(new BatchSetTexture(tex));
    	lastTexture = tex;
    }
  
    public void setBlend(int src, int dest) {
    	if (src == lastSrcBlend && dest == lastDestBlend)
    		return;
    	
    	batch.add(new BatchSetBlend(src, dest));
    	lastSrcBlend = src;
    	lastDestBlend = dest;
    }

}