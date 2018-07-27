package com.stedeshain.loop.Engine.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Utils.Utils;

public class PixelScene extends Scene
{
	public static final int DEFAULT_PIXEL_WINDOW_WIDTH = 320;
	public static final int DEFAULT_PIXEL_WINDOW_HEIGHT = 240;
	
	public static final int MAX_PIXEL_SCALE = 8;
	public static final int MIN_PIXEL_SCALE = 1;
	
	private int mOriginWidth = DEFAULT_PIXEL_WINDOW_WIDTH;
	private int mOriginHeight = DEFAULT_PIXEL_WINDOW_HEIGHT;
	private int mPixelScale = 1;

	private float mMetersPerPixel = -1f;

	public PixelScene(Game motherGame)
	{
		super(motherGame, new Vector2(50, 100));
	}

	public int getOriginWidth()
	{
		return mOriginWidth;
	}
	public void setOriginWidth(int originWidth)
	{
		mOriginWidth = originWidth;
	}

	public int getOriginHeight()
	{
		return mOriginHeight;
	}
	public void setOriginHeight(int originHeight)
	{
		mOriginHeight = originHeight;
	}

	public int getPixelScale()
	{
		return mPixelScale;
	}
	public void setPixelScale(int pixelScale)
	{
		mPixelScale = MathUtils.clamp(pixelScale, MIN_PIXEL_SCALE, MAX_PIXEL_SCALE);

		//Do not resize window while game is running, it will cause pixel jittering
		//Gdx.graphics.setWindowedMode(mPixelScale * mOriginWidth, mPixelScale * mOriginHeight);
		getCamera().zoom = 1f / (float)mPixelScale;
		getCamera().update();
	}
	
	protected void setMetersPerPixel(float metersPerPixel)
	{
		if(metersPerPixel < 0)
			return;
		
		mMetersPerPixel = metersPerPixel;
	}

	protected void adjustViewport()
	{
		if(mMetersPerPixel < 0)
			return;
		
		OrthographicCamera camera = getCamera();
		
		if(getViewportFixedType() == ViewportFixedType.FixedHeight)
		{
			float aspectRatio = (float)camera.viewportWidth / (float)camera.viewportHeight;
			int originPixelHeight = (int)(camera.viewportHeight / mMetersPerPixel);
			int originPixelWidth = (int)(originPixelHeight * aspectRatio);
			camera.viewportHeight = (float)camera.viewportHeight / 
					((float)originPixelWidth / (float)Gdx.graphics.getWidth());
			camera.viewportWidth = camera.viewportHeight * aspectRatio;
			camera.update();
		}
		else if(getViewportFixedType() == ViewportFixedType.FixedWidth)
		{
			float aspectRatio = (float)camera.viewportHeight / (float)camera.viewportWidth;
			int originPixelWidth = (int)(camera.viewportWidth / mMetersPerPixel);
			int originPixelHeight = (int)(originPixelWidth * aspectRatio);
			camera.viewportWidth = (float)camera.viewportWidth /
					((float)originPixelHeight / (float)Gdx.graphics.getHeight());
			camera.viewportHeight = camera.viewportWidth * aspectRatio;
			camera.update();
		}
		else // fixed area
		{
			// TODO
			Utils.error("fixed area adjustViewport not implemented yet");
		}
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);

		adjustViewport();
	}
}
