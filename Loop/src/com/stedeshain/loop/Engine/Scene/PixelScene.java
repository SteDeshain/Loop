package com.stedeshain.loop.Engine.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Component.Body.AbstractBody;
import com.stedeshain.loop.Engine.Component.Body.Role;
import com.stedeshain.loop.Engine.Utils.Level;
import com.stedeshain.loop.Engine.Utils.Utils;

public class PixelScene extends Scene
{
	// private int mOriginPixelHeight = 480;
	// private int mOriginPixelWidth = 800;

	private float mMetersPerPixel = 0;

	// temp
	Level level;
	Role player;

	public PixelScene(Game motherGame)
	{
		super(motherGame, new Vector2(50, 100));
	}

	@Override
	public void create()
	{
		super.create();

		this.addPhysicsModule();
		
		level = new Level(this, "pixel");
		level.init();

		mMetersPerPixel = level.getOriginMetersPerPixel();

		TextureRegion grayRegion = Utils.getColorTextureRegion(Color.GRAY, 1, 1);
		player = new Role(new Vector2(0, 0), new Vector2(0.5f, 1f), 0, 0, grayRegion)
		{
			@Override
			public void update(float deltaTime)
			{
				super.update(deltaTime);
				
				float maxVelocity = player.isGrounded() ? 3 : 1.5f;
				float impulse = player.isGrounded() ? 4 : 2;
				if(Gdx.input.isKeyPressed(Keys.A) && this.mBody.getLinearVelocity().x > -maxVelocity)
				{
					this.mBody.applyLinearImpulse(-impulse, 0, this.getPosition().x, this.getPosition().y, true);
				}
				else if((Gdx.input.isKeyPressed(Keys.D) || Gdx.app.getType() == ApplicationType.Android) && 
						this.mBody.getLinearVelocity().x < maxVelocity)
				{
					this.mBody.applyLinearImpulse(impulse, 0, this.getPosition().x, this.getPosition().y, true);
				}
				
				if(Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isTouched())
				{
					if(player.isGrounded())
						this.mBody.applyLinearImpulse(0, 25, this.getPosition().x, this.getPosition().y, true);
				}
			}
		};
		player.setTag("Player");
		player.setGroundDetectorHeight(0.2f);
		player.setGroundDetectorVerticalOffset(0.05f);
		player.setGroundDetectorHorizontalShrink(0.01f);
		player.setOwnAssets(true);
		player.setCenterOrigin();
		player.setDensityDef(10);
		player.setFixedRotationDef(true);
		player.setBodyTypeDef(BodyType.DynamicBody);
		player.setFrictionDef(0.9f);
		player.setRestitutionDef(0);
		player.setBulletDef(true);
		this.addComponent(player);
		adjustViewport();
	}

	private void adjustViewport()
	{
		OrthographicCamera camera = getCamera();
		
		if(this.getViewportFixedType() == ViewportFixedType.FixedHeight)
		{
			float aspectRatio = (float)camera.viewportWidth / (float)camera.viewportHeight;
			int originPixelHeight = (int)(camera.viewportHeight / mMetersPerPixel);
			int originPixelWidth = (int)(originPixelHeight * aspectRatio);
			camera.viewportHeight = (float)camera.viewportHeight / 
					((float)originPixelWidth / (float)Gdx.graphics.getWidth());
			camera.viewportWidth = camera.viewportHeight * aspectRatio;
			camera.update();
		}
		else if(this.getViewportFixedType() == ViewportFixedType.FixedWidth)
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
		}
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);

		adjustViewport();
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
			//Gdx.graphics.setWindowedMode(300, 400);
			//adjustViewport();
			this.getCamera().zoom = 0.5f;
			this.getCamera().update();
		}

		return false;
	}

	@Override
	public boolean onTouchUp(int screenX, int screenY, int pointer, int button)
	{
		this.getCamera().zoom = 0.25f;
		this.getCamera().update();
		
		return super.onTouchUp(screenX, screenY, pointer, button);
	}
}
