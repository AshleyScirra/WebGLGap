package com.phonegap.webglgapexample;

import android.os.Bundle;
import com.phonegap.*;

public class App extends DroidGap {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
        appView.setBackgroundColor(0);
        root.setBackgroundColor(0);
    }
}
