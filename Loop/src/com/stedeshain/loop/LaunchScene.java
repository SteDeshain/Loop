package com.stedeshain.loop;

import com.stedeshain.loop.Engine.Game;
//import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Scene.LoadScene;
import com.stedeshain.loop.Engine.Scene.Scene;

public class LaunchScene extends LoadScene
{
	public LaunchScene(Game motherGame, Scene nextScene)
	{
		super(motherGame, nextScene);
	}

	@Override
	public void create()
	{
		super.create();
		
//		Label loadingLabel = new Label("Loading...");
//		loadingLabel.setViewportAnchor(1, 0);
//		loadingLabel.setSourceAnchor(1, 0);
//		
//		addComponent(loadingLabel);
	}
}
