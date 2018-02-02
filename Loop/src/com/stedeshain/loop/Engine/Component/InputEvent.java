package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.stedeshain.loop.Engine.Scene.Scene;

public class InputEvent implements Poolable
{
	public static final int INVALID_POINTER = -1;
	public static final int INVALID_BUTTON = -1;
	public static final int INVALID_KEYCODE = Keys.F12;
	public static final char INVALID_TYPEDCHAR = ' ';
	
	private Scene mScene;
	private InputType mType;
	private float mViewportX;
	private float mViewportY;
	private int mPointer;
	private int mButton;
	
	private int mKeycode;
	private char mTypedChar;
	
	private int mScrollAmount;
	
	public InputEvent() {}
	
	public enum InputType
	{
		TouchDown,
		TouchDragged,
		TouchUp,
		KeyDown,
		KeyUp,
		KeyTyped,
		MouseMoved,
		Scrolled,
		//...
		NoInput,
	}

	public Scene getScene()
	{
		return mScene;
	}

	public void setScene(Scene scene)
	{
		this.mScene = scene;
	}

	public InputType getType()
	{
		return mType;
	}

	public void setType(InputType type)
	{
		this.mType = type;
	}

	public float getViewportX()
	{
		return mViewportX;
	}

	public void setViewportX(float viewportX)
	{
		this.mViewportX = viewportX;
	}

	public float getViewportY()
	{
		return mViewportY;
	}

	public void setViewportY(float viewportY)
	{
		this.mViewportY = viewportY;
	}
	
	public int getPointer()
	{
		return mPointer;
	}

	public void setPointer(int pointer)
	{
		this.mPointer = pointer;
	}

	public int getButton()
	{
		return mButton;
	}

	public void setButton(int button)
	{
		this.mButton = button;
	}

	public int getKeycode()
	{
		return mKeycode;
	}

	public void setKeycode(int keycode)
	{
		mKeycode = keycode;
	}

	public char getTypedChar()
	{
		return mTypedChar;
	}

	public void setTypedChar(char typedChar)
	{
		mTypedChar = typedChar;
	}

	public int getScrollAmount()
	{
		return mScrollAmount;
	}

	public void setScrollAmount(int scrollAmount)
	{
		mScrollAmount = scrollAmount;
	}

	public void initTouch(Scene scene, InputType type, float viewportX, float viewportY, 
			int pointer, int button)
	{
		mScene = scene;
		mType = type;
		mViewportX = viewportX;
		mViewportY = viewportY;
		mPointer = pointer;
		mButton = button;
	}
	
	public void initKey(Scene scene, InputType type, int keycode, char typedChar)
	{
		mScene = scene;
		mType = type;
		setKeycode(keycode);
		setTypedChar(typedChar);
	}
	
	public void initMouse(Scene scene, InputType type, float viewportX, float viewportY, int scrollAmount)
	{
		mScene = scene;
		mType = type;
		mViewportX = viewportX;
		mViewportY = viewportY;
		mPointer = 0;	//remember mouse pointer !!!
		setScrollAmount(scrollAmount);
	}

	@Override
	public void reset()
	{
		mScene = null;
		mType = InputType.NoInput;
		mViewportX = 0f;
		mViewportY = 0f;
		mPointer = INVALID_POINTER;
		mButton = INVALID_BUTTON;
		setKeycode(Keys.F12);
		setTypedChar(' ');
		setScrollAmount(0);
	}
}
