package com.stedeshain.loop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.CheckBox;
import com.stedeshain.loop.Engine.Component.CheckableButton.OnCheckListener;
import com.stedeshain.loop.Engine.Component.CheckableButton.OnDecheckListener;
import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Component.Body.AbstractBody;
import com.stedeshain.loop.Engine.Component.Body.OneSidedPlatform;
import com.stedeshain.loop.Engine.Component.Body.Role;
import com.stedeshain.loop.Engine.Scene.PixelScene;
import com.stedeshain.loop.Engine.Utils.Level;
import com.stedeshain.loop.Engine.Utils.Utils;

public class TestPixelScene extends PixelScene
{
	// temp
	Level level;
	Role player;

	Label fps;
	Label grounded;
	OneSidedPlatform p;
	OneSidedPlatform p2;

	public TestPixelScene(Game motherGame)
	{
		super(motherGame);
	}

	@Override
	public void create()
	{
		super.create();

		// oneTime();
		// twoTime();
		// threeTime();

		this.addPhysicsModule();

		this.newLayer("main", 1, false);
		this.newLayer("debug_ui", 0, true);

		level = new Level(this, "pixel1");
		level.init();
		setMetersPerPixel(level.getOriginMetersPerPixel());

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
				else if((Gdx.input.isKeyPressed(Keys.D) || Gdx.app.getType() == ApplicationType.Android)
						&& this.mBody.getLinearVelocity().x < maxVelocity)
				{
					this.mBody.applyLinearImpulse(impulse, 0, this.getPosition().x, this.getPosition().y, true);
				}

				if(Gdx.input.isKeyJustPressed(Keys.SPACE))// ||
															// Gdx.input.isTouched())
				{
					if(!Gdx.input.isKeyPressed(Keys.DOWN))
					{
						if(player.isGrounded())
							this.mBody.applyLinearImpulse(0, 30, this.getPosition().x, this.getPosition().y, true);
					}
					else
					{
						// FIXME implement Role.getStandingTerrain()
						this.mBody.setAwake(true);
						AbstractBody terrain = this.getStandingTerrain();
						if(terrain instanceof OneSidedPlatform)
						{
							((OneSidedPlatform)terrain).letThrougn(this.getMainFixture());
						}
					}
				}
			}
		};
		player.setTag("Player");
		player.setGroundDetectorHeight(0.1f);
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
		this.addComponent(player, "main");

		fps = new Label("FPS: ");
		fps.setViewportAnchor(1, 1);
		fps.setSourceAnchor(1, 1);
		this.addComponent(fps, "debug_ui");

		TextureAtlas colors = this.getAssetsHelper().getTextureAtlas(Utils.getImageAssetPath("color_tiles.atlas"));
		CheckBox cb = new CheckBox("ShowBounds", Utils.getDefaultFont(), colors.findRegion("white"),
				colors.findRegion("red"));
		cb.setSourceAnchor(1, 1);
		cb.setViewportAnchor(1, 0.9f);
		cb.setOnCheckListener(new OnCheckListener()
		{
			@Override
			public void onCheck()
			{
				TestPixelScene.this.getMotherGame().setDebugMode(true);
			}
		});
		cb.setOnDecheckListener(new OnDecheckListener()
		{
			@Override
			public void onDecheck()
			{
				TestPixelScene.this.getMotherGame().setDebugMode(false);
			}
		});
		this.addComponent(cb, "debug_ui");

		grounded = new Label("Grounded: ");
		grounded.setViewportAnchor(1, 0.96f);
		grounded.setSourceAnchor(1, 1);
		this.addComponent(grounded, "debug_ui");

		p = new OneSidedPlatform(new Vector2(0, -2), new Vector2(2, 0.2f), 0, 0, grayRegion);
		p.setRestitutionDef(0);
		p.setBulletDef(true);
		// p.setAngleDegreeDef(5);
		this.addComponent(p, "main");

		p2 = new OneSidedPlatform(new Vector2(-2, -1.8f), new Vector2(2, 0.2f), 0, 0, grayRegion);
		p2.setRestitutionDef(0);
		p2.setBulletDef(true);
		p2.setDegreeAngleDef(180);
		this.addComponent(p2, "main");

		this.restrictCameraHorizontal();
		this.restrictCameraVertical(-1, 2);
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
		else if(keycode == Keys.ENTER)
		{
			follow = !follow;
		}

		return false;
	}

	@Override
	public boolean onTouchUp(int screenX, int screenY, int pointer, int button)
	{
		// this.setPixelScale(2);

		return super.onTouchUp(screenX, screenY, pointer, button);
	}

	boolean follow = true;

	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);

		if(follow)
			this.setCameraPosition(player.getPosition());

		fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		grounded.setText("Grounded: " + player.isGrounded());
	}
}
