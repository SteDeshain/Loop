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
		setSize(radius + mBodyMargin * 2, radius + mBodyMargin * 2);
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
		
		CircleShape circleShape = new CircleShape();
		Vector2 size = getSize();
		circleShape.setRadius(size.x / 2 - mBodyMargin);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = mDensityDef == -1 ? AbstractBody.DEFAULT_DENSITY : mDensityDef;
		fixtureDef.friction = mFrictionDef == -1 ? AbstractBody.DEFAULT_FRICTION : mFrictionDef;
		fixtureDef.restitution = mRestitutionDef == -1 ? AbstractBody.DEFAULT_RESTITUTION : mRestitutionDef;
		mBody.createFixture(fixtureDef);
		
		circleShape.dispose();
	}

	public float getBodyMargin()
	{
		return mBodyMargin;
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
}
