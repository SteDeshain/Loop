package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IdentityMap;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.stedeshain.loop.Engine.Utils.Constants;
import com.stedeshain.loop.Engine.Utils.NinePatch;
import com.stedeshain.loop.Engine.Utils.Utils;
import com.sun.istack.internal.NotNull;

//TODO mute some useless method ...
public class Selector extends UIComponent
{
	private NinePatch mNinePatch = null;
	/**
	 * shrinkable size
	 */
	private float mMargin = 0f;
	/**
	 * padding inside
	 */
	private float mPadding = 0f;

	private float mCornerViewportLeftWidth = -1f;
	private float mCornerViewportRightWidth = -1f;
	private float mCornerViewportTopHeight = -1f;
	private float mCornerViewportBottomHeight = -1f;
	
	private Selectable mSelectedComponent = null;
	private int mAttachedPointer = InputEvent.INVALID_POINTER;
	
	private OnSelectListener mOnSelectListener = null;
	
	//animation related properties
	private float mShowingTime = 0f;
	private float mMovingTime = 0f;
	private float mShrinkingTime = 0f;

	//destination value
	//moving animation
	private Vector2 mDestSize = new Vector2();
	private Vector2 mDestCenterAnchor = new Vector2();
	//hiding or showing
	private float mHidingExpandWidthFactor = 0f;
	private float mHidingExpandHeightFactor = 0f;
	
	//framing value
	//shrinking animation
	private float mShrinkingWidth = 0f;
	private float mShrinkingHeight = 0f;
	//opaque animation
	private float mTransparency = 1f;
	
	//amounting time
	private float mMovingAnimationTime = 0f;
	
	//flag value
	private boolean mIsShrinking = false;
	private boolean mIsExpanding = false;
	
	private boolean mIsShowing = false;
	private boolean mIsHiding = false;
	
	/**
	 * Stores scenes that already has a Selector
	 */
	private static IdentityMap<Scene, Selector> sSelectors = new IdentityMap<Scene, Selector>();
	
	//TODO a selector may exists in some Layer rather than the default UI Layer
	public static Selector generateSelector(@NotNull Scene scene, @NotNull TextureRegion region, float padding)
	{
		if(!sSelectors.containsKey(scene))
		{
			Selector instance = new Selector(region, padding);
			instance.setDepthToBottom();
			instance.setSourceAnchor(0.5f, 0.5f);
//			scene.insertComponent(instance, 0);
			//TODO here
			scene.addComponent(instance);
			sSelectors.put(scene, instance);
			return instance;
		}
		else
			return null;
	}
	
	public static void cancelScene(Scene scene)
	{
		Selector selector = sSelectors.get(scene);
		sSelectors.remove(scene);
		scene.appendRemovingComponent(selector);
	}
	
	public static Selector getSelector(Scene scene)
	{
		return sSelectors.get(scene, null);
	}
	
	private Selector(TextureRegion region, float margin)
	{
		super(region);
		
		mNinePatch = new NinePatch(region);
		setMargin(margin);
	}
	
	public boolean setSelected(Selectable component, int pointer)
	{
		if(component == null || mSelectedComponent == component)
			return false;
		
		if(mAttachedPointer != InputEvent.INVALID_POINTER && mAttachedPointer != pointer)
			return false;
		
		boolean firstSet = false;
		if(mSelectedComponent != null)
			mSelectedComponent.deselect();
		else
			firstSet = true;
		mSelectedComponent = component;
		mSelectedComponent.select();
		mAttachedPointer = pointer;
		final Vector2 selectedSize = mSelectedComponent.getSize();
		final Vector2 selectedAnchor = mSelectedComponent.getCenterAnchor();
		setSourceAnchor(0.5f, 0.5f);
		if(firstSet)
		{
			setSize(selectedSize.x + mMargin + mPadding, selectedSize.y + mMargin + mPadding);
			setViewportAnchor(selectedAnchor);
		}
		//start animating
		//setSize(selectedSize.x + mPadding, selectedSize.y + mPadding);
		//setViewportAnchor(selectedAnchor);
		mDestSize.set(selectedSize.x + mMargin + mPadding, selectedSize.y + mMargin + mPadding);
		mDestCenterAnchor.set(selectedAnchor);
		
		if(!isVisible())
		{
			startShowing();
		}
		
		if(mOnSelectListener != null)
		{
			mOnSelectListener.onSelect();
		}
		
		return true;
	}
	public Selectable getSelected()
	{
		return mSelectedComponent;
	}
	
