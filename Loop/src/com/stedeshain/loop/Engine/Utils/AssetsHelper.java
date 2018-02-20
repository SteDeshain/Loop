package com.stedeshain.loop.Engine.Utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetsHelper extends AssetManager
{
	public AssetsHelper()
	{
		super();
	}
	
	public TextureAtlas getTextureAtlas(String fileName)
	{
		return get(fileName, TextureAtlas.class);
	}
	public Music getMusic(String fileName)
	{
		return get(fileName, Music.class);
	}
	
	public void loadTexture(String fileName)
	{
		super.load(fileName, Texture.class);
	}
	public void loadTextureAtlas(String fileName)
	{
		super.load(fileName, TextureAtlas.class);
	}
	
	public void loadSound(String fileName)
	{
		super.load(fileName, Sound.class);
	}
	public void loadMusic(String fileName)
	{
		super.load(fileName, Music.class);
	}
}
