WebGLGap - Enable WebGL in PhoneGap
===================================

About
-----

Currently, HTML5 games don't perform very well on mobile.  WebGL in theory allows for high performance games by enabling low-level programming from javascript.  However, no mobile browsers currently support WebGL in their browsers (iOS actually supports it, but frustratingly have it disabled by default).

WebGLGap is a small research project by Scirra (www.scirra.com) to see if a PhoneGap plugin to enable WebGL would make for high performance HTML5 games.  It creates an OpenGL ES 2 layer and "simply" forwards all javascript calls to WebGL to the OpenGL ES 2 layer (with much hackery).  It's been written for and tested on Android 2.3.4 on a HTC Sensation.  However, I was unable to measure any performance benefit using WebGLGap over the Canvas 2D (which I understand is software-rendered).  I can't say for sure but I think the Android browser's javascript engine is so slow it negates any performance benefit from using hardware-accelerated rendering, and this is worsened by the fact PhoneGap fundamentally works by stringifying everything between the WebView and the plugin (even all your buffer data).  Still, I don't know much about this area, so perhaps someone out there can find a way to make it fast.

Only a very small subset of WebGL is supported (see webglgap.js for more) and it's full of hacks to fix "JNI local references overflow" and other weird stuff I encountered.  Still I'm open sourcing it since it might be useful for prototyping WebGL stuff on Android in anticipation of real support.  BSD license.

Set up
------

1.  Add to your plugins.xml (res\xml\plugins.xml in Eclipse):

	> <plugin name="WebGLGap" value="com.phonegap.plugin.webglgap.WebGLGapPlugin" />

2.  Add WebGLGapPlugin.java and WebGLGapRenderer.java from src\ to your project

3.  Include webglgap.js from src\ in your index.html, beneath the PhoneGap script, e.g.:

	> <script src="phonegap-1.3.0.js"></script>
	> <script src="webglgap.js"></script>

4.  The OpenGL context is displayed beneath the WebView PhoneGap makes.  I've written some code to try and make PhoneGap's WebView have a transparent background, but it doesn't seem to work - you need to add these two lines to the end of onCreate() to make sure the WebView doesn't obscure the OpenGL view.  Also note your index.html must not specify a background color otherwise the same happens.

	> appView.setBackgroundColor(0);
	> root.setBackgroundColor(0);
	
How to use
----------

WebGLGap is designed to work with minimal changes to existing WebGL code.

1.  Creating the context is done by

	> window.plugins.WebGLGap.createContext(); // can also pass attribs "alpha", "depth", "stencil"
	
	You probably want to detect if the browser supports WebGL natively first for future-proofing.  The example project uses this code:
	
	> gl = canvas.getContext("experimental-webgl");
	> if (!gl)
	> 	gl = window.plugins.WebGLGap.createContext({alpha: false, depth: true, stencil: false});
	
	The returned context mimics a real WebGL context.  See webglgap.js for supported methods.
	
2.  The Android browser doesn't support typed arrays, so you'll have to add fallbacks to standard arrays.  In the example the typed arrays were just removed and the arrays used directly.

3.  WebGLGap requires a call to gl.flush() at the end of each tick.  This is when WebGLGap sends all the queued commands off to the render thread.  You need to add a call to flush() if you don't have one already.  It won't break existing WebGL code.

4.  Some javascript code may still need tweaking.  Generally you shouldn't store things like Image objects or any other values in the objects returned by WebGL, since it can break the JSON stringifying.  The example had to be modified to get around this.

4.  There's lots of stuff not supported.  See Limitations.

Limitations
-----------

It's a crazy hack so start from the mindset that nothing works, then add:

1.  You only get a fullscreen context.  It's layered underneath the WebView.  You can't have anything inline to the page.  If you're making a game, this is what you want anyway.

2.  Performance is so-so, but probably better than trying to do the same in software.  My theory is too much stringifying/javascript going on.

3.  All GLEnums are supported (e.g. gl.SRC_COLOR, gl.ONE).

4.  A very limited subset of WebGL's functions are supported.  See webglgap.js for the full list.  Generally only just enough is supported to get some simple WebGL demos running.  No framebuffers, renderbuffers, extensions, stencils, and so on.  texImage2D only supports the overload taking a HTMLImageElement.  It should be straightforward in the source to see how to add new functions though, if you feel like taking it on.

Example
-------

The example is an Eclipse project from Windows with learningwebgl.com lesson 5 (http://learningwebgl.com/lessons/lesson05/index.html) ported to WebGLGap.  I've also successfully ported lessons 1-4 and 9 and one or two other demos I found on the web.

The end
-------

This is a few days worth of failed hacking, but hopefully it's somehow useful to you.  It's BSD license, feel free to use anywhere.  Let me know if you have improvements or if you know more about why performance isn't what I'd hoped (ashley@scirra.com).  CBA to try an iOS port since this one didn't work and Apple should just enable it anyway, they have it working for iAd.  Google, please hurry up and support WebGL in Android with a fast javascript engine :)