package com.icl.digiboard.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.icl.digiboard.DigiBoardMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Digital Notice Board";
		/*config.width = 1366;
		config.height = 768;
        config.fullscreen = true;*/
		config.width = 800;
		config.height = 600;
		new LwjglApplication(new DigiBoardMain(), config);
	}
}
