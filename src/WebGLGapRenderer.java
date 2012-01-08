// WebGLGap
// Copyright (c) 2012 Scirra Ltd
// Written by Ashley Gullen
package com.phonegap.plugin.webglgap;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

public class WebGLGapRenderer implements GLSurfaceView.Renderer {
	
	public AssetManager amgr;
  
	// Any queued OpenGL call.
	public interface IDrawCommand {
		public void run();
	}
	
	// activeTexture
	public class ActiveTextureCommand implements IDrawCommand {
		int target;
		
		public ActiveTextureCommand(int target_) {
			target = target_;
		}
		
		public void run() {
			GLES20.glActiveTexture(target);
		}
	}
	
	// attachShader
	public class AttachShaderCommand implements IDrawCommand {
		private int progID;
		private int shaderID;
		
		public AttachShaderCommand(int progID_, int shaderID_) {
			progID = progID_;
			shaderID = shaderID_;
		}
		
		public void run() {
			GLES20.glAttachShader(webglgap.programIDs.get(progID), webglgap.shaderIDs.get(shaderID));
		}
	}
	
	// bindBuffer
	public class BindBufferCommand implements IDrawCommand {
		private int type;
		private int bufferID;
		
		public BindBufferCommand(int type_, int bufferID_) {
			type = type_;
			bufferID = bufferID_;
		}
		
		public void run() {
			GLES20.glBindBuffer(type, webglgap.bufferIDs.get(bufferID));
		}
	}
	
	// bindTexture
	public class BindTextureCommand implements IDrawCommand {
		private int target;
		private int textureID;
		
		public BindTextureCommand(int target_, int textureID_) {
			target = target_;
			textureID = textureID_;
		}
		
		public void run() {
			if (textureID == 0)
				GLES20.glBindTexture(target, 0);
			else
				GLES20.glBindTexture(target, webglgap.textureIDs.get(textureID));
		}
	}
	
	// blendFunc
	public class BlendFuncCommand implements IDrawCommand {
		private int srcBlend;
		private int destBlend;
		
		public BlendFuncCommand(int srcBlend_, int destBlend_) {
			srcBlend = srcBlend_;
			destBlend = destBlend_;
		}
		
		public void run() {
			GLES20.glBlendFunc(srcBlend, destBlend);
		}
	}
		
	// bufferData
	public class BufferDataCommand implements IDrawCommand {
		private int type;
		private FloatBuffer floatbuffer;
		private ShortBuffer shortbuffer;
		private int usage;
		
		public BufferDataCommand(int type_, FloatBuffer floatbuffer_, int usage_) {
			type = type_;
			floatbuffer = floatbuffer_;
			usage = usage_;
		}
		
		public BufferDataCommand(int type_, ShortBuffer shortbuffer_, int usage_) {
			type = type_;
			shortbuffer = shortbuffer_;
			usage = usage_;
		}
		
		public void run() {
			if (type == GLES20.GL_ELEMENT_ARRAY_BUFFER)
			{
				GLES20.glBufferData(type, shortbuffer.capacity() * 2, shortbuffer, usage);
			}
			else
			{
				GLES20.glBufferData(type, floatbuffer.capacity() * 4, floatbuffer, usage);
			}
		}
	}
	
	// clear
	public class ClearCommand implements IDrawCommand {
		int flags;
		
		public ClearCommand(int flags_) {
			flags = flags_;
		}
		
		public void run() {
			GLES20.glClear(flags);
		}
	}
	
	// clearColor
	public class ClearColorCommand implements IDrawCommand {
		float r, g, b, a;
		
		public ClearColorCommand(float r_, float g_, float b_, float a_) {
			r = r_; g = g_; b = b_; a = a_;
		}
		
		public void run() {
			GLES20.glClearColor(r, g, b, a);
		}
	}
	
	// compileShader
	public class CompileShaderCommand implements IDrawCommand {
		int shaderID;
		
