package com.stedeshain.loop.Engine.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Utils.Level;

public class PixelScene extends Scene
{	
	private int mOriginPixelHeight = 480;
	private int mOriginPixelWidth = 800;

	private int mPixelPerMeter = 16;
	
	//temp
	Level level;
	
	public PixelScene(Game motherGame)
	{
		super(motherGame, new Vector2(50, 100));
	}
	
	@Override
	public void create()
	{
		super.create();
		
		OrthographicCamera camera = getCamera();
		
		//temp
		this.addPhysicsModule();
		
		Label temp = new Label("temp");
		temp.setSourceAnchor(1, 1);
		temp.setViewportAnchor(1, 1);
		addComponent(temp);

		level = new Level(this, "pixel");
		level.init();
		
		mOriginPixelWidth = level.getOriginScreenWidth();
		mOriginPixelHeight = level.getOriginScreenHeight();
		
		if(Gdx.graphics.getWidth() < mOriginPixelWidth && Gdx.graphics.getHeight() < mOriginPixelHeight)
		{
			//do nothing
		}
		else
		{
//			if(this.getViewportFixedType() == ViewportFixedType.FixedWidth)
			if(this.getViewportFixedType() == ViewportFixedType.FixedHeight)
			{
				int pixelTimes = Gdx.graphics.getWidth() / mOriginPixelWidth;
				pixelTimes = pixelTimes == 0 ? 1 : pixelTimes;
				float whRatio = (float)getCamera().viewportWidth / (float)getCamera().viewportHeight;
				//mOriginPixelHeight = (int)(mOriginPixelWidth / whRatio);
				getCamera().viewportWidth = (float)getCamera().viewportWidth / 
						((float)(mOriginPixelWidth) / (float)Gdx.graphics.getWidth());
				getCamera().viewportHeight = getCamera().viewportWidth / whRatio;
				getCamera().update();
			}
			else if(this.getViewportFixedType() == ViewportFixedType.FixedHeight)
			{
				
			}
			else	//fixed area
			{
				//TODO
			}
		}
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
	}

	public float getMeterPerPixel()
	{
		return 1f / (float)mPixelPerMeter;
	}
	public int getPixelPerMeter()
	{
		return mPixelPerMeter;
	}
	public void setPixelPerMeter(int pixelPerMeter)
	{
		mPixelPerMeter = pixelPerMeter;
	}

	public int getOriginPixelHeight()
	{
		return mOriginPixelHeight;
	}
	public void setOriginPixelHeight(int originPixelHeight)
	{
		mOriginPixelHeight = originPixelHeight;
	}

	public int getOriginPixelWidth()
	{
		return mOriginPixelWidth;
	}
	public void setOriginPixelWidth(int originPixelWidth)
	{
		mOriginPixelWidth = originPixelWidth;
	}
	
	@Override
	public void dispose()
	{
		level.dispose();
	}
	
	@Override
	public boolean onKeyPressed(int keycode)
	{
		float distance = 0.5f;
		if(keycode == Keys.LEFT)
		{
			this.addCameraPosition(-distance, 0);
		}
		else if(keycode == Keys.RIGHT)
		{
			this.addCameraPosition(distance, 0);
		}
		else if(keycode == Keys.UP)
		{
			this.addCameraPosition(0, distance);
		}
		else if(keycode == Keys.DOWN)
		{
			this.addCameraPosition(0, -distance);
		}
		else if(keycode == Keys.PERIOD)
		{
			this.zoomInCamera();
		}
		else if(keycode == Keys.COMMA)
		{
			this.zoomOutCamera();
		}
		else if(keycode == Keys.SLASH)
		{
			this.clearCameraPosition();
		}
		else if(keycode == Keys.F)
		{
			this.getMotherGame().toogleDebugMode();
		}
		else if(keycode == Keys.G)
		{
			Gdx.graphics.setWindowedMode(100, 100);
		}
		
		return false;
	}
	
}
