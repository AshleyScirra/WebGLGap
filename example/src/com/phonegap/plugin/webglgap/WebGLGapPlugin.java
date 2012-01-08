// WebGLGap
// Copyright (c) 2012 Scirra Ltd
// Written by Ashley Gullen
package com.phonegap.plugin.webglgap;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.phonegap.DroidGap;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;
import com.phonegap.plugin.webglgap.WebGLGapRenderer.IDrawCommand;

public class WebGLGapPlugin extends Plugin {

	// The OpenGL ES 2 surface view and its renderer
	public GLSurfaceView mGLView = null;
	public WebGLGapRenderer renderer = null;
	
	// Since the javascript calls and renderer are running on separate threads,
	// queue all the calls in to nextQueue.  In endRender(), nextQueue is sent
	// to drawQueue which the renderer uses, so the javascript thread can start
	// filling up the next queue without waiting for the renderer.
	public ArrayList<IDrawCommand> drawQueue = new ArrayList<IDrawCommand>();
	public ArrayList<IDrawCommand> nextQueue = new ArrayList<IDrawCommand>();
	
	// The lock for the drawQueue.  rendered renderedCondition guarantee the render thread
	// has seen the draw queue at least once, so racing doesn't drop a frame (which could be initialisation code!)
	public Lock renderLock = new ReentrantLock();
	public Condition renderedCondition = renderLock.newCondition();
	public boolean rendered = true;
	
	// Since calls are queued and not immediately run, there's no way to get real
	// return values for WebGL calls.  Instead a made-up ID is returned, and when
	// the actual call runs it maps the made-up ID to the real ID.
	public HashMap<Integer, Integer> programIDs = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> bufferIDs = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> shaderIDs = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> locationIDs = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> textureIDs = new HashMap<Integer, Integer>();
	
	// Convert JSON parameter to matrix
	private static float[] JSONToMatrix(JSONArray arr) throws JSONException {
		float[] ret = new float[16];
		
		for (int i = 0; i < 16; ++i)
			ret[i] = (float)arr.getDouble(i);
		
		return ret;
	}
	
	// Get webglgap_id from a parameter
	private static int GetWebGLGapID(JSONArray arr, int index) throws JSONException {
		return arr.getJSONObject(index).getInt("webglgap_id");
	}
	
