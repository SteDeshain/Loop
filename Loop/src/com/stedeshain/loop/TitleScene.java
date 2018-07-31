package com.stedeshain.loop;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.Button;
import com.stedeshain.loop.Engine.Component.Button.OnClickListener;
import com.stedeshain.loop.Engine.Component.ButtonGroup;
import com.stedeshain.loop.Engine.Component.CheckBox;
import com.stedeshain.loop.Engine.Component.CheckableButton;
import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Component.RadioButton;
import com.stedeshain.loop.Engine.Component.UIComponent;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.stedeshain.loop.Engine.Utils.Utils;

public class TitleScene extends Scene
{
	TestPixelScene testPixelScene;
	Label hint;
	Music bgm;
	
	public TitleScene(Game motherGame)
	{
		super(motherGame, null);
	}

	@Override
	public void create()
	{
		super.create();
		
		this.newLayer("test", 1, true);
		
		hint = new Label("Press anything to start");
		hint.setSourceAnchor(0.5f, 0.5f);
		hint.setViewportAnchor(0.5f, 0.3f);
		addComponent(hint);
		
		Button bt1 = new Button("Start", Utils.getDefaultFont());
		bt1.setSourceAnchor(0.5f, 0.5f);
		bt1.setViewportAnchor(0.5f, 0.1f);
		bt1.setPressedColor(Color.CYAN);
		bt1.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick()
				{
					TitleScene.this.startGame();
				}
			});
		addComponent(bt1, "test");
		
		TextureAtlas colors = this.getAssetsHelper().getTextureAtlas(Utils.getImageAssetPath("color_tiles.atlas"));
		CheckBox cb = new CheckBox("testBox", Utils.getDefaultFont(), colors.findRegion("yellow"), colors.findRegion("red"));
		cb.setSourceAnchor(0.5f, 0.5f);
		cb.setViewportAnchor(0.5f, 0.2f);
		addComponent(cb, "test");
		
		CheckBox cb1 = new CheckBox(colors.findRegion("white"), Color.CYAN, 5);
		cb1.setSize(100, 100);
		cb1.setSourceAnchor(0.5f, 0.5f);
		cb1.setViewportAnchor(0.7f, 0.3f);
		cb1.setLineColor(Color.RED);
		cb1.setLineWidth(5);
		addComponent(cb1, "test");
		
		ButtonGroup group = new ButtonGroup();
		
		RadioButton rb = new RadioButton("testRB1", Utils.getDefaultFont(), colors.findRegion("yellow"), colors.findRegion("red"));
		rb.setSourceAnchor(0.5f, 0.5f);
		rb.setViewportAnchor(0.3f, 0.2f);
		addComponent(rb, "test");
		group.addButton(rb);
		
		RadioButton rb1 = new RadioButton("testRB1", Utils.getDefaultFont(), colors.findRegion("yellow"), colors.findRegion("red"));
		rb1.setSourceAnchor(0.5f, 0.5f);
		rb1.setViewportAnchor(0.3f, 0.3f);
		addComponent(rb1, "test");
		group.addButton(rb1);
		
		RadioButton rb2 = new RadioButton("testRB2", Utils.getDefaultFont(), colors.findRegion("yellow"), colors.findRegion("red"));
		rb2.setSourceAnchor(0.5f, 0.5f);
		rb2.setViewportAnchor(0.3f, 0.4f);
		addComponent(rb2, "test");
		group.addButton(rb2);
		
		RadioButton rb3 = new RadioButton("testRB3", Utils.getDefaultFont(), colors.findRegion("yellow"), colors.findRegion("red"));
		rb3.setSourceAnchor(0.5f, 0.5f);
		rb3.setViewportAnchor(0.3f, 0.5f);
		addComponent(rb3, "test");
		group.addButton(rb3);
		
//		bgm = this.getAssetsHelper().getMusic(Utils.getMusicAssetPath("canon_in_d@authorbrian_crain.mp3"));
//		bgm.play();
		
		UIComponent title = new UIComponent(this.getAssetsHelper().getTextureAtlas(Utils.getImageAssetPath("loop.atlas")).findRegion("loop"));
		title.setHeightToFitRegion(180);
		title.setSourceAnchor(0.5f, 0.5f);
		title.setViewportAnchor(0.5f, 0.7f);
		addComponent(title);
		
		//Utils.debug("aaaa");
	}
	
	private void startGame()
	{
		if(getMotherGame().isTransiting())
			return;
		
		testPixelScene = new TestPixelScene(getMotherGame());
		testPixelScene.setFadeInDuration(0.5f);
		testPixelScene.addAssetToLoading(Utils.getImageAssetPath("color_tiles.atlas"), TextureAtlas.class);
		getMotherGame().beginScene(testPixelScene);
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
		//startGame();
		return false;
	}
	
	@Override
	public boolean onKeyReleased(int keycode)
	{
		startGame();
		return false;
	}
}
