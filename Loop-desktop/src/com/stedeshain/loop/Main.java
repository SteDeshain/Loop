package com.stedeshain.loop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Loop";
		cfg.width = 1350;
		cfg.height = 700;
		cfg.width = 800;
		cfg.height = 480;
		cfg.allowSoftwareMode = true;
		cfg.resizable = false;
		cfg.x = -1;
		cfg.y = -1;
		cfg.vSyncEnabled = true;
		
		new LwjglApplication(new LoopMain("../Loop-android/assets/scripts/?.lua", null, 0), cfg);
	}
}