	@Override
	public PluginResult execute(String action, JSONArray params, String callbackId) {
		
		PluginResult result = new PluginResult(Status.OK);
		
		try {
			if (action.equals("commandBatch"))
			{
				for (int i = 0, len = params.length(); i < len; ++i)
				{
					JSONObject jso = params.getJSONObject(i);
					doCommand(jso.getString("action"), jso.getJSONArray("params"));
				}
			}
			// Create the WebGLGap context
			else if (action.equals("create"))
			{			
				final WebGLGapPlugin me = this;
				
				JSONObject attribs = params.getJSONObject(0);
				final boolean alpha = attribs.optBoolean("alpha", false);
				final boolean depth = attribs.optBoolean("depth", true);
				final boolean stencil = attribs.optBoolean("stencil", false);
				Log.i("WebGLGap", "Creating OpenGL view... alpha=" + alpha + ", depth=" + depth + ", stencil=" + stencil);
				
				Runnable startup = new Runnable() {
					public void run() {
						// Create the OpenGL ES 2 view and add it as another content view
				        me.mGLView = new C2OpenGLES20View(me.ctx, me, alpha, depth, stencil);
				        me.ctx.addContentView(me.mGLView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				       
				        // Make sure the view and its parent layout have transparent backgrounds
				        // since they're on top of the OpenGL view
				        DroidGap dg = (DroidGap)me.ctx;
						dg.setIntegerProperty("backgroundColor", 0);
						
				        me.webView.setBackgroundColor(0);
				        LinearLayout root = (LinearLayout)me.webView.getParent();
				        root.setBackgroundColor(0);
				        
				        // Z order the web view to top
				        
				        root.bringChildToFront(me.webView);
				        root.bringToFront();
				        root.invalidate();
				        me.webView.invalidate();
				        
				        Log.i("WebGLGap", "Created OpenGL view");
					}
				};
				
				ctx.runOnUiThread(startup);
			}
			else
				Log.e("WebGLGap", "Unknown command: " + action);
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.e("WebGLGap", e.toString());
		}
		
		return result;
	}
	
	public void doCommand(String action, JSONArray params) throws JSONException, InterruptedException {
		
		//Log.i("WebGLGap", "command '" + action + "': " + params.toString());
		
		if (action.equals("activeTexture"))
		{
			nextQueue.add(renderer.new ActiveTextureCommand(params.getInt(0)));
		}
		else if (action.equals("attachShader"))
		{
			nextQueue.add(renderer.new AttachShaderCommand(GetWebGLGapID(params, 0), GetWebGLGapID(params, 1)));
		}
		else if (action.equals("bindBuffer"))
		{
			nextQueue.add(renderer.new BindBufferCommand(params.getInt(0), GetWebGLGapID(params, 1)));
		}
		else if (action.equals("bindTexture"))
		{
			int textureID = 0;
			
			// Allow passing null or 0 for texture ID
			JSONObject tex = params.optJSONObject(1);
			
			if (tex != null)
				textureID = tex.getInt("webglgap_id");
			
			nextQueue.add(renderer.new BindTextureCommand(params.getInt(0), textureID));
		}
		else if (action.equals("blendFunc"))
		{
			nextQueue.add(renderer.new BlendFuncCommand(params.getInt(0), params.getInt(1)));
		}
		else if (action.equals("bufferData"))
		{
			int type = params.getInt(0);
			JSONArray data_array = params.getJSONArray(1);
			
			// Is using elements array buffer: copy out uint16s
			if (type == GLES20.GL_ELEMENT_ARRAY_BUFFER)
			{
				ShortBuffer sb = ShortBuffer.allocate(data_array.length());
				
				for (int i = 0, len = data_array.length(); i < len; ++i)
					sb.put(i, (short)data_array.getInt(i));
				
				nextQueue.add(renderer.new BufferDataCommand(params.getInt(0), sb, params.getInt(2)));
			}
			// Otherwise assume float32 data
			else
			{
				// Copy float buffer out from JSON array
				FloatBuffer fb = FloatBuffer.allocate(data_array.length());
				
				for (int i = 0, len = data_array.length(); i < len; ++i)
					fb.put(i, (float)data_array.getDouble(i));
				
				nextQueue.add(renderer.new BufferDataCommand(params.getInt(0), fb, params.getInt(2)));
			}
		}
		else if (action.equals("clear"))
		{
			nextQueue.add(renderer.new ClearCommand(params.getInt(0)));
		}
		else if (action.equals("clearColor"))
		{
			nextQueue.add(renderer.new ClearColorCommand((float)params.getDouble(0), (float)params.getDouble(1), (float)params.getDouble(2), (float)params.getDouble(3)));
		}
		else if (action.equals("compileShader"))
		{
			nextQueue.add(renderer.new CompileShaderCommand(GetWebGLGapID(params, 0)));
		}
		else if (action.equals("cullFace"))
		{
			nextQueue.add(renderer.new CullFaceCommand(params.getInt(0)));
		}
		else if (action.equals("disable"))
		{
			nextQueue.add(renderer.new EnableDisableCommand(false, params.getInt(0)));
		}
		else if (action.equals("drawArrays"))
		{
			nextQueue.add(renderer.new DrawArraysCommand(params.getInt(0), params.getInt(1), params.getInt(2)));
		}
		else if (action.equals("drawElements"))
		{
			nextQueue.add(renderer.new DrawElementsCommand(params.getInt(0), params.getInt(1), params.getInt(2), params.getInt(3)));
		}
		else if (action.equals("enable"))
		{
			nextQueue.add(renderer.new EnableDisableCommand(true, params.getInt(0)));
		}
		else if (action.equals("enableVertexAttribArray"))
		{
			nextQueue.add(renderer.new EnableVertexAttribArrayCommand(GetWebGLGapID(params, 0)));
		}
		else if (action.equals("generateMipmap"))
		{
			nextQueue.add(renderer.new GenerateMipmapCommand(params.getInt(0)));
		}
		else if (action.equals("linkProgram"))
		{
			nextQueue.add(renderer.new LinkProgramCommand(GetWebGLGapID(params, 0)));
		}
		else if (action.equals("pixelStorei"))
		{
			nextQueue.add(renderer.new PixelStoreiCommand(params.getInt(0), params.getInt(1)));
		}
		else if (action.equals("shaderSource"))
		{
			nextQueue.add(renderer.new ShaderSourceCommand(GetWebGLGapID(params, 0), params.getString(1)));
		}
		else if (action.equals("texImage2D"))
		{
			Log.i("WebGLGap", "texImage2D(): " + params.toString());
			nextQueue.add(renderer.new TexImage2DCommand(params.getInt(0), params.getInt(1), params.getInt(2), params.getInt(3), params.getInt(4), params.getString(5)));
		}
		else if (action.equals("texParameteri"))
		{
			nextQueue.add(renderer.new TexParameteriCommand(params.getInt(0), params.getInt(1), params.getInt(2)));
		}
		else if (action.equals("uniform1f"))
		{
			nextQueue.add(renderer.new UniformfCommand(GetWebGLGapID(params, 0), 1, (float)params.getDouble(1), 0, 0, 0));
		}
		else if (action.equals("uniform1i"))
		{
			nextQueue.add(renderer.new UniformiCommand(GetWebGLGapID(params, 0), 1, params.getInt(1), 0, 0, 0));
		}
		else if (action.equals("uniform2f"))
		{
			nextQueue.add(renderer.new UniformfCommand(GetWebGLGapID(params, 0), 2, (float)params.getDouble(1), (float)params.getDouble(2), 0, 0));
		}
		else if (action.equals("uniform2i"))
		{
			nextQueue.add(renderer.new UniformiCommand(GetWebGLGapID(params, 0), 2, params.getInt(1), params.getInt(2), 0, 0));
		}
		else if (action.equals("uniform3f"))
		{
			nextQueue.add(renderer.new UniformfCommand(GetWebGLGapID(params, 0), 3, (float)params.getDouble(1), (float)params.getDouble(2), (float)params.getDouble(3), 0));
		}
		else if (action.equals("uniform3i"))
		{
			nextQueue.add(renderer.new UniformiCommand(GetWebGLGapID(params, 0), 3, params.getInt(1), params.getInt(2), params.getInt(3), 0));
		}
		else if (action.equals("uniform4f"))
		{
			nextQueue.add(renderer.new UniformfCommand(GetWebGLGapID(params, 0), 4, (float)params.getDouble(1), (float)params.getDouble(2), (float)params.getDouble(3), (float)params.getDouble(4)));
		}
		else if (action.equals("uniform4i"))
		{
			nextQueue.add(renderer.new UniformiCommand(GetWebGLGapID(params, 0), 4, params.getInt(1), params.getInt(2), params.getInt(3), params.getInt(4)));
		}
		else if (action.equals("uniformMatrix4fv"))
		{
			nextQueue.add(renderer.new UniformMatrix4fvCommand(GetWebGLGapID(params, 0), params.getBoolean(1), JSONToMatrix(params.getJSONArray(2))));
		}
		else if (action.equals("useProgram"))
		{
			nextQueue.add(renderer.new UseProgramCommand(GetWebGLGapID(params, 0)));
		}
		else if (action.equals("vertexAttribPointer"))
		{
			nextQueue.add(renderer.new VertexAttribPointerCommand(GetWebGLGapID(params, 0), params.getInt(1), params.getInt(2), params.getBoolean(3), params.getInt(4), params.getInt(5)));
		}
		else if (action.equals("flush"))
		{
			renderLock.lock();

			// wait to ensure the last drawQueue has been rendered by the renderer so we
			// aren't swapping the draw queue before the last was run
			while (!rendered)
				renderedCondition.await();
			
			drawQueue = nextQueue;						// swap drawQueue for the new list of commands
			rendered = false;							// make sure the render thread runs the commands
			nextQueue = new ArrayList<IDrawCommand>();	// clear the next queue
			renderLock.unlock();
			
			mGLView.requestRender();					// request draw on render thread
		}
		else if (action.equals("createBuffer"))
		{
			// Map to the ID given from javascript
			nextQueue.add(renderer.new CreateBufferCommand(params.getInt(0)));
		}
		else if (action.equals("createProgram"))
		{
			nextQueue.add(renderer.new CreateProgramCommand(params.getInt(0)));
		}
		else if (action.equals("createShader"))
		{
			nextQueue.add(renderer.new CreateShaderCommand(params.getInt(1), params.getInt(0)));
		}
		else if (action.equals("createTexture"))
		{
			nextQueue.add(renderer.new CreateTextureCommand(params.getInt(0)));
		}
		else if (action.equals("getAttribLocation"))
		{
			nextQueue.add(renderer.new GetAttribLocationCommand(params.getInt(2), GetWebGLGapID(params, 0), params.getString(1)));
		}
		else if (action.equals("getUniformLocation"))
		{
			nextQueue.add(renderer.new GetUniformLocationCommand(params.getInt(2), GetWebGLGapID(params, 0), params.getString(1)));
		}
	}
	
	// Pause and resume the renderer
	public void onPause(boolean multitasking)
	{
		if (mGLView != null)
			mGLView.onPause();
	}
	
	public void onResume(boolean multitasking)
	{
		if (mGLView != null)
			mGLView.onResume();
	}
	
	public boolean isSynch(String action)
	{
		// all methods are synchronous
		return true;
	}
}

// The OpenGL ES 2 renderer
class C2OpenGLES20View extends GLSurfaceView {

	private WebGLGapPlugin webglgap;
	
    public C2OpenGLES20View(Context context, WebGLGapPlugin webglgap_, boolean alpha, boolean depth, boolean stencil){
        super(context);
        webglgap = webglgap_;
        
        setEGLContextClientVersion(2);				// OpenGL ES 2
        setEGLConfigChooser(8, 8, 8, alpha ? 8 : 0, depth ? 16 : 0, stencil ? 8 : 0);
        
        // Set the Renderer for drawing on the GLSurfaceView
        webglgap.renderer = new WebGLGapRenderer(webglgap);
        webglgap.renderer.amgr = webglgap.ctx.getAssets();
        setRenderer(webglgap.renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);		// Wait for the JS to call endRender (which calls requestRender)
    }
}