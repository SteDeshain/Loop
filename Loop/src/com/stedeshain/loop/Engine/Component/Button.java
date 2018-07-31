package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.stedeshain.loop.Engine.Utils.Utils;
import com.sun.istack.internal.NotNull;

/**
 * Like a Label, if the Button has a text, its some properties like
 * rotation, scaling and origin make no sense.
 * @author SteDeshain
 */
public class Button extends UIComponent implements Selectable
{
	private ButtonGroup mMotherButtonGroup = null;
	
	protected Label mLabel = null;
	
	private boolean mIsPressed = false;
	private boolean mLastIsPressed = false;
	private TextureRegion mPressedRegion = null;
	private Color mPressedColor = null;
	
	private TextureRegion mStoredRegion = null;
	private Color mStoredFontColor = null;
	
	private int mPressedPointer = InputEvent.INVALID_POINTER;
	private OnClickListener mOnClickListener = null;
	
	private Selectable mLeftComponent = null;
	private Selectable mTopComponent = null;
	private Selectable mRightComponent = null;
	private Selectable mBottomComponent = null;
	private boolean mSelected = false;
	/**
	 * whether should hide the selector (if has one) after pressed the button
	 */
	private boolean mHidingSelector = false;
	
	/**
	 * Create a Button only contains a text "Button" with GDX default font
	 */
	public Button()
	{
		this("Button", Utils.getDefaultFont());
	}
	/**
	 * Create a Button only has a picture
	 * @param background
	 */
	public Button(@NotNull TextureRegion background)
	{
		super(background);
		mLabel = null;
	}
	/**
	 * Create a Button only has a text
	 * @param text
	 * @param font
	 */
	public Button(@NotNull String text, @NotNull BitmapFont font)
	{
		super(null);
		mLabel = new Label(font, text);
	}
	/**
	 * Create a Button who has a text with a picture background
	 * @param background
	 * @param text
	 * @param font
	 */
	public Button(@NotNull TextureRegion background, @NotNull String text, @NotNull BitmapFont font)
	{
		super(background);
		mLabel = new Label(font, text);
	}
	
	void setButtonGroup(ButtonGroup group)
	{
		mMotherButtonGroup = group;
	}
	public ButtonGroup getButtonGroup()
	{
		return mMotherButtonGroup;
	}

	/**
	 * MUST NOT call it when a Button is pressed down !!!
	 * @param pressedRegion
	 */
	public void setPressedTextureRegion(TextureRegion pressedRegion)
	{
		if(getTextureRegion() != null)
			mPressedRegion = pressedRegion;
	}

	public void setPressedColor(Color pressedColor)
	{
		if(mLabel != null)
			mPressedColor = pressedColor;
	}
	public Color getPressedColor()
	{
		return mPressedColor;
	}
	
	public boolean isPressed()
	{
		return mIsPressed;
	}
	public void setPressed(boolean isPressed)
	{
		mIsPressed = isPressed;
		Selector selector = Selector.getSelector(getMotherScene());
		if(selector != null)
		{
			if(mLastIsPressed == false && mIsPressed == true)
			{
				selector.startShrinking();
			}
			else if(mLastIsPressed == true && mIsPressed == false)
			{
				selector.startExpanding();
			}
		}
	}
	
	//TODO necessary?
	public Label getLabel()
	{
		return mLabel;
	}
	
	@Override
	public void disable()
	{
		super.disable();
		abortPressed();
	}
	
	@Override
	public void setPosition(Vector2 position)
	{
		if(mLabel != null)
			mLabel.setPosition(position);

		super.setPosition(position);
	}
	
	@Override
	public void setSize(Vector2 size)
	{
		if(mLabel != null)
			mLabel.setSize(size);
		
		super.setSize(size);
	}
	@Override
	public Vector2 getSize()
	{
		if(getTextureRegion() == null)	//only a label, so the size is this label's size
		{
			return mLabel.getSize();
		}
		else	//contains a picture, so the final size is this picture's size, no matter how big its label is
		{
			return super.getSize();
		}
	}
	
	@Override
	public Vector2 getLeftBottomPosition()
	{
		if(getTextureRegion() == null)
		{
			return mLabel.getLeftBottomPosition();
		}
		else
		{
			return super.getLeftBottomPosition();
		}
	}
	
