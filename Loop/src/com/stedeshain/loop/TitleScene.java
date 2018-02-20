package com.stedeshain.loop;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Component.UIComponent;
import com.stedeshain.loop.Engine.Scene.UIScene;
import com.stedeshain.loop.Engine.Utils.Utils;

public class TitleScene extends UIScene
{
	TestScene testScene;
	Label hint;
	Music bgm;
	
	public TitleScene(Game motherGame)
	{
		super(motherGame);
	}

	@Override
	public void create()
	{
		super.create();
		
		hint = new Label("Press anything to start");
		hint.setSourceAnchor(0.5f, 0.5f);
		hint.setViewportAnchor(0.5f, 0.3f);
		addComponent(hint);
		
//		bgm = this.getAssetsHelper().getMusic(Utils.getMusicAssetPath("Canon in D @author Brian Crain.mp3"));
//		bgm.play();
		
		UIComponent title = new UIComponent(this.getAssetsHelper().getTextureAtlas(Utils.getImageAssetPath("loop.atlas")).findRegion("loop"));
		title.setHeightToFitRegion(180);
		title.setSourceAnchor(0.5f, 0.5f);
		title.setViewportAnchor(0.5f, 0.7f);
		addComponent(title);
	}
	
	private void startGame()
	{
		if(getMotherGame().isTransiting())
			return;
		
		testScene = new TestScene(getMotherGame(), new Vector2(5, 10));
		testScene.setFadeInDuration(0.5f);
		getMotherGame().beginScene(testScene);
	}
	
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		//hint.setText(String.format("%1.3f", getMotherGame().getTransitionProgress()));
//		if(getMotherGame().isTransiting())
//		{
//			bgm.setVolume(getMotherGame().getTransitionProgress());
//		}
	}
	
	@Override
	public boolean onTouchUp(int screenX, int screenY, int pointer, int button)
	{
		startGame();
		return false;
	}
	
	@Override
	public boolean onKeyReleased(int keycode)
	{
		startGame();
		return false;
	}
}