	public void setDefaultAnimation(boolean hasAnimation)
	{
		if(hasAnimation)
		{
			setMovingTime(Constants.SELECTOR_DEFAULT_MOVING_TIME);
			setShrinkingTime(Constants.SELECTOR_DEFAULT_SHRINKING_TIME);
			setShowingTime(Constants.SELECTOR_DEFAULT_SHOWING_TIME);
			setHidingExpandWidthFactor(Constants.SELECTOR_DEFAULT_HIDING_EXPAND_WIDTH_FACTOR);
			setHidingExpandHeightFactor(Constants.SELECTOR_DEFAULT_HIDING_EXPAND_HEIGHT_FACTOR);
		}
		else
		{
			setMovingTime(0f);
			setShrinkingTime(0f);
			setShowingTime(0f);
			setHidingExpandWidthFactor(1f);
			setHidingExpandHeightFactor(1f);
		}
	}
	
	public void startShrinking()
	{
		if(mIsShowing || mIsHiding)
			return;
		
		mIsShrinking = true;
		mIsExpanding = false;
	}
	public void startExpanding()
	{
		if(mIsShowing || mIsHiding)
			return;
		
		mIsShrinking = false;
		mIsExpanding = true;
	}
	
	public void startShowing()
	{
		mIsShowing = true;
		mIsHiding = false;

		mIsShrinking = false;
		mIsExpanding = false;
	}
	public void startHiding()
	{
		mIsShowing = false;
		mIsHiding = true;

		mIsShrinking = false;
		mIsExpanding = false;
	}
	