		public CompileShaderCommand(int shaderID_) {
			shaderID = shaderID_;
		}
		
		public void run() {
			GLES20.glCompileShader(webglgap.shaderIDs.get(shaderID));
		}
	}
	
	// createBuffer
	public class CreateBufferCommand implements IDrawCommand {
		
		private int mapToID;
		
		public CreateBufferCommand(int mapToID_) {
			mapToID = mapToID_;
		}
		
		public void run()
		{
			IntBuffer ib = IntBuffer.allocate(1);
			GLES20.glGenBuffers(1, ib);
			webglgap.bufferIDs.put(mapToID, ib.get(0));
		}
	}
	
	// createProgram
	public class CreateProgramCommand implements IDrawCommand {
		
		private int mapToID;
		
		public CreateProgramCommand(int mapToID_) {
			mapToID = mapToID_;
		}
		
		public void run()
		{
			int ret = GLES20.glCreateProgram();
			webglgap.programIDs.put(mapToID, ret);
		}
	}
	
	// createShader
	public class CreateShaderCommand implements IDrawCommand {
		
		private int mapToID;
		private int type;
		
		public CreateShaderCommand(int mapToID_, int type_) {
			mapToID = mapToID_;
			type = type_;
		}
		
		public void run()
		{
			int ret = GLES20.glCreateShader(type);
			webglgap.shaderIDs.put(mapToID, ret);
		}
	}
	
	// createTexture
	public class CreateTextureCommand implements IDrawCommand {
		
		private int mapToID;
		
		public CreateTextureCommand(int mapToID_) {
			mapToID = mapToID_;
		}
		
		public void run()
		{
			IntBuffer ib = IntBuffer.allocate(1);
			GLES20.glGenTextures(1, ib);
			webglgap.textureIDs.put(mapToID, ib.get(0));
		}
	}
	
	// cullFace
	public class CullFaceCommand implements IDrawCommand {
		
		private int mode;
		
		public CullFaceCommand(int mode_) {
			mode = mode_;
		}
		
		public void run()
		{
			GLES20.glCullFace(mode);
		}
	}
	
	// drawArrays
	public class DrawArraysCommand implements IDrawCommand {
		
		private int type;
		private int first;
		private int count;
		
		public DrawArraysCommand(int type_, int first_, int count_) {
			type = type_;
			first = first_;
			count = count_;
		}
		
		public void run() {
			GLES20.glDrawArrays(type, first, count);
		}
	}
	
	// drawElements
	public class DrawElementsCommand implements IDrawCommand {
		
		private int mode;
		private int count;
		private int type;
		private int offset;
		
		public DrawElementsCommand(int mode_, int count_, int type_, int offset_) {
			mode = mode_;
			count = count_;
			type = type_;
			offset = offset_;
		}
		
		public void run() {
			GLES20.glDrawElements(mode, count, type, offset);
		}
	}
	
	// enable/disable
	public class EnableDisableCommand implements IDrawCommand {
		private boolean enable;
		private int flag;
		
		public EnableDisableCommand(boolean enable_, int flag_) {
			enable = enable_;
			flag = flag_;
		}
		
		public void run() {
			if (enable)
				GLES20.glEnable(flag);
			else
				GLES20.glDisable(flag);
		}
	}
	
	// enableVertexAttribArray
	public class EnableVertexAttribArrayCommand implements IDrawCommand {
		private int locationID;
		
		public EnableVertexAttribArrayCommand(int locationID_) {
			locationID = locationID_;
		}
		
		public void run() {
			GLES20.glEnableVertexAttribArray(webglgap.locationIDs.get(locationID));
		}
	}
	
	// getAttribLocation
	public class GetAttribLocationCommand implements IDrawCommand {
		private int mapToID;
		private int progID;
		private String attrib;
		
		public GetAttribLocationCommand(int mapToID_, int progID_, String attrib_) {
			mapToID = mapToID_;
			progID = progID_;
			attrib = attrib_;
		}
		
