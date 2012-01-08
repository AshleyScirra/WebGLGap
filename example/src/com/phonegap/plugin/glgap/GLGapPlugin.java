// GLGap
// Copyright (c) 2012 Scirra Ltd
// Written by Ashley Gullen
package com.phonegap.plugin.glgap;


import org.json.JSONArray;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.phonegap.DroidGap;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;
import com.phonegap.plugin.glgap.GLGapPlugin;
import com.phonegap.plugin.glgap.GLGapRenderer;

public class GLGapPlugin extends Plugin {

	// The OpenGL ES 2 surface view and its renderer
	public GLSurfaceView mGLView = null;
	public GLGapRenderer renderer = null;
	
	@Override
	public PluginResult execute(String action, JSONArray params, String callbackId) {
		
		PluginResult result = new PluginResult(Status.OK);
		
		try {
			if (action.equals("commandBatch"))
			{
				//Log.i("GLGap", params.getString(0));
				
				renderer.renderLock.lock();
	
				// wait to ensure the last drawQueue has been rendered by the renderer so we
				// aren't swapping the draw queue before the last was run
				while (!renderer.rendered)
					renderer.renderedCondition.await();
				
				renderer.renderCommand = params.getString(0);
				renderer.rendered = false;
				renderer.renderLock.unlock();
				
				mGLView.requestRender();					// request draw on render thread
			}
			else if (action.equals("create"))
			{			
				final GLGapPlugin me = this;
				Log.i("GLGap", "Creating OpenGL view...");
				
				Runnable startup = new Runnable() {
					public void run() {
						// Create the OpenGL ES 2 view and add it as another content view
				        me.mGLView = new C2OpenGLES20View(me.ctx, me);
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
				        
				        Log.i("GLGap", "Created OpenGL view");
					}
				};
				
				ctx.runOnUiThread(startup);
			}
		}
		catch (Exception e)
		{
			Log.e("GLGap", e.toString());
		}
		
		return result;
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

//The OpenGL ES 2 renderer
class C2OpenGLES20View extends GLSurfaceView {

	private GLGapPlugin glgap;
	
	public C2OpenGLES20View(Context context, GLGapPlugin glgap_){
	     super(context);
	     glgap = glgap_;
	     
	     setEGLContextClientVersion(2);				// OpenGL ES 2
	     setEGLConfigChooser(8, 8, 8, 8, 0, 0);
	     
	     // Set the Renderer for drawing on the GLSurfaceView
	     glgap.renderer = new GLGapRenderer(glgap);
	     glgap.renderer.amgr = glgap.ctx.getAssets();
	     setRenderer(glgap.renderer);
	     setRenderMode(RENDERMODE_WHEN_DIRTY);		// Wait for the JS to call endRender (which calls requestRender)
	}
}