	public int getAttachedPointer()
	{
		return mAttachedPointer;
	}
	public void clearAttachedPointer()
	{
		mAttachedPointer = InputEvent.INVALID_POINTER;
	}
	
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		//final Vector2 selectedLeftBottom = mSelectedComponent.getLeftBottomPosition();
		//this.setLeftBottomPosition(selectedLeftBottom.x, selectedLeftBottom.y);
	}
	
	@Override
	public boolean fire(InputEvent event)
	{
		switch(event.getType())
		{
		case KeyDown:
			if(!Gdx.input.isKeyPressed(Keys.ENTER) && !Gdx.input.isButtonPressed(Buttons.LEFT))
			{
				if(event.getKeycode() == Keys.DOWN)
				{
					setSelected(mSelectedComponent.toBottom(), mAttachedPointer);
				}
				else if(event.getKeycode() == Keys.UP)
				{
					setSelected(mSelectedComponent.toTop(), mAttachedPointer);
				}
				else if(event.getKeycode() == Keys.LEFT)
				{
					setSelected(mSelectedComponent.toLeft(), mAttachedPointer);
				}
				else if(event.getKeycode() == Keys.RIGHT)
				{
					setSelected(mSelectedComponent.toRight(), mAttachedPointer);
				}
			}
			break;
		case KeyUp:
			break;
		default:
			break;
		}
		
		return false;
	}
	
	public float getMargin()
	{
		return mMargin;
	}
	public void setMargin(float padding)
	{
		mMargin = padding;
		mLeftBottomUpdated = false;
	}

	public float getPadding()
	{
		return mPadding;
	}

	public void setPadding(float padding)
	{
		mPadding = padding;
		mLeftBottomUpdated = false;
	}

	public float getCornerViewportLeftWidth()
	{
		return mCornerViewportLeftWidth;
	}
	public void setCornerViewportLeftWidth(float cornerViewportLeftWidth)
	{
		mCornerViewportLeftWidth = cornerViewportLeftWidth;
	}

	public float getCornerViewportRightWidth()
	{
		return mCornerViewportRightWidth;
	}
	public void setCornerViewportRightWidth(float cornerViewportRightWidth)
	{
		mCornerViewportRightWidth = cornerViewportRightWidth;
	}

	public float getCornerViewportTopHeight()
	{
		return mCornerViewportTopHeight;
	}
	public void setCornerViewportTopHeight(float cornerViewportTopHeight)
	{
		mCornerViewportTopHeight = cornerViewportTopHeight;
	}

	public float getCornerViewportBottomHeight()
	{
		return mCornerViewportBottomHeight;
	}
	public void setCornerViewportBottomHeight(float cornerViewportBottomHeight)
	{
		mCornerViewportBottomHeight = cornerViewportBottomHeight;
	}
	
	public void setCornerWidth(float cornerWidth)
	{
		mCornerViewportLeftWidth = cornerWidth;
		mCornerViewportRightWidth = cornerWidth;
		mLeftBottomUpdated = false;
	}
	public void setCornerHeight(float cornerHeight)
	{
		mCornerViewportTopHeight = cornerHeight;
		mCornerViewportBottomHeight = cornerHeight;
		mLeftBottomUpdated = false;
	}
	public void setCornerSize(float cornerWidth, float cornerHeight)
	{
		mCornerViewportLeftWidth = cornerWidth;
		mCornerViewportRightWidth = cornerWidth;
		mCornerViewportTopHeight = cornerHeight;
		mCornerViewportBottomHeight = cornerHeight;
		mLeftBottomUpdated = false;
	}

	public float getMovingTime()
	{
		return mMovingTime;
	}
	public void setMovingTime(float movingTime)
	{
		mMovingTime = movingTime;
	}

	public float getShrinkingTime()
	{
		return mShrinkingTime;
	}
	public void setShrinkingTime(float shrinkingTime)
	{
		mShrinkingTime = shrinkingTime;
	}

	public float getShowingTime()
	{
		return mShowingTime;
	}
	public void setShowingTime(float showingTime)
	{
		mShowingTime = showingTime;
	}

	public float getHidingExpandWidthFactor()
	{
		return mHidingExpandWidthFactor;
	}
	public void setHidingExpandWidthFactor(float hidingExpandWidthFactor)
	{
		mHidingExpandWidthFactor = hidingExpandWidthFactor;
	}
	
	public float getHidingExpandHeightFactor()
	{
		return mHidingExpandHeightFactor;
	}
	public void setHidingExpandHeightFactor(float hidingExpandHeightFactor)
	{
		mHidingExpandHeightFactor = hidingExpandHeightFactor;
	}

	public void setOnSelectListener(OnSelectListener onSelectListener)
	{
		mOnSelectListener = onSelectListener;
	}
	
	@Override
	protected void calculateLeftBottom()
	{
		if(mLeftBottomUpdated)
			return;
		
		float offsetX, offsetY;
		Vector2 sourceAnchor = getSourceAnchor();
		if(sourceAnchor == null)
		{
			offsetX = getOrigin().x;
			offsetY = getOrigin().y;
		}
		else
		{
			//TODO should take scaling and rotation affection in count ?
			offsetX = sourceAnchor.x * (getSize().x + mShrinkingWidth);
			offsetY = sourceAnchor.y * (getSize().y + mShrinkingHeight);
		}
		
		Vector2 viewportAnchor = getViewportAnchor();
		if(viewportAnchor == null)
		{
			mLeftBottomPosition.x = getPosition().x - offsetX;
			mLeftBottomPosition.y = getPosition().y - offsetY;
		}
		else
		{
			float viewportWidth = getMotherScene().getUICamera().viewportWidth;
			float viewportHeight = getMotherScene().getUICamera().viewportHeight;
			mLeftBottomPosition.x = viewportAnchor.x * viewportWidth - viewportWidth / 2 - offsetX;
			mLeftBottomPosition.y = viewportAnchor.y * viewportHeight - viewportHeight / 2 - offsetY;
		}
		
		mLeftBottomUpdated = true;
		
		//super.calculateLeftBottom();
	}
	
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		//TODO
		//when mSelectedComponent == null, find a Selectable in mothseScene's mComponents,
		//and set this one to mSelectedComponent. If found no Selectable, throw a RuntimeException
		
		boolean reachDestSize = Utils.isEqual(getSize(), mDestSize);
		boolean reachDestPosition = Utils.isEqual(getViewportAnchor(), mDestCenterAnchor);
		if((reachDestSize && reachDestPosition) || mMovingTime == 0f)
		{
			//no animation
			if(mMovingAnimationTime != 0f)
				mMovingAnimationTime = 0f;

			if(!reachDestSize)
			{
				setSize(mDestSize);
			}
			if(!reachDestPosition)
			{
				setViewportAnchor(mDestCenterAnchor);
			}
		}
		else
		{
			mMovingAnimationTime += deltaTime;
			if(mMovingAnimationTime >= mMovingTime)
				mMovingAnimationTime = mMovingTime;

			getSize().lerp(mDestSize, mMovingAnimationTime / mMovingTime);
			getViewportAnchor().lerp(mDestCenterAnchor, mMovingAnimationTime / mMovingTime);
		}
		
		float deltaShrink = mShrinkingTime == 0 ? mPadding : mPadding / mShrinkingTime * deltaTime;
		if(mIsShrinking && !mIsExpanding)
		{
			mShrinkingWidth -= deltaShrink;
			mShrinkingHeight -= deltaShrink;
			if(mShrinkingWidth <= -mPadding)
			{
				mShrinkingWidth = -mPadding;
				//stop animation
				mIsShrinking = false;
				mIsExpanding = false;
			}
		}
		else if(!mIsShrinking && mIsExpanding)
		{
			mShrinkingWidth += deltaShrink;
			mShrinkingHeight += deltaShrink;
			if(mShrinkingWidth >= 0)
			{
				mShrinkingWidth = 0;
				//stop animation
				mIsShrinking = false;
				mIsExpanding = false;
			}
		}
		//correct settings might being wrong
		else if(mIsShrinking && mIsExpanding)
		{
			mIsShrinking = false;
			mIsExpanding = false;
		}

		final Vector2 size = mSelectedComponent.getSize();
		boolean hasShowingAnimation = mShowingTime != 0;
		float destShrinkWidth = hasShowingAnimation ? mHidingExpandWidthFactor * (size.x + mMargin) : 0f;
		float destShrinkHeight = hasShowingAnimation ? mHidingExpandHeightFactor * (size.y + mMargin) : 0f;
		float deltaShrinkWidth = hasShowingAnimation ? destShrinkWidth / mShowingTime * deltaTime : destShrinkWidth;
		float deltaShrinkHeight = hasShowingAnimation ? destShrinkHeight / mShowingTime * deltaTime : destShrinkHeight;
		float deltaTransparency = hasShowingAnimation ? 1f / mShowingTime * deltaTime : 1f;
		if(mIsShowing && !mIsHiding)
		{
			if(!isVisible())
			{
				setVisible(true);
				mShrinkingWidth = destShrinkWidth;
				mShrinkingHeight = destShrinkHeight;
				setViewportAnchor(mSelectedComponent.getCenterAnchor());
			}

			mShrinkingWidth -= deltaShrinkWidth;
			mShrinkingHeight -= deltaShrinkHeight;
			mTransparency += deltaTransparency;
			if(mTransparency >= 1f || mShrinkingWidth <= mMargin || mShrinkingHeight <= mMargin)
			{
				mTransparency = 1f;
				mShrinkingWidth = mMargin;
				mShrinkingHeight = mMargin;
				mIsShowing = false;
				mIsHiding = false;
			}
		}
		else if(!mIsShowing && mIsHiding)
		{
			mShrinkingWidth += deltaShrinkWidth;
			mShrinkingHeight += deltaShrinkHeight;
			mTransparency -= deltaTransparency;
			if(mTransparency <= 0f)
			{
				mTransparency = 0f;
				mIsShowing = false;
				mIsHiding = false;
				setVisible(false);
			}
		}
		else if(mIsShowing && mIsHiding)
		{
			mIsShowing = false;
			mIsHiding = false;
		}
		else
		{
			if(isVisible())
			{
				mTransparency = 1f;
				mIsShowing = false;
				mIsHiding = false;
			}
		}
		
		if(!mIsShowing && !mIsHiding)
		{
			mShrinkingWidth = MathUtils.clamp(mShrinkingWidth, -mPadding, 0f);
			mShrinkingHeight = MathUtils.clamp(mShrinkingHeight, -mPadding, 0f);
		}
		else
		{
			mShrinkingWidth = MathUtils.clamp(mShrinkingWidth, mMargin, destShrinkWidth);
			mShrinkingHeight = MathUtils.clamp(mShrinkingHeight, mMargin, destShrinkHeight);
		}
	}

	@Override
	public void draw(SpriteBatch batch)
	{
		//no allocating a new Color, but reusing the Color.tempColor field
		Color originBatchColor = batch.getColor();
		batch.setColor(originBatchColor.r, originBatchColor.g, originBatchColor.b, mTransparency);
		
		float viewportWidth = getMotherScene().getUICamera().viewportWidth;
		float viewportHeight = getMotherScene().getUICamera().viewportHeight;
		float factorY = viewportHeight / (float)Gdx.graphics.getHeight();
		float factorX = viewportWidth / (float)Gdx.graphics.getWidth();
		float left = mCornerViewportLeftWidth < 0 ?
				mNinePatch.left * factorX :
				mCornerViewportLeftWidth;
		float right = mCornerViewportRightWidth < 0 ?
				mNinePatch.right * factorX :
				mCornerViewportRightWidth;
		float top = mCornerViewportTopHeight < 0 ?
				mNinePatch.top * factorY :
				mCornerViewportTopHeight;
		float bottom = mCornerViewportBottomHeight < 0 ?
				mNinePatch.bottom * factorY :
				mCornerViewportBottomHeight;
		
		Texture texture = getTextureRegion().getTexture();
		Vector2 leftBottom = getLeftBottomPosition();

		float lineHeight = getSize().y + mShrinkingHeight - top - bottom;
		//float lineHeight = getSize().y - top - bottom;
		lineHeight = lineHeight < 0 ? 0 : lineHeight;
		float lineWidth = getSize().x + mShrinkingWidth - left - right;
		//float lineWidth = getSize().x - left - right;
		lineWidth = lineWidth < 0 ? 0 : lineWidth;
		
		//draw nine-patch
		//left bottom
		Rectangle leftBottomRect = mNinePatch.leftBottom;
		batch.draw(texture, leftBottom.x, leftBottom.y,
				0f, 0f,
				left, bottom,
				1f, 1f, 0f, 
				(int)leftBottomRect.x, (int)leftBottomRect.y,
				(int)leftBottomRect.width, (int)leftBottomRect.height,
				false, false);
		//left line
		Rectangle leftLineRect = mNinePatch.leftLine;
		batch.draw(texture, leftBottom.x, leftBottom.y + bottom,
				0f, 0f,
				left, lineHeight,
				1f, 1f, 0f,
				(int)leftLineRect.x, (int)leftLineRect.y,
				(int)leftLineRect.width, (int)leftLineRect.height,
				false, false);
		//left top
		Rectangle leftTopRect = mNinePatch.leftTop;
		batch.draw(texture, leftBottom.x, 
				leftBottom.y + bottom + lineHeight,
				0f, 0f,
				left, top,
				1f, 1f, 0f, 
				(int)leftTopRect.x, (int)leftTopRect.y,
				(int)leftTopRect.width, (int)leftTopRect.height,
				false, false);
		//top line
		Rectangle topLineRect = mNinePatch.topLine;
		batch.draw(texture, leftBottom.x + left,
				leftBottom.y + bottom + lineHeight,
				0f, 0f,
				lineWidth, top,
				1f, 1f, 0f,
				(int)topLineRect.x, (int)topLineRect.y,
				(int)topLineRect.width, (int)topLineRect.height,
				false, false);
		//right top
		Rectangle rightTopRect = mNinePatch.rightTop;
		batch.draw(texture, leftBottom.x + left + lineWidth, 
				leftBottom.y + bottom + lineHeight,
				0f, 0f,
				right, top,
				1f, 1f, 0f, 
				(int)rightTopRect.x, (int)rightTopRect.y,
				(int)rightTopRect.width, (int)rightTopRect.height,
				false, false);
		//right line
		Rectangle rightLineRect = mNinePatch.rightLine;
		batch.draw(texture, leftBottom.x + left + lineWidth, 
				leftBottom.y + bottom,
				0f, 0f,
				right, lineHeight,
				1f, 1f, 0f,
				(int)rightLineRect.x, (int)rightLineRect.y,
				(int)rightLineRect.width, (int)rightLineRect.height,
				false, false);
		//right bottom
		Rectangle rightBottomRect = mNinePatch.rightBottom;
		batch.draw(texture, leftBottom.x + left + lineWidth, 
				leftBottom.y,
				0f, 0f,
				right, bottom,
				1f, 1f, 0f, 
				(int)rightBottomRect.x, (int)rightBottomRect.y,
				(int)rightBottomRect.width, (int)rightBottomRect.height,
				false, false);
		//bottom line
		Rectangle bottomLineRect = mNinePatch.bottomLine;
		batch.draw(texture, leftBottom.x + left,
				leftBottom.y,
				0f, 0f,
				lineWidth, bottom,
				1f, 1f, 0f,
				(int)bottomLineRect.x, (int)bottomLineRect.y,
				(int)bottomLineRect.width, (int)bottomLineRect.height,
				false, false);
		//center
		Rectangle centerRect = mNinePatch.centerBox;
		batch.draw(texture, leftBottom.x + left,
				leftBottom.y + bottom,
				0f, 0f,
				lineWidth, lineHeight,
				1f, 1f, 0f,
				(int)centerRect.x, (int)centerRect.y,
				(int)centerRect.width, (int)centerRect.height,
				false, false);
		
		batch.setColor(originBatchColor);
	}
	
	public static abstract class OnSelectListener
	{
		public abstract void onSelect();
	}
}