	@Override
	public void setOrigin(Vector2 origin)
	{
		if(mLabel != null)
			mLabel.setOrigin(origin);
		
		super.setOrigin(origin);
	}
	
	@Override
	public void setViewportAnchor(Vector2 viewportAnchor)
	{
		if(mLabel != null)
			mLabel.setViewportAnchor(viewportAnchor);
		super.setViewportAnchor(viewportAnchor);
	}
	
	@Override
	public void setSourceAnchor(Vector2 sourceAnchor)
	{
		if(mLabel != null)
			mLabel.setSourceAnchor(sourceAnchor);
		super.setSourceAnchor(sourceAnchor);
	}
	
	@Override
	public void setMotherScene(Scene motherScene)
	{
		if(mLabel != null)
			mLabel.setMotherScene(motherScene);
		super.setMotherScene(motherScene);
	}
	@Override
	public void departFromScene()
	{
		if(mLabel != null)
			mLabel.departFromScene();
		super.departFromScene();
	}
	
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		if(mLabel != null)
			mLabel.resize(width, height);
	}
	
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		if(mPressedRegion != null && mIsPressed && getTextureRegion() != mPressedRegion)
		{
			mStoredRegion = getTextureRegion();
			setTextureRegion(mPressedRegion);
		}
		else if(mPressedRegion != null && !mIsPressed && getTextureRegion() == mPressedRegion)
		{
			setTextureRegion(mStoredRegion);
		}

		if(mPressedPointer != InputEvent.INVALID_POINTER && mIsPressed == true)
		{
			Vector2 viewportPosition = getMotherScene().toUIViewportCoordination(
					Gdx.input.getX(mPressedPointer),
					Gdx.input.getY(mPressedPointer));
			if(!this.hit(viewportPosition))
			{
				abortPressed();
			}	
		}
	}
	
	public void abortPressed()
	{
		mIsPressed = false;
		mPressedPointer = InputEvent.INVALID_POINTER;
		if(mLabel != null && mStoredFontColor != null)
		{
			mLabel.setColor(mStoredFontColor);
		}
		if(mPressedRegion != null && mStoredRegion != null)
		{
			setTextureRegion(mStoredRegion);
		}

		Selector selector = Selector.getSelector(getMotherScene());
		if(selector != null)
		{
			selector.startExpanding();
		}
	}
	
	@Override
	public void draw(SpriteBatch batch)
	{
		if(mLabel != null && mLastIsPressed == false && mIsPressed == true)
		{
			if(mStoredFontColor == null)
				mStoredFontColor = new Color();
			mStoredFontColor.set(mLabel.getColor());
			mLabel.setColor(mPressedColor);
		}
		
		if(getTextureRegion() == null)	//only draw label
		{
			mLabel.draw(batch);
		}
		else if(mLabel == null)	//only draw background picture
		{
			super.draw(batch);
		}
		else
		{
			super.draw(batch);

			Vector2 leftBottom = this.getLeftBottomPosition();
			float left = leftBottom.x + (this.getSize().x - mLabel.getSize().x) / 2f;
			float top = leftBottom.y + (this.getSize().y - mLabel.getSize().y) / 2f + mLabel.getSize().y;
			mLabel.draw(batch, left, top);
		}
		
		if(mLabel != null && mLastIsPressed == true && mIsPressed == false)
		{
			mLabel.setColor(mStoredFontColor);
		}
	}
	
	@Override
	public void updateLastFrameValue()
	{
		super.updateLastFrameValue();
		mLastIsPressed = mIsPressed;
	}
	
	//Dealing with selector and touch coorporate
	@Override
	public boolean fire(InputEvent event)
	{
		if(!isEnable())
			return false;
		
		/**/
		Selector selector = Selector.getSelector(getMotherScene());
		switch(event.getType())
		{
		case TouchDown:
			if(event.getButton() != Buttons.LEFT)
				break;
			if(!isSelected())
			{
				if(attemptSelect(event.getPointer()))
				{
					this.setPressed(true);
					mPressedPointer = event.getPointer();
					if(selector != null && !selector.isVisible())
					{
						selector.startShowing();
					}
				}
			}
			else
			{
				this.setPressed(true);
				mPressedPointer = event.getPointer();
			}
			break;
		case TouchUp:
			if(event.getButton() != Buttons.LEFT)
				break;
			this.setPressed(false);
			if(mPressedPointer == event.getPointer() && mLastIsPressed == true && mIsPressed == false)
			{
				onClick();
				if(mHidingSelector && selector != null)
				{
					selector.startHiding();
				}
			}
			mPressedPointer = InputEvent.INVALID_POINTER;
			
			if(selector != null)
			{
				if(selector.getAttachedPointer() == event.getPointer())
				{
					selector.clearAttachedPointer();
				}
			}
			break;
		case KeyDown:
			if(Selector.getSelector(getMotherScene()) == null)
				break;
			else
			{
				if(isSelected() && event.getKeycode() == Keys.ENTER)
				{
					this.setPressed(true);
					mPressedPointer = InputEvent.INVALID_POINTER;
				}
			}
			break;
		case KeyUp:
			if(Selector.getSelector(getMotherScene()) == null)
				break;
			else
			{
				if(isSelected() && event.getKeycode() == Keys.ENTER)
				{
					this.setPressed(false);
					onClick();
					if(mHidingSelector && selector != null)
					{
						selector.startHiding();
					}
				}
			}
			break;
		case TouchDragged:
			if(!isSelected())
			{
				attemptSelect(event.getPointer());
			}
			break;
		case MouseMoved:
			if(!isSelected())
			{
				attemptSelect(event.getPointer());
			}
			break;
		default:
			break;
		}
		/**/
		
		return false;
	}
	
	protected void onClick()
	{
		if(mOnClickListener != null)
			mOnClickListener.onClick();
	}
	
	@Override
	public void clearInputEvent()
	{
		abortPressed();
		mLastIsPressed = false;
	}
	
	public void setOnClickListener(OnClickListener listener)
	{
		mOnClickListener = listener;
	}
	
	/**
	 * @param pointer
	 * @return whether the selection succeeds
	 */
	public boolean attemptSelect(int pointer)
	{
		Selector sceneSelector = Selector.getSelector(getMotherScene());
		if(sceneSelector == null)
		{
			return true;
		}
		else
		{
			return sceneSelector.setSelected(this, pointer);
		}
	}

	public void setNextComponents(Selectable left, Selectable top, Selectable right, Selectable bottom)
	{
		mLeftComponent = left;
		mTopComponent = top;
		mRightComponent = right;
		mBottomComponent = bottom;
	}
	public boolean isHidingSelector()
	{
		return mHidingSelector;
	}
	public void setHidingSelector(boolean hidingSelector)
	{
		this.mHidingSelector = hidingSelector;
	}
	//Selectable interface method
	@Override
	public Vector2 getCenterAnchor()
	{
		Vector2 leftBottom = getLeftBottomPosition();
		Vector2 size = getSize();
		float centerX = leftBottom.x + size.x / 2;
		float centerY = leftBottom.y + size.y / 2;
		
		Vector2 anchor = new Vector2();
		float viewportWidth = getMotherScene().getUICamera().viewportWidth;
		float viewportHeight = getMotherScene().getUICamera().viewportHeight;
		anchor.x = (centerX + viewportWidth / 2) / viewportWidth;
		anchor.y = (centerY + viewportHeight / 2) / viewportHeight;
		
		return anchor;
	}
	@Override
	public Selectable toLeft()
	{
		return mLeftComponent;
	}
	@Override
	public Selectable toTop()
	{
		return mTopComponent;
	}
	@Override
	public Selectable toRight()
	{
		return mRightComponent;
	}
	@Override
	public Selectable toBottom()
	{
		return mBottomComponent;
	}
	@Override
	public boolean isSelected()
	{
		return mSelected;
	}
	@Override
	public void select()
	{
		mSelected = true;
	}
	@Override
	public void deselect()
	{
		mSelected = false;
		abortPressed();
	}

	@Override
	public void dispose()
	{
		if(isOwnAssets())
		{
			if(mPressedRegion != null)
				mPressedRegion.getTexture().dispose();
		}
		
		if(mLabel != null)
			mLabel.dispose();
		
		super.dispose();
	}
	
	public interface OnClickListener
	{
		public void onClick();
	}

}
