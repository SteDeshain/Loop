package com.stedeshain.loop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.Label;
import com.stedeshain.loop.Engine.Component.Body.BoxBody;
import com.stedeshain.loop.Engine.Component.Body.CircleBody;
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
		
		/**
		TextureAtlas atlas = getMotherGame().getGlobalAssets().getTextureAtlas(Utils.getImageAssetPath("test.atlas"));
		
		BoxBody platform01 = new BoxBody(new Vector2(-5.5f, -3.8f), 2.5f, false, atlas.findRegion("ground"));
		platform01.setRestitutionDef(0);
		platform01.setCenterOrigin();
		this.addComponent(platform01);
		
		float planeWidth = 0.5f;
		int planeCount = 5;
		for(int i = 0; i <= planeCount; i++)
		{
			float x = i * planeWidth + 1;
			BoxBody plane = new BoxBody(new Vector2(x, -3.8f), new Vector2(planeWidth, 2.5f), null);
			plane.setRestitutionDef(0);
			plane.setCenterOrigin();
			this.addComponent(plane);
		}
		
		Vector2[] vecs = new Vector2[planeCount];
		for(int i = 0; i < planeCount; i++)
		{
			//vecs[i] = new Vector2(i * planeWidth + 1, -1 - i * 0f);
			vecs[i] = new Vector2(i * planeWidth, 0);
		}
		ChainShape chain = new ChainShape();
		chain.createChain(vecs);
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(0, -1);
		Body chainBody = this.getPhysicsWorld().createBody(bodyDef);
		chainBody.createFixture(chain, 1);
		
		Vector2[] vecs2 = new Vector2[planeCount];
		for(int i = 0; i < planeCount; i++)
		{
			//vecs2[i] = new Vector2(i * planeWidth + 10, -1 - i * 0f);
			vecs2[i] = new Vector2(i * planeWidth - 1, 0);
		}
		ChainShape chain2 = new ChainShape();
		chain2.createChain(vecs2);
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyType.KinematicBody;
		bodyDef2.position.set((planeCount - 1) * planeWidth, -1);
		//bodyDef2.angle = Utils.getRadians(30);
		Body chainBody2 = this.getPhysicsWorld().createBody(bodyDef2);
		chainBody2.createFixture(chain2, 1);
		chainBody2.setAngularVelocity(0.1f);
		
		chain.dispose();
		chain2.dispose();
		
		//((ChainShape)(chainBody.getFixtureList().get(0).getShape())).createChain(null);
		
		BoxBody box = new BoxBody(new Vector2(0.5f, 0), new Vector2(0.5f, 0.5f), null);
		box.setBodyTypeDef(BodyType.DynamicBody);
		box.setBulletDef(true);
		box.setRestitutionDef(0);
		box.setCenterOrigin();
		
		BoxBody staticBox = new BoxBody(new Vector2(-3f, -1f), new Vector2(1.5f, 0.5f), null);
		staticBox.setRestitutionDef(0);
		staticBox.setCenterOrigin();

		TextureRegion grayRegion = Utils.getColorTextureRegion(Color.GRAY, 1, 1);
		BoxBody staticBox2 = new BoxBody(new Vector2(-2f, -1f), new Vector2(1.5f, 0.5f), 0.0f, 0.1f, grayRegion);
		staticBox2.setBodyTypeDef(BodyType.DynamicBody);
		staticBox2.setDensityDef(30);
		staticBox2.setRestitutionDef(0);
		staticBox2.setOriginFactor(0.4f, 0.3f);
		
		this.addComponent(box);
		this.addComponent(staticBox);
		this.addComponent(staticBox2);

		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.initialize(staticBox.getBody(), staticBox2.getBody(), 
				staticBox.getBody().getWorldPoint(new Vector2(0.5f, 0f)));
		jointDef.maxMotorTorque = 300f;
		jointDef.motorSpeed = 1;
		jointDef.enableMotor = true;
		this.getPhysicsWorld().createJoint(jointDef);

		/**/
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
