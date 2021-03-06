package com.stedeshain.loop.Engine.Component.Body;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.sun.istack.internal.NotNull;

public class CircleBody extends AbstractBody
{
	private float mBodyMargin = 0f;

	/**
	 * @param position : center position
	 * @param radius
	 * @param textureRegion
	 */
	public CircleBody(@NotNull Vector2 position, float radius, TextureRegion textureRegion)
	{
		this(position, radius, 0f, textureRegion);
	}
	public CircleBody(@NotNull Vector2 position, float radius, float margin, TextureRegion textureRegion)
	{
		super(position, textureRegion);

		mBodyMargin = margin;
		setPosition(position);
		setSize((radius + mBodyMargin) * 2, (radius + mBodyMargin) * 2);
	}

	@Override
	public void create()
	{
		World world = getWorld();
		if(world == null)
			return;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = mBodyTypeDef;
		bodyDef.angle = mAngleDef;
		bodyDef.fixedRotation = mFixedRotationDef;
		bodyDef.position.set(getPosition());
		bodyDef.bullet = mBulletDef;
		mBody = world.createBody(bodyDef);
		
		CircleShape circleShape = new CircleShape();
		Vector2 size = getSize();
		Vector2 origin = getOrigin();
		float radius = size.x / 2 - mBodyMargin;
		circleShape.setRadius(radius);
		circleShape.setPosition(new Vector2(radius - origin.x - mBodyMargin, radius - origin.y - mBodyMargin));

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = mDensityDef;
		fixtureDef.friction = mFrictionDef;
		fixtureDef.restitution = mRestitutionDef;
		fixtureDef.filter.categoryBits = mCategoryBits;
		fixtureDef.filter.maskBits = mMaskBits;
		fixtureDef.filter.groupIndex = mGroupIndex;
		fixtureDef.isSensor = mIsSensor;
		mMainFixture = mBody.createFixture(fixtureDef);
		
		circleShape.dispose();
		
		registerGeneralContactEvent();
		
		registerUserData();
	}

	public float getBodyMargin()
	{
		return mBodyMargin;
	}

	@Override
	public void updatePhysics()
	{
		super.updatePhysics();

		if(mBody == null)
			return;
		final Vector2 position = mBody.getPosition();
		setPosition(position.x - getOrigin().x, position.y - getOrigin().y);
		setRotation(mBody.getAngle() * MathUtils.radiansToDegrees);
	}
}
