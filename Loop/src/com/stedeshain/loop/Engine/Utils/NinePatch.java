package com.stedeshain.loop.Engine.Utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.Rectangle;

public class NinePatch
{
	public TextureRegion mDestRegion;
	
	//corner
	public Rectangle leftTop = new Rectangle();
	public Rectangle rightTop = new Rectangle();
	public Rectangle rightBottom = new Rectangle();
	public Rectangle leftBottom = new Rectangle();
	
	//stretchable line
	public Rectangle leftLine = new Rectangle();
	public Rectangle topLine = new Rectangle();
	public Rectangle rightLine = new Rectangle();
	public Rectangle bottomLine = new Rectangle();
	
	//center
	public Rectangle centerBox = new Rectangle();
	
	//
	public int left = 0;
	public int right = 0;
	public int top = 0;
	public int bottom = 0;
	
	public NinePatch(TextureRegion destRegion)
	{
		mDestRegion = destRegion;
		init();
	}

	private void init()
	{
		Pixmap pixmap = null;
		TextureData data = mDestRegion.getTexture().getTextureData();
		if(data instanceof FileTextureData)
		{
			//TODO need a better way to access the pixmap.
			//Maybe to load a PixmapTalas when loading textureAtlas at the beginning of scene.
			//Maybe use AssetLoaderParameters' callback method.
			pixmap = new Pixmap(((FileTextureData)data).getFileHandle());
		}
		else
		{
			throw new RuntimeException("Cannot create a NinePatch with a non-file texture.");
		}

		int regionX = mDestRegion.getRegionX();
		int regionY = mDestRegion.getRegionY();
		int regionWidth = mDestRegion.getRegionWidth();
		int regionHeight = mDestRegion.getRegionHeight();

		int startX = 0;
		int endX = 0;
		int color = 0xff00ffff;
		int lastColor = 0xffffff00;
		int black = Color.rgba8888(Color.BLACK);
		int white = Color.rgba8888(Color.WHITE);
		for(int i = 0; i < regionWidth; i++)
		{
			color = pixmap.getPixel(regionX + i, regionY);
			if(color == black && lastColor == white)
			{
				startX = i;
			}
			else if(color == white && lastColor == black)
			{
				endX = i;
				break;
			}
			
			lastColor = color;
		}
		int startY = 0;
		int endY = 0;
		color = 0xff00ffff;
		lastColor = 0x00000000;
		for(int i = 0; i < regionHeight; i++)
		{
			color = pixmap.getPixel(regionX, regionY + i);
			color = color & 0xffffffff;
			if(color == black && lastColor == white)
			{
				startY = i;
			}
			else if(color == white && lastColor == black)
			{
				endY = i;
				break;
			}
			
			lastColor = color;
		}
		
		left = startX;
		right = regionWidth - endX;
		top = startY;
		bottom = regionHeight - endY;
		calculateRectangles(startX, endX, startY, endY, regionX, regionY, regionWidth, regionHeight);
		
		pixmap.dispose();
	}

	private void calculateRectangles(int startX, int endX, int startY, int endY,
			int regionX, int regionY, int regionWidth, int regionHeight)
	{
		int finalX = regionX + 1;
		int finalY = regionY + 1;
		
		leftTop.set(finalX, finalY, startX, startY);
		rightTop.set(endX + finalX, finalY, regionWidth - endX, startY);
		rightBottom.set(endX + finalX, endY + finalY, regionWidth - endX, regionHeight - endY);
		leftBottom.set(finalX, endY + finalY, startX, regionHeight - endY);
		
		leftLine.set(finalX, startY + finalY, startX, endY - startY);
		topLine.set(startX + finalX, finalY, endX - startX, startY);
		rightLine.set(endX + finalX, startY + finalY, regionWidth - endX, endY - startY);
		bottomLine.set(startX + finalX, endY + finalY, endX - startX, regionHeight - endY);
		
		centerBox.set(startX + finalX, startY + finalY, endX - startX, endY - startY);
	}
}