		public void run()
		{
			int ret = GLES20.glGetAttribLocation(webglgap.programIDs.get(progID), attrib);
			webglgap.locationIDs.put(mapToID, ret);
		}
	}
	
	// getUniformLocation
	public class GetUniformLocationCommand implements IDrawCommand {
		private int mapToID;
		private int progID;
		private String uniform;
		
		public GetUniformLocationCommand(int mapToID_, int progID_, String uniform_) {
			mapToID = mapToID_;
			progID = progID_;
			uniform = uniform_;
		}
		
		public void run()
		{
			int ret = GLES20.glGetUniformLocation(webglgap.programIDs.get(progID), uniform);
			webglgap.locationIDs.put(mapToID, ret);
		}
	}
	
	// generateMipmap
	public class GenerateMipmapCommand implements IDrawCommand {
		private int target;
		
		public GenerateMipmapCommand(int target_) {
			target = target_;
		}
		
		public void run() {
			GLES20.glGenerateMipmap(target);
		}
	}
	
	// linkProgram
	public class LinkProgramCommand implements IDrawCommand {
		private int programID;
		
		public LinkProgramCommand(int programID_) {
			programID = programID_;
		}
		
		public void run() {
			GLES20.glLinkProgram(webglgap.programIDs.get(programID));
		}
	}
	
	// pixelStorei
	public class PixelStoreiCommand implements IDrawCommand {
		private int pname;
		private int param;
		
		public PixelStoreiCommand(int pname_, int param_) {
			pname = pname_;
			param = param_;
		}
		
		public void run() {
			GLES20.glPixelStorei(pname, param);
		}
	}
	
	// shaderSource
	public class ShaderSourceCommand implements IDrawCommand {
		private int shaderID;
		private String src;
		
		public ShaderSourceCommand(int shaderID_, String src_) {
			shaderID = shaderID_;
			src = src_;
		}
		
		public void run() {
			GLES20.glShaderSource(webglgap.shaderIDs.get(shaderID), src);
		}
	}
	
	// texImage2D
	public class TexImage2DCommand implements IDrawCommand {
		private int target;
		private int level;
		private int internalformat;
		private int format;
		private int type;
		private String src;
		
		public TexImage2DCommand(int target_, int level_, int internalformat_, int format_, int type_, String src_) {
			target = target_;
			level = level_;
			internalformat = internalformat_;
			format = format_;
			type = type_;
			src = src_;
			
			if (src.startsWith("file:///android_asset/"))
				src = src.substring(22);
		}
		
		public void run() {
			Bitmap bmp = null;
			try {
				bmp = BitmapFactory.decodeStream(amgr.open(src));
			} catch (IOException e) {}
			
			if (bmp == null)
			{
				Log.e("WebGLGap", "Error loading texture (returned null) for " + src);
				return;
			}
			
			Log.i("WebGLGap", "GLUtils.texImage2D: " + bmp.getWidth() + " x " + bmp.getHeight() + " -> " + src);
			GLUtils.texImage2D(target, level, GLUtils.getInternalFormat(bmp), bmp, GLUtils.getType(bmp), 0);
		}
	}
	
	// texParameteri
	public class TexParameteriCommand implements IDrawCommand {
		private int target;
		private int pname;
		private int param;
		
		public TexParameteriCommand(int target_, int pname_, int param_) {
			target = target_;
			pname = pname_;
			param = param_;
		}
		
		public void run() {
			GLES20.glTexParameteri(target, pname, param);
		}
	}
	
	// uniform{1|2|3|4}f
	public class UniformfCommand implements IDrawCommand {
		private int pos;
		private int n;
		private float x, y, z, w;
		
		public UniformfCommand(int pos_, int n_, float x_, float y_, float z_, float w_) {
			pos = pos_;
			n = n_;
			x = x_;
			y = y_;
			z = z_;
			w = w_;
		}
		
