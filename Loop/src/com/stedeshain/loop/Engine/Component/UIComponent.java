package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Scene.Scene;

public class UIComponent extends DrawableComponent
{
	/**
	 * if mViewportAnchor == null, draw() will use a static position,
	 * otherwise, draw() will use a dynamic position calculating by mViewportAnchor
	 */
	private Vector2 mViewportAnchor = null;
	
	/**
	 * if mSourceAnchor == null, draw() will use mOrigin to calculate destination position,
	 * and scaling or rotation operation will still be based on mOrigin, so three things all
	 * are rely on that mOrigin, sometimes this will cause some problem user doesn't want.
	 * And if mSourceAnchor is not null, calculating destination position will only rely on
	 * mSourceAnchor, and separately scaling and rotation operation will be based on mOrigin
	 */
	private Vector2 mSourceAnchor = null;
	
	protected Vector2 mLeftBottomPosition = new Vector2();
	protected boolean mLeftBottomUpdated = false;
	
	public UIComponent()
	{
		super();
	}
	
	public UIComponent(TextureRegion textureRegion)
	{
		super(textureRegion);
	}
	
	public void resize(int width, int height)
	{
		mLeftBottomUpdated = false;
	}
	
	/**
	 * UIComponent draw() use mOrigin as an anchor in source,
	 * and will put source's mOrigin point at the viewport's mPosition
	 * when drawing source.
	 */
	@Override
	public void draw(SpriteBatch batch)
	{
		if(getTextureRegion() == null)
			return;

		calculateLeftBottom();
		batch.draw(getTextureRegion(), 
				mLeftBottomPosition.x, mLeftBottomPosition.y, 
				getOrigin().x, getOrigin().y, 
				getSize().x, getSize().y, 
				getScale().x, getScale().y, 
				getRotation());
	}

	protected void calculateLeftBottom()
	{
		if(mLeftBottomUpdated)
			return;
		
		float offsetX, offsetY;
		if(mSourceAnchor == null)
		{
			offsetX = getOrigin().x;
			offsetY = getOrigin().y;
		}
		else
		{
			//TODO should take scaling and rotation affection in count ?
			offsetX = mSourceAnchor.x * getSize().x;
			offsetY = mSourceAnchor.y * getSize().y;
		}
		
		if(mViewportAnchor == null)
		{
			mLeftBottomPosition.x = getPosition().x - offsetX;
			mLeftBottomPosition.y = getPosition().y - offsetY;
		}
		else
		{
			float viewportWidth = getMotherScene().getUICamera().viewportWidth;
			float viewportHeight = getMotherScene().getUICamera().viewportHeight;
			mLeftBottomPosition.x = mViewportAnchor.x * viewportWidth - viewportWidth / 2 - offsetX;
			mLeftBottomPosition.y = mViewportAnchor.y * viewportHeight - viewportHeight / 2 - offsetY;
		}
		
		mLeftBottomUpdated = true;
	}
	
	//TODO necessary?  
	public void setLeftBottomPosition(float left, float bottom)
	{
		/**
		mLeftBottomPosition.x = left;
		mLeftBottomPosition.y = bottom;
		float offsetX, offsetY;
		if(mViewportAnchor == null)
		{
			offsetX = getPosition().x - mLeftBottomPosition.x;
			offsetY = getPosition().y - mLeftBottomPosition.y;
		}
		else
		{
			float viewportWidth = getMotherScene().getUICamera().viewportWidth;
			float viewportHeight = getMotherScene().getUICamera().viewportHeight;
			offsetX = mViewportAnchor.x * viewportWidth - viewportWidth / 2 - mLeftBottomPosition.x;
			offsetY = mViewportAnchor.y * viewportHeight - viewportHeight / 2 - mLeftBottomPosition.y;
		}
		
		if(mSourceAnchor == null)
		{
			setOrigin(offsetX, offsetY);
		}
		else
		{
			mSourceAnchor.x = offsetX / getSize().x;
			mSourceAnchor.y = offsetY / getSize().y;
		}
		/**/

		mLeftBottomUpdated = true;
	}
	
	public Vector2 getLeftBottomPosition()
	{
		//TODO necessary ?
		mLeftBottomUpdated = false;
		
		calculateLeftBottom();
		return mLeftBottomPosition;
	}

	public Vector2 getDimensionFactor()
	{
		return new Vector2(getSize().x / getMotherScene().getUICamera().viewportWidth,
				getSize().y / getMotherScene().getUICamera().viewportHeight);
	}

	public Vector2 getViewportAnchor()
	{
		return mViewportAnchor;
	}
	/**
	 * if set a mViewportAnchor not null value, draw() will dynamic calculate destination position,
	 * and ignoring mPosition
	 * @param viewportAnchor
	 */
	public void setViewportAnchor(Vector2 viewportAnchor)
	{
		mViewportAnchor = viewportAnchor;
		mLeftBottomUpdated = false;
	}
	
	public void setViewportAnchor(float xAnchor, float yAnchor)
	{
		setViewportAnchor(new Vector2(xAnchor, yAnchor));
	}

	public Vector2 getSourceAnchor()
	{
		return mSourceAnchor;
	}
	public void setSourceAnchor(Vector2 sourceAnchor)
	{
		mSourceAnchor = sourceAnchor;
		mLeftBottomUpdated = false;
	}
	public void setSourceAnchor(float xAnchor, float yAnchor)
	{
		setSourceAnchor(new Vector2(xAnchor, yAnchor));
	}
	
	@Override
	public void setPosition(Vector2 position)
	{
		super.setPosition(position);
		mLeftBottomUpdated = false;
	}
	@Override
	public void setSize(Vector2 size)
	{
		super.setSize(size);
		mLeftBottomUpdated = false;
	}
	@Override
	public void setOrigin(Vector2 origin)
	{
		super.setOrigin(origin);
		mLeftBottomUpdated = false;
	}
	@Override
	public void setScale(Vector2 scale)
	{
		super.setScale(scale);
		mLeftBottomUpdated = false;
	}
	@Override
	public void setRotation(float rotation)
	{
		super.setRotation(rotation);
		mLeftBottomUpdated = false;
	}
	@Override
	public void setTextureRegion(TextureRegion textureRegion)
	{
		super.setTextureRegion(textureRegion);
		mLeftBottomUpdated = false;
	}

	@Override
	public void setMotherScene(Scene motherScene)
	{
		mLeftBottomUpdated = false;
		super.setMotherScene(motherScene);
	}
	
	public boolean hit(float x, float y)
	{
		if(!isEnable())
			return false;
		
		final Vector2 leftBottom = getLeftBottomPosition();
		float left = leftBottom.x;
		float bottom = leftBottom.y;
		final Vector2 size = getSize();
		float width = size.x;
		float height = size.y;
		if(x >= left && x <= left + width && y >= bottom && y <= bottom + height)
			return true;
		else
			return false;
	}
	public boolean hit(Vector2 position)
	{
		return hit(position.x, position.y);
	}
	
	/**
	 * Override it !
	 * @param event
	 * @return whether this event is handled by this UIComponent
	 */
	public boolean fire(InputEvent event)
	{
		return false;
	}
	
	/**
	 * Override this method on every UI whose state may be affected by the InputEvent
	 */
	public void clearInputEvent() {}
}
