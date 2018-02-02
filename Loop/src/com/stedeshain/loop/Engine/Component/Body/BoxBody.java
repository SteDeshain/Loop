package com.stedeshain.loop.Engine.Component.Body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sun.istack.internal.NotNull;

public class BoxBody extends AbstractBody
{	
	/**
	 * positive margin means body shrinks a little size from textureRegion's size
	 */
	private float mHorizontalBodyMargin = 0f;
	private float mVerticalBodyMargin = 0f;
	
	public BoxBody(@NotNull Vector2 position, float specifiedSize, boolean specifyWidth,
			@NotNull TextureRegion textureRegion)
	{
		this(position, specifiedSize, specifyWidth, 0, 0, textureRegion);
	}
	/**
	 * 
	 * @param position : center position
	 * @param specifiedSize : given one value of size (height or width), to calculate another value
	 * @param specifyWidth : true to specify width, false to specify height
	 * @param horizontalMargin
	 * @param verticalMargin
	 * @param textureRegion : Must NOT be null
	 */
	public BoxBody(@NotNull Vector2 position, float specifiedSize, boolean specifyWidth,
			float horizontalMargin, float verticalMargin,
			@NotNull TextureRegion textureRegion)
	{
		this(position, new Vector2(), horizontalMargin, verticalMargin, textureRegion);
		
		Vector2 size = new Vector2();
		if(specifyWidth) // use width to calculate height
		{
			size.x = specifiedSize;
			size.y = (float)textureRegion.getRegionHeight() / (float)textureRegion.getRegionWidth() * specifiedSize;
		}
		else // use height to calculate width
		{
			size.y = specifiedSize;
			size.x = (float)textureRegion.getRegionWidth() / (float)textureRegion.getRegionHeight() * specifiedSize;
		}
		size.add(mHorizontalBodyMargin * 2, mVerticalBodyMargin * 2);
		setSize(size);
		setCenterOrigin();
	}
	/**
	 * 
	 * @param position : center position
	 * @param size : body size
	 * @param textureRegion
	 */
	public BoxBody(@NotNull Vector2 position, @NotNull Vector2 size, TextureRegion textureRegion)
	{
		this(position, size, 0f, 0f, textureRegion);
	}
	public BoxBody(@NotNull Vector2 position, @NotNull Vector2 size,
			float horizontalMargin, float verticalMargin,
			TextureRegion textureRegion)
	{
		super(position, textureRegion);
		
		mHorizontalBodyMargin = horizontalMargin;
		mVerticalBodyMargin = verticalMargin;
		setPosition(position);
		size.add(mHorizontalBodyMargin * 2, mVerticalBodyMargin * 2);
		setSize(size);
		setCenterOrigin();
	}
	
	@Override
	public void create()
	{
		World world = getWorld();
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = mBodyTypeDef;
		bodyDef.fixedRotation = mFixedRotationDef;
		bodyDef.position.set(getPosition());
		bodyDef.bullet = mBulletDef;
		mBody = world.createBody(bodyDef);
		
		PolygonShape boxShape = new PolygonShape();
		final Vector2 size = getSize();
		boxShape.setAsBox(size.x / 2 - mHorizontalBodyMargin, size.y / 2 - mHorizontalBodyMargin);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = boxShape;
		fixtureDef.density = mDensityDef;
		fixtureDef.friction = mFrictionDef;
		fixtureDef.restitution = mRestitutionDef;
		mBody.createFixture(fixtureDef);
		
		boxShape.dispose();
	}

	@Override
	public void updatePhysics()
	{
		super.updatePhysics();
		
		final Vector2 position = mBody.getPosition();
		final Vector2 size = getSize();
		setPosition(position.x - size.x / 2, position.y - size.y / 2);
		setRotation(mBody.getAngle() * MathUtils.radiansToDegrees);
	}
	
	public float getHorizontalBodyMargin()
	{
		return mHorizontalBodyMargin;
	}
	public float getVerticalBodyMargin()
	{
		return mVerticalBodyMargin;
	}
}
