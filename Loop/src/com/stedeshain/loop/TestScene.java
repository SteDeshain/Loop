package com.stedeshain.loop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Component.Body.AbstractBody;
import com.stedeshain.loop.Engine.Component.Body.Role;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.stedeshain.loop.Engine.Utils.Level;
import com.stedeshain.loop.Engine.Utils.Utils;

public class TestScene extends Scene
{
	Label fpsLabel;
	Label scoreLabel;
	int score = 0;
	
	Level level;
	
	Role player;
	
	Array<AbstractBody> tempBodies = new Array<AbstractBody>();
	
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

		scoreLabel = new Label("Score: 0");
		scoreLabel.setViewportAnchor(0, 1);
		scoreLabel.setSourceAnchor(0, 1);
		this.addComponent(scoreLabel);
		
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
			
			@Override
			protected void onBeginContact(Fixture anotherFixture, Contact contact)
			{
				AbstractBody anotherBody = (AbstractBody)anotherFixture.getBody().getUserData();
				if(anotherBody.matchTag("coin"))
				{
					anotherBody.departFromScene();
					tempBodies.add(anotherBody);
					//anotherBody.dispose();
					score++;
					scoreLabel.setText("Score: " + score);
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
		/**/
		
		if(Gdx.app.getType() == ApplicationType.Android)
		{
			player.getBody().applyForceToCenter(0, 0, true);
		}
	}
	
	/**/
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		
		this.setCameraPosition(player.getPosition());
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
		else if(keycode == Keys.F)
		{
			this.getMotherGame().toogleDebugMode();
		}
		else if(keycode == Keys.G)
		{
			this.sortDrawables();
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

		for(int i = 0; i < tempBodies.size; i++)
		{
			tempBodies.get(i).dispose();
		}
		tempBodies.clear();
	}
}
