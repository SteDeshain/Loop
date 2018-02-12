package com.stedeshain.loop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Component.Body.BoxBody;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.stedeshain.loop.Engine.Utils.Level;
import com.stedeshain.loop.Engine.Utils.Utils;

public class TestScene extends Scene
{
	Label fpsLabel;
	Level level;
	
	public TestScene(Game motherGame, Vector2 viewport)
	{
		super(motherGame, viewport);
	}
	
	@Override
	public void create()
	{
		super.create();

		this.addPhysicsModule();
		
		level = new Level(this, "test");
		level.init();
		
		fpsLabel = new Label("FPS:");
		fpsLabel.setViewportAnchor(1, 1);
		fpsLabel.setSourceAnchor(1, 1);
		this.addComponent(fpsLabel);
		
		TextureRegion grayRegion = Utils.getColorTextureRegion(Color.GRAY, 1, 1);
		BoxBody player = new BoxBody(new Vector2(0, 0), new Vector2(0.5f, 1f), grayRegion)
		{
			@Override
			public void update(float deltaTime)
			{
				super.update(deltaTime);
				
				if(Gdx.input.isKeyPressed(Keys.A) && this.mBody.getLinearVelocity().x > -2)
				{
					this.mBody.applyLinearImpulse(-4, 0, this.getPosition().x, this.getPosition().y, true);
				}
				else if(Gdx.input.isKeyPressed(Keys.D) && this.mBody.getLinearVelocity().x < 2)
				{
					this.mBody.applyLinearImpulse(4, 0, this.getPosition().x, this.getPosition().y, true);
				}
				
				if(Gdx.input.isKeyJustPressed(Keys.SPACE))
				{
					this.mBody.applyLinearImpulse(0, 25, this.getPosition().x, this.getPosition().y, true);
				}
			}
		};
		player.setOwnAssets(true);
		player.setCenterOrigin();
		player.setDensityDef(10);
		player.setFixedRotationDef(true);
		player.setBodyTypeDef(BodyType.DynamicBody);
		player.setFrictionDef(0.9f);
		player.setRestitutionDef(0);
		this.addComponent(player);
		/**/
	}
	
	/**/
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
	}
	/**/
	
	@Override
	public boolean onKeyPressed(int keycode)
	{
		if(keycode == Keys.LEFT)
		{
			this.addCameraPosition(-1, 0);
		}
		else if(keycode == Keys.RIGHT)
		{
			this.addCameraPosition(1, 0);
		}
		else if(keycode == Keys.UP)
		{
			this.addCameraPosition(0, 1);
		}
		else if(keycode == Keys.DOWN)
		{
			this.addCameraPosition(0, -1);
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
		
		return false;
	}
	
	@Override
	public boolean onKeyReleased(int keycode)
	{
		return false;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		level.dispose();
	}
}