		public void run() {
			switch (n) {
			case 1:
				GLES20.glUniform1f(webglgap.locationIDs.get(pos), x);
				break;
			case 2:
				GLES20.glUniform2f(webglgap.locationIDs.get(pos), x, y);
				break;
			case 3:
				GLES20.glUniform3f(webglgap.locationIDs.get(pos), x, y, z);
				break;
			case 4:
				GLES20.glUniform4f(webglgap.locationIDs.get(pos), x, y, z, w);
				break;
			}
		}
	}
	
	// uniform{1|2|3|4}i
	public class UniformiCommand implements IDrawCommand {
		private int pos;
		private int n;
		private int x, y, z, w;
		
		public UniformiCommand(int pos_, int n_, int x_, int y_, int z_, int w_) {
			pos = pos_;
			n = n_;
			x = x_;
			y = y_;
			z = z_;
			w = w_;
		}
		
		public void run() {
			switch (n) {
			case 1:
				GLES20.glUniform1i(webglgap.locationIDs.get(pos), x);
				break;
			case 2:
				GLES20.glUniform2i(webglgap.locationIDs.get(pos), x, y);
				break;
			case 3:
				GLES20.glUniform3i(webglgap.locationIDs.get(pos), x, y, z);
				break;
			case 4:
				GLES20.glUniform4i(webglgap.locationIDs.get(pos), x, y, z, w);
				break;
			}
		}
	}
	
	// uniformMatrix4fv
	public class UniformMatrix4fvCommand implements IDrawCommand {
		private int locationID;
		private boolean transpose;
		private float[] mat;
		
		public UniformMatrix4fvCommand(int locationID_, boolean transpose_, float[] mat_) {
			locationID = locationID_;
			transpose = transpose_;
			mat = mat_;
		}
		
		public void run() {
			GLES20.glUniformMatrix4fv(webglgap.locationIDs.get(locationID), 1, transpose, mat, 0);
		}
	}
	
	// useProgram
	public class UseProgramCommand implements IDrawCommand {
		private int programID;
		
		public UseProgramCommand(int programID_) {
			programID = programID_;
		}
		
		public void run() {
			GLES20.glUseProgram(webglgap.programIDs.get(programID));
		}
	}
	
	// vertexAttribPointer
	public class VertexAttribPointerCommand implements IDrawCommand {
		private int locationID;
		private int size;
		private int type;
		private boolean normalized;
		private int stride;
		private int offset;
		
		public VertexAttribPointerCommand(int locationID_, int size_, int type_, boolean normalized_, int stride_, int offset_)
		{
			locationID = locationID_;
			size = size_;
			type = type_;
			normalized = normalized_;
			stride = stride_;
			offset = offset_;
		}
		
		public void run() {
			GLES20.glVertexAttribPointer(webglgap.locationIDs.get(locationID), size, type, normalized, stride, offset);
		}
	}
	
	// Reference back to the plugin.
	public static WebGLGapPlugin webglgap;
	
	public WebGLGapRenderer(WebGLGapPlugin webglgap_) {
		super();
		webglgap = webglgap_;
	}
	
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    
    	// Clear black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
    
    // Only called by requestRender since we're in dirty mode
    public void onDrawFrame(GL10 unused) {
    
    	// Run the draw queue then clear it
        webglgap.renderLock.lock();
        
        if (webglgap.rendered)
        {
        	webglgap.renderLock.unlock();
        	return;
        }
        
        Log.i("WebGLGap", "Rendering " + webglgap.drawQueue.size() + " commands");
        
        try {
        	for (IDrawCommand dc : webglgap.drawQueue)
        	{
        		dc.run();
        	}
        	
        	GLES20.glFlush();
        	
        	// indicate that draw queue has been rendered
        	webglgap.rendered = true;
        	webglgap.renderedCondition.signal();
        	
        } finally {
        	webglgap.renderLock.unlock();
        }
    }
    
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
  
}