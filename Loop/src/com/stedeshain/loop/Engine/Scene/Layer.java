package com.stedeshain.loop.Engine.Scene;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.stedeshain.loop.Engine.Component.DrawableComponent;
import com.stedeshain.loop.Engine.Component.InputEvent;
import com.stedeshain.loop.Engine.Component.UIComponent;
import com.stedeshain.loop.Engine.Component.Body.AbstractBody;
import com.stedeshain.loop.Engine.Utils.Constants;
import com.stedeshain.loop.Engine.Utils.Deepable;
import com.stedeshain.loop.Engine.Utils.IntVector2;
import com.stedeshain.loop.Engine.Utils.Utils;
import com.sun.istack.internal.NotNull;

public class Layer implements Deepable, Disposable
{
	private Scene mMotherScene;
	private Array<DrawableComponent> mDrawableComponents;
	/**
	 * to accelerate some calculation in rendering
	 */
	private Array<UIComponent> mUIComponents;
	
	private Array<Layer> mChildrenLayers;
	
	private final boolean mIsUILayer;
	private long mDepth = 0;
	private String mName = null;
	private boolean mIsVisible = true;
	/**
	 * its origin is at the left-bottom corner
	 */
	private Vector2 mPosition = new Vector2();
	private Vector2 mSize = new Vector2();
	/**
	 * a Layer can be drawn on another Layer, but it's only recommended on UI Layer
	 */
	private final Layer mMotherLayer;
	
	private FrameBuffer mFrameBuffer;
	
	/**
	 * new a none-UI Layer, without a mother Layer
	 * @param motherScene
	 */
	Layer(@NotNull Scene motherScene)
	{
		this(motherScene, false);
	}
	/**
	 * new a Layer without a mother Layer
	 * @param motherScene
	 * @param isUILayer
	 */
	Layer(@NotNull Scene motherScene, boolean isUILayer)
	{
		this(motherScene, null, isUILayer);
	}
	/**
	 * new a UI Layer with the given mother Layer
	 * @param motherScene
	 * @param motherLayer
	 */
	Layer(@NotNull Scene motherScene, @NotNull Layer motherLayer)
	{
		this(motherScene, motherLayer, true);
	}
	private Layer(@NotNull Scene motherScene, Layer motherLayer, boolean isUILayer)
	{
		mMotherScene = motherScene;
		mMotherLayer = motherLayer;
		mIsUILayer = isUILayer;
		mDrawableComponents = new Array<DrawableComponent>();
		mUIComponents = new Array<UIComponent>();
		mChildrenLayers = new Array<Layer>();
		mFrameBuffer = new FrameBuffer(Constants.GAME_PIXEL_FORMAT, 
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
		if(mMotherLayer == null)	//add into Scene's mLayers
		{
			mMotherScene.getLayers().add(this);
		}
		else	//add into mother Layer's mChildrenLayers
		{
			mMotherLayer.mChildrenLayers.add(this);
		}
	}
	
	//package access
	//User cannot add component to a Layer directly,
	//but can only add to a Scene with a specified Layer index or name
	void addComponent(@NotNull DrawableComponent component)
	{
		if(mDrawableComponents.contains(component, true))
			return;
		
		mDrawableComponents.add(component);
		component.setMotherLayer(this);
		
		if(component instanceof UIComponent)
		{
			mUIComponents.add((UIComponent)component);
		}
	}
	
	//package access
	void sortDrawables(Comparator<Deepable> comparator)
	{
		mDrawableComponents.sort(comparator);
	}
	void sortLayers(Comparator<Deepable> comparator)
	{
		mChildrenLayers.sort(comparator);
		for(int i = mChildrenLayers.size - 1; i >= 0; i--)
		{
			//recursed
			mChildrenLayers.get(i).sortLayers(comparator);
		}
	}

	//package access
	/**
	 * remove component from this Layer and all its childrenLayers
	 * @param component to remove
	 * @return true if find the component and removed it successfully, false if can't find such component
	 */
	boolean removeComponent(@NotNull DrawableComponent component)
	{
		int index = -1;
		index = mDrawableComponents.indexOf(component, true);
		if(index >= 0)
		{
			mDrawableComponents.removeValue(component, true);
			if(component instanceof AbstractBody)
			{
				((AbstractBody)component).disposePhysics();
			}
			component.setMotherLayer(null);
			if(component instanceof UIComponent)
			{
				mUIComponents.removeValue((UIComponent)component, true);
			}
			return true;
		}
		else
		{
			for(int i = mChildrenLayers.size - 1; i >= 0; i--)
			{
				//recursed
				if(mChildrenLayers.get(i).removeComponent(component))
					return true;
			}
			return false;
		}
	}
	
	boolean containComponent(@NotNull DrawableComponent component)
	{
		return mDrawableComponents.contains(component, true);
	}
	
	Layer getChildeLayer(@NotNull String name)
	{
		for(int i = mChildrenLayers.size - 1; i >= 0; i--)
		{
			Layer curLayer = mChildrenLayers.get(i);
			if(curLayer.matchName(name))
				return curLayer;
		}
		return null;
	}

	//package access
	Texture draw(SpriteBatch batch)
	{	
		mFrameBuffer.begin();
		
		for(int i = mChildrenLayers.size - 1; i >= 0; i--)
		{
			Layer curLayer = mChildrenLayers.get(i);
			if(curLayer.isVisible())
			{
				Texture curTexture = curLayer.draw(batch);
				Vector2 curSize = curLayer.getSize();
				if(Utils.isVectorLyingOnAxis(curSize))	//invalid size
				{
					curSize.set(mMotherScene.toUIViewportCoordination(
							Gdx.graphics.getWidth(),
							Gdx.graphics.getHeight()));
				}
				IntVector2 curScreenSize = new IntVector2(
						mMotherScene.toScreenCoordinationFromUIViewport(curSize.x, curSize.y));
				Vector2 curPosition = curLayer.getPosition();
				batch.draw(curTexture, curPosition.x, curPosition.y, 0f, 0f,
						curSize.x, curSize.y, 1f, 1f, 0f, 0, 0,
						curScreenSize.x, curScreenSize.y, false, false);
			}
		}
		for(int i = mDrawableComponents.size - 1; i >= 0; i--)
		{
			if(mDrawableComponents.get(i).isVisible())
				mDrawableComponents.get(i).draw(batch);
		}
		
		mFrameBuffer.end();
		return mFrameBuffer.getColorBufferTexture();
	}
	
	void resize(int width, int height)
	{
		if(!mIsUILayer)
			return;
		
		for(int i = mUIComponents.size - 1; i >= 0; i--)
		{
			mUIComponents.get(i).resize(width, height);
		}
	}
	
	boolean fireInputEvent(InputEvent event)
	{
		if(!mIsUILayer)
			return false;
		
		boolean handled = false;
		for(int i = mUIComponents.size - 1; i >= 0; i--)
		{
			handled |= mUIComponents.get(i).fire(event);
			if(handled)
				break;
		}
		return handled;
	}
	
	UIComponent hit(float viewportX, float viewportY)
	{
		if(!mIsUILayer)
			return null;
		
		for(int i = mUIComponents.size - 1; i >= 0; i--)
		{
			UIComponent curUI = mUIComponents.get(i);
			if(curUI.hit(viewportX, viewportY))
				return curUI;
		}
		return null;
	}
	
	void clearInputEvent()
	{
		if(!mIsUILayer)
			return;
		
		for(int i = mUIComponents.size - 1; i >= 0; i--)
		{
			mUIComponents.get(i).clearInputEvent();
		}
	}

	public Scene getMotherScene()
	{
		return mMotherScene;
	}
	
	public Layer getMotherLayer()
	{
		return mMotherLayer;
	}
	
	public void setName(String name)
	{
		mName = name;
	}
	public String getName()
	{
		return mName == null ? "no-name" : mName;
	}
	public boolean matchName(@NotNull String name)
	{
		if(mName == null)
			return false;
		
		return mName.equals(name);
	}
	
	public void setDepth(long depth)
	{
		mDepth = depth;
	}
	public long getDepth()
	{
		return mDepth;
	}
	
	public boolean isUILayer()
	{
		return mIsUILayer;
	}
	
	public boolean isVisible()
	{
		return mIsVisible;
	}
	public void setVisible(boolean visible)
	{
		mIsVisible = visible;
	}
	
	public Vector2 getPosition()
	{
		return mPosition;
	}
	public void setPosition(Vector2 position)
	{
		mPosition.set(position);
	}
	public void setPosition(float x, float y)
	{
		mPosition.set(x, y);
	}
	public void move(float dx, float dy)
	{
		mPosition.add(dx, dy);
	}
	public void moveHorizontal(float dx)
	{
		mPosition.x += dx;
	}
	public void moveVertical(float dy)
	{
		mPosition.y += dy;
	}
	
	public Vector2 getSize()
	{
		return mSize;
	}
	public void setSize(Vector2 size)
	{
		mSize.set(size);
	}
	public void setSize(float w, float h)
	{
		mSize.set(w, h);
	}
	
	@Override
	public void dispose()
	{
		for(int i = mDrawableComponents.size - 1; i >= 0; i--)
		{
			mDrawableComponents.get(i).dispose();
		}
		mFrameBuffer.dispose();
	}
}
