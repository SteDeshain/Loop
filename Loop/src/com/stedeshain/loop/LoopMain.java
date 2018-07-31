package com.stedeshain.loop;

import org.luaj.vm2.lib.ResourceFinder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Utils.Utils;

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
		Utils.setChckBtnPicTxtPadding(2);
		//this.setDebugMode(true);
		this.setHasDragBug(true);

		super.create();
		
		TitleScene title = new TitleScene(this);
		//title.setFadeInDuration(1f);
		//title.setFadeOutDuration(1f);
		//title.addAssetToLoading(Utils.getMusicAssetPath("canon_in_d@authorbrian_crain.mp3"), Music.class);
		//title.addAssetToLoading(Utils.getMusicAssetPath("µ­¡©ÅÝ¡© @author Foxtail-Grass Studio.mp3"), Music.class);
		title.addAssetToLoading(Utils.getImageAssetPath("loop.atlas"), TextureAtlas.class);
		title.addAssetToLoading(Utils.getImageAssetPath("color_tiles.atlas"), TextureAtlas.class);
		title.setOneTimeUsed(true);
		
		LaunchScene launchScene = new LaunchScene(this, title);
//		launchScene.setMinWaitingTime(1);
		launchScene.setOneTimeUsed(true);
		setFirstScene(launchScene);
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}
}
