package com.stedeshain.loop;

import org.luaj.vm2.lib.ResourceFinder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Utils.*;

public class LoopMain extends com.stedeshain.loop.Engine.Game
{
	public LoopMain(String scriptPath, ResourceFinder androidLuaScriptFinder, int androidTouchSlop)
	{
		super(scriptPath, androidLuaScriptFinder, androidTouchSlop);
	}
	
	@Override
	public void create()
	{
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		this.setUIViewport(new Vector2(800, 480));
		this.setDebugMode(true);
		this.setHasDragBug(true);

		super.create();
		
		this.getGlobalAssets().loadTextureAtlas(Utils.getImageAssetPath("test.atlas"));
		this.getGlobalAssets().finishLoading();
		
		TestScene testScene = new TestScene(this, new Vector2(5, 10));
		//testScene.setBackColor(Color.MAGENTA);
		//first scene doesn't load any assets
		//testScene.addAssetToLoading(Utils.getImageAssetPath("test.atlas"), TextureAtlas.class);
		
		Game.sSetFirstScene(testScene);
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}
}
