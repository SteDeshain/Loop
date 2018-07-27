package com.stedeshain.loop.Engine.Scene;

import java.util.Comparator;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pools;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Component.DrawableComponent;
import com.stedeshain.loop.Engine.Component.InputEvent;
import com.stedeshain.loop.Engine.Component.InputEvent.InputType;
import com.stedeshain.loop.Engine.Component.SceneComponent;
import com.stedeshain.loop.Engine.Component.Selector;
import com.stedeshain.loop.Engine.Component.UIComponent;
import com.stedeshain.loop.Engine.Utils.AssetsHelper;
import com.stedeshain.loop.Engine.Utils.Constants;
import com.stedeshain.loop.Engine.Utils.Deepable;
import com.stedeshain.loop.Engine.Utils.IntVector2;
import com.stedeshain.loop.Engine.Utils.Utils;
import com.sun.istack.internal.NotNull;

public class Scene extends InputMultiplexer implements Disposable
{
	public enum ViewportFixedType
	{
		FixedWidth,
		FixedHeight,
		FixedArea,
	}
	
	//physics
	private World mWorld = null;
	private Array<ContactListener> mContactListeners = new Array<ContactListener>();
	private Box2DDebugRenderer mPhysicsDebugRenderer = null;
	private ShapeRenderer mShapeRenderer = null;	//For now, it's used to draw body's mess center
	
	//fix frame
	private float mPhysicsAccumulator = 0;

	/**
	 * duration time that all Scenes will take to hide or show themselves
	 */
	private float mFadeInDuration = Constants.SCENE_DEFAULT_TRANSITION_DURATION;
	private float mFadeOutDuration = Constants.SCENE_DEFAULT_TRANSITION_DURATION;
	
	private AssetsHelper mSceneAssets;
	private boolean mHasAssets = false;
	private boolean mGetAssetsLoaded = false;
	
	private Game mMotherGame;
	private boolean mPaused;
	private Array<SceneComponent> mComponents;
	private Array<Layer> mLayers;
	private Layer mDefaultUILayer;
//	//FIXME remove mDrawableComponents
//	private Array<DrawableComponent> mDrawableComponents;
//	//FIXME remove mUIComponents, use a Layer instead
//	private Array<UIComponent> mUIComponents;
	/**
	 * A reverse order comparator, cause draw() use reverse order to iterate child component.
	 * Two negatives make a positive.
	 */
	private Comparator<Deepable> mDeepableComparator;
//	private Comparator<DrawableComponent> mDrawableComparator;
//	private Comparator<Layer> mLayerComparator;
	
	private Array<SceneComponent> mComponentsToRemoving;
	
	private ViewportFixedType mViewportType;
	private OrthographicCamera mCamera;
	private OrthographicCamera mUICamera;
	private SpriteBatch mBatch;
	private Color mBackColor = new Color(Constants.SCREEN_FLUSH_COLOR);
	
	private Vector2 mCameraHorizontalRestriction;
	private Vector2 mCameraVerticalRestriction;
	
	/**
	 * Used to fix the drag bug on some platform.
	 */
	private IntMap<TouchPositionInfo> lastDragPositions;
	
	/**
	 * Whether this scene is one-time used.
	 * if it's true, this scene will be disposed after another scene shows up.
	 */
	private boolean mOneTimeUsed = false;
	
	private OnUpdateListener mUpdateListener;
	private OnCreateListener mCreateListener;

	public Scene(@NotNull Game motherGame, Vector2 viewport)
	{
		setPaused(false);
		mMotherGame = motherGame;
		mComponents = new Array<SceneComponent>();
		mLayers = new Array<Layer>();
		mDefaultUILayer = new Layer(this, true);
		mLayers.add(mDefaultUILayer);
//		mDrawableComponents = new Array<DrawableComponent>();
//		mUIComponents = new Array<UIComponent>();
		mDeepableComparator = new Comparator<Deepable>()
				{
					@Override
					public int compare(Deepable a, Deepable b)
					{
						if(a.getDepth() > b.getDepth())
							return 1;
						else if(a.getDepth() == b.getDepth())
							return 0;
						else
							return -1;
					}
				};
		mComponentsToRemoving = new Array<SceneComponent>();

		mViewportType = ViewportFixedType.FixedHeight;
		mBatch = new SpriteBatch();
		mCamera = new OrthographicCamera();
		if(viewport != null)
		{
			mCamera.viewportWidth = viewport.x;
			mCamera.viewportHeight = viewport.y;
		}
		mCamera.position.set(0, 0, 0);
		mCamera.update();

		mUICamera = new OrthographicCamera();
		if(mMotherGame.getUIViewport() == null)
		{
			mUICamera.viewportWidth = Gdx.graphics.getWidth();
			mUICamera.viewportHeight = Gdx.graphics.getHeight();
		}
		else
		{
			mUICamera.viewportWidth = mMotherGame.getUIViewport().x;
			mUICamera.viewportHeight = mMotherGame.getUIViewport().y;
		}
		mUICamera.position.set(0, 0, 0);
		mUICamera.update();
		
		lastDragPositions = new IntMap<TouchPositionInfo>();
	}
	
	public void newLayer(String name, long depth, boolean isUILayer)
	{
		Layer layer = new Layer(this, isUILayer);
		layer.setName(name);
		layer.setDepth(depth);
		mLayers.add(layer);
		sortLayers();
	}
	public Layer getLayer(@NotNull String name)
	{
		for(Layer layer: mLayers)
		{
			if(layer.matchName(name))
				return layer;
		}
		return null;
	}
	public Layer getLayer(int index)
	{
		if(index < 0 || index >= mLayers.size)
			return null;
		
		return mLayers.get(index);
	}
	public void sortLayers()
	{
		mLayers.sort(mDeepableComparator);
	}
	
	public void addComponent(@NotNull SceneComponent component)
	{
		if(mComponents.contains(component, true))
			return;
		
		mComponents.add(component);
		component.setMotherScene(this);
		component.create();
	}
	/**
	 * if no specified layer, the component will be added into the default UI Layer
	 * @param component
	 */
	public void addComponent(@NotNull DrawableComponent component)
	{
		if(mComponents.contains(component, true))
			return;
		
		mComponents.add(component);
		mDefaultUILayer.addComponent(component);
		component.setMotherScene(this);
		component.create();
	}
	public void addComponent(@NotNull DrawableComponent component, @NotNull String layerName)
	{
		if(mComponents.contains(component, true))
			return;
		
		Layer targetLayer = getLayer(layerName);
		if(targetLayer == null)
			return;

		mComponents.add(component);
		targetLayer.addComponent(component);
		component.setMotherScene(this);
		component.create();
	}


	/**
	 * add the component at the specific index of mComponents
	 */
	//FIXME remove it !!! Yes ! need to remove it !!!
	/**
	public void insertComponent(@NotNull SceneComponent component, int index)
	{
		if(mComponents.contains(component, true))
			return;
		if(index > mComponents.size)
		{
			Utils.debug("Attempt to insert into an Array at the index out of bound.");
			return;
		}
		
		mComponents.insert(index, component);
		//Separate UIComponent and DrawableComponent
		if(component instanceof UIComponent)
			mUIComponents.insert(index, (UIComponent)component);	//wrong index
		else if(component instanceof DrawableComponent)
			mDrawableComponents.insert(index, (DrawableComponent)component);	//wrong index
		component.setMotherScene(this);
		component.create();
	}
	/**/
	
	/**
	 * Can not be called in main loop, like update() or draw()
	 * @param component
	 */
	private void removeComponent(@NotNull SceneComponent component)
	{
		if(mBatch.isDrawing())
		{
			Utils.debug("Try removing component while rendering is carrying on.");
			return;
		}
		
		int index = -1;
		
		index = mComponents.indexOf(component, true);
		if(index >= 0)
		{
			mComponents.removeIndex(index);
			
			if(component instanceof DrawableComponent)
			{
				DrawableComponent drawableComp = (DrawableComponent)component;
				for(Layer layer: mLayers)
				{
					if(layer.removeComponent(drawableComp))
						break;
				}
			}

			component.setMotherScene(null);
		}
	}
	
	public boolean containComponent(SceneComponent component)
	{
		return mComponents.contains(component, true);
	}

	/**
	 * find component by name
	 * @param name
	 * @return first component matching the given name
	 */
	public SceneComponent getComponent(@NotNull String name)
	{
		for(int i = 0; i < mComponents.size; i++)
		{
			SceneComponent currentComp = mComponents.get(i);
			if(currentComp.matchName(name))
				return currentComp;
		}
		return null;
	}
	private Array<SceneComponent> tempCompArray = new Array<SceneComponent>();
	/**
	 * find components by tag
	 * @param tag
	 * @return This method will return the same instance of an Array every time
	 */
	public Array<SceneComponent> getComponents(@NotNull String tag)
	{
		tempCompArray.clear();
		for(int i = 0; i < mComponents.size; i++)
		{
			SceneComponent currentComp = mComponents.get(i);
			if(currentComp.matchTag(tag))
				tempCompArray.add(currentComp);
		}
		return tempCompArray;
	}

	public void appendRemovingComponent(SceneComponent component)
	{
		mComponentsToRemoving.add(component);
	}
	
	public void addContactListener(@NotNull ContactListener listener)
	{
		mContactListeners.add(listener);
	}

	//FIXME sort every layer
	/**
	 * Can not be called in update() or draw()
	 */
	public void sortDrawables()
	{
		//TODO may sort in another thread. After sorting finished, need to post to render thread.
		//Cause libGDX has a good implementation of postRunnable(), saying this:
		//"This will run the code in the Runnable in the rendering thread in the next frame, 
		//before ApplicationListener.render() is called."
		sortLayers();
		for(Layer layer: mLayers)
		{
			layer.sortDrawables(mDeepableComparator);
		}
	}
	
	public void addPhysicsModule()
	{
		addPhysicsModule(new Vector2(0f, -10f));
	}
	public void addPhysicsModule(float gravityX, float gravityY)
	{
		addPhysicsModule(new Vector2(gravityX, gravityY));
	}
	public void addPhysicsModule(Vector2 gravity)
	{
		if(mWorld != null)
			return;
		else
		{
			Box2D.init();
			mWorld = new World(gravity, true);
			mPhysicsDebugRenderer = new Box2DDebugRenderer();
			//TODO maybe need some method to specify what to draw in mPhysicsDebugRenderer
			mPhysicsDebugRenderer.setDrawBodies(true);	//it's default true
			
			mShapeRenderer = new ShapeRenderer();
			mShapeRenderer.setAutoShapeType(true);
			
			//register contact listener
			mWorld.setContactListener(new ContactListener()
			{
				@Override
				public void beginContact(Contact contact)
				{
					for(int i = 0; i < mContactListeners.size; i++)
					{
						mContactListeners.get(i).beginContact(contact);
					}
				}

				@Override
				public void endContact(Contact contact)
				{
					for(int i = 0; i < mContactListeners.size; i++)
					{
						mContactListeners.get(i).endContact(contact);
					}
				}

				@Override
				public void preSolve(Contact contact, Manifold oldManifold)
				{
					for(int i = 0; i < mContactListeners.size; i++)
					{
						mContactListeners.get(i).preSolve(contact, oldManifold);
					}
				}

				@Override
				public void postSolve(Contact contact, ContactImpulse impulse)
				{
					for(int i = 0; i < mContactListeners.size; i++)
					{
						mContactListeners.get(i).postSolve(contact, impulse);
					}
				}
			});
		}
	}
	
	public void setCameraPosition(float x, float y)
	{
		mCamera.position.set(x, y, 0f);
		mCamera.update();
	}
	public void setCameraPosition(Vector2 position)
	{
		setCameraPosition(position.x, position.y);
	}
	public void addCameraPosition(float dx, float dy)
	{
		mCamera.position.add(dx, dy, 0f);
		mCamera.update();
	}
	public void clearCameraPosition()
	{
		mCamera.position.set(0f, 0f, 0f);
		mCamera.update();
	}
	
	/**
	 * Clear camera horizontal restriction
	 */
	public void restrictCameraHorizontal()
	{
		mCameraHorizontalRestriction = null;
	}
	/**
	 * set camera horizontal restriction
	 * @param min
	 * @param max
	 */
	public void restrictCameraHorizontal(float min, float max)
	{
		if(mCameraHorizontalRestriction == null)
			mCameraHorizontalRestriction = new Vector2();
		
		mCameraHorizontalRestriction.set(min, max);
	}
	/**
	 * Clear camera vertical restriction
	 */
	public void restrictCameraVertical()
	{
		mCameraVerticalRestriction = null;
	}
	/**
	 * set camera vertical restriction
	 * @param min
	 * @param max
	 */
	public void restrictCameraVertical(float min, float max)
	{
		if(mCameraVerticalRestriction == null)
			mCameraVerticalRestriction = new Vector2();
		
		mCameraVerticalRestriction.set(min, max);
	}
	
	//TODO set and get method of mDefaultZoomDelta
	private float mDefaultZoomDelta = 0.1f;
	//TODO set and get method of mMaxZoom and mMinZoom
	private float mMaxZoom = 10f;
	private float mMinZoom = 0.1f;
	public void zoomInCamera()
	{
		zoomInCamera(mDefaultZoomDelta);
	}
	public void zoomInCamera(float deltaZoom)
	{
		mCamera.zoom = MathUtils.clamp(mCamera.zoom - deltaZoom, mMinZoom, mMaxZoom);
		mCamera.update();
	}
	public void zoomOutCamera()
	{
		zoomOutCamera(mDefaultZoomDelta);
	}
	public void zoomOutCamera(float deltaZoom)
	{
		mCamera.zoom = MathUtils.clamp(mCamera.zoom + deltaZoom, mMinZoom, mMaxZoom);
		mCamera.update();
	}
	//TODO set and get method of mCameraDefalutZoom
	//and setCurrentAsDefaultZoom()
	private float mCameraDefalutZoom = 1f;
	public void rezoomCamera()
	{
		mCamera.zoom = mCameraDefalutZoom;
		mCamera.update();
	}
	
	public void create()
	{
		if(mCreateListener != null)
			mCreateListener.onCreate(this);
	}
	
	/**
	 * @param deltaTime
	 * @return whether this update does do an update operation
	 */
	public void update(float deltaTime)
	{
		for(int i = mComponents.size - 1; i >= 0; i--)
		{
			if(mComponents.get(i).isEnable())
				mComponents.get(i).update(deltaTime);
		}
		
		if(mUpdateListener != null)
			mUpdateListener.onUpdate(this, deltaTime);
	}
	
	/**
	 * invoked by main loop to restrict camera position before rendering on every frame
	 */
	public final void restrictCamera()
	{
		if(mCameraHorizontalRestriction != null)
		{
			mCamera.position.x = MathUtils.clamp(mCamera.position.x, 
					mCameraHorizontalRestriction.x, mCameraHorizontalRestriction.y);
		}
		if(mCameraVerticalRestriction != null)
		{
			mCamera.position.y = MathUtils.clamp(mCamera.position.y, 
					mCameraVerticalRestriction.x, mCameraVerticalRestriction.y);
		}
		mCamera.update();
	}
	
	//FIXME draw every layer
	public void draw()
	{
		mBatch.setProjectionMatrix(mCamera.combined);
		mBatch.begin();
		
		for(int i = mLayers.size - 1; i >= 0; i--)
		{
			Layer curLayer = mLayers.get(i);
			if(curLayer.isVisible())
			{
				if(!curLayer.isUILayer())
					mBatch.setProjectionMatrix(mCamera.combined);
				else
					mBatch.setProjectionMatrix(mUICamera.combined);
				curLayer.draw(mBatch);
			}
		}

		mBatch.end();
		
		//draw debug UI
		if(mMotherGame.getDebugMode())
			drawDebugUI();
	}

	private Array<Body> mBodiesForDebugDraw = new Array<Body>();
	protected void drawDebugUI()
	{
		//TODO draw debug UI
		if(mPhysicsDebugRenderer != null)
			mPhysicsDebugRenderer.render(mWorld, mCamera.combined);
		
		if(mShapeRenderer != null)
		{
			mShapeRenderer.setProjectionMatrix(mCamera.combined);
			mShapeRenderer.begin();
			
			mWorld.getBodies(mBodiesForDebugDraw);
			for(int i = mBodiesForDebugDraw.size - 1; i >= 0; i--)
			{
				mShapeRenderer.setColor(Constants.BODY_ORIGIN_COLOR);
				mShapeRenderer.x(mBodiesForDebugDraw.get(i).getPosition(), Constants.BODY_ORIGIN_CROSS_SIZE);
				
				mShapeRenderer.setColor(Constants.MESS_CENTER_COLOR);
				mShapeRenderer.x(mBodiesForDebugDraw.get(i).getWorldCenter(), Constants.MESS_CENTER_CROSS_SIZE);
			}
			
			mShapeRenderer.end();
		}
	}
	
	public void updatePhysics(float deltaTime)
	{
		if(mWorld == null)
			return;
		
		float frameTime = Math.min(deltaTime, 0.25f);
		mPhysicsAccumulator += frameTime;
		while(mPhysicsAccumulator >= Constants.TIME_STEP)
		{
			mWorld.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
			mPhysicsAccumulator -= Constants.TIME_STEP;
		}
		
		for(int i = mComponents.size - 1; i >= 0; i--)
		{
			//synchronize drawable position with physics body
			mComponents.get(i).updatePhysics();
		}
	}
	
	public void updateLastFrameValue()
	{
		for(int i = mComponents.size - 1; i >= 0; i--)
		{
			if(mComponents.get(i).isEnable())
				mComponents.get(i).updateLastFrameValue();
		}
	}
	
	/**
	 * make sure call it out of render()
	 */
	public void removeAppendedComponents()
	{
		for(int i = mComponentsToRemoving.size - 1; i >= 0; i--)
		{
			removeComponent(mComponentsToRemoving.get(i));
		}
		mComponentsToRemoving.clear();
	}
	
	//FIXME resize on every layer
	public void resize(int width, int height)
	{
		//TODO maybe separate ViewportFixedType and UIViewportFixedType ?
		if(mViewportType == ViewportFixedType.FixedHeight)
		{
			mCamera.viewportWidth = (mCamera.viewportHeight / height) * width;
			mUICamera.viewportWidth = (mUICamera.viewportHeight / height) * width;
		}
		else if(mViewportType == ViewportFixedType.FixedWidth)
		{
			mCamera.viewportHeight = (mCamera.viewportWidth / height) * width;
			mUICamera.viewportHeight = (mUICamera.viewportWidth / height) * width;
		}
		else	//fixed area
		{
			float factor = (float)width / (float)height;
			mCamera.viewportHeight = (float)Math.sqrt(mCamera.viewportHeight * mCamera.viewportWidth / factor);
			mCamera.viewportWidth = mCamera.viewportHeight * factor;

			mUICamera.viewportHeight = (float)Math.sqrt(mUICamera.viewportHeight * mUICamera.viewportWidth / factor);
			mUICamera.viewportWidth = mUICamera.viewportHeight * factor;
		}
		mCamera.update();
		mUICamera.update();
		
		for(int i = mLayers.size - 1; i >= 0; i--)
		{
			mLayers.get(i).resize(width, height);
		}
	}
	
	public World getPhysicsWorld()
	{
		return mWorld;
	}
	
	public Game getMotherGame()
	{
		return mMotherGame;
	}
	
	public boolean hasAssets()
	{
		return mHasAssets && mSceneAssets != null;
	}

	/**
	 * must call it before begin this scene(by call Game.beginScene(Scene))
	 * before create() and after constructor
	 * @param loadInformation
	 */
	public void addAssetToLoading(String fileName, Class<?> type)
	{
		if(mSceneAssets == null)
			mSceneAssets = new AssetsHelper();
		
		mSceneAssets.load(fileName, type);
		mHasAssets = true;
	}
	public void addTextureAsset(String fileName)
	{
		addAssetToLoading(fileName, Texture.class);
	}
	public void addTextureAtlasAsset(String fileName)
	{
		addAssetToLoading(fileName, TextureAtlas.class);
	}
	public void addSoundAsset(String fileName)
	{
		addAssetToLoading(fileName, Sound.class);
	}
	public void addMusicAsset(String fileName)
	{
		addAssetToLoading(fileName, Music.class);
	}

	public OrthographicCamera getCamera()
	{
		return mCamera;
	}
	public void setViewport(float width, float height)
	{
		mCamera.viewportWidth = width;
		mCamera.viewportHeight = height;
		mCamera.update();
	}

	/**
	 * set a AssetsHelper only when this Scene has no one
	 * @param assetsHepler
	 */
	public void setAssetsHelper(AssetsHelper assetsHepler)
	{
		if(mSceneAssets == null)
			mSceneAssets = assetsHepler;
	}
	public AssetsHelper getAssetsHelper()
	{
		return mSceneAssets;
	}
	
	/**
	 * Maybe some Scene will have several AssetsHelper ?
	 * better not call it in render() cause it may new a short-life object
	 * @return
	 */
	/**
	public Array<AssetsHelper> getAssetsHelpers()
	{
		Array<AssetsHelper> result = new Array<AssetsHelper>();
		result.add(mSceneAssets);
		return result;
	}
	/**/

	public boolean isAssetsLoaded()
	{
		return mGetAssetsLoaded;
	}

	public void finishLoading()
	{
		this.mGetAssetsLoaded = true;
	}

	public OrthographicCamera getUICamera()
	{
		return mUICamera;
	}

	public SpriteBatch getSpriteBatch()
	{
		return mBatch;
	}

	public Color getBackColor()
	{
		return mBackColor;
	}

	public void setBackColor(Color backColor)
	{
		mBackColor = backColor;
	}
	public void setBackColor(float r, float g, float b, float a)
	{
		if(mBackColor == null)
			mBackColor = new Color(r, g, b, a);
		else
			mBackColor.set(r, g, b, a);
	}

	public boolean isPaused()
	{
		return mPaused;
	}

	public void setPaused(boolean paused)
	{
		this.mPaused = paused;
	}

	public float getFadeInDuration()
	{
		return mFadeInDuration;
	}

	public void setFadeInDuration(float fadeInDuration)
	{
		mFadeInDuration = fadeInDuration;
	}

	public float getFadeOutDuration()
	{
		return mFadeOutDuration;
	}

	public void setFadeOutDuration(float fadeOutDuration)
	{
		mFadeOutDuration = fadeOutDuration;
	}

	public boolean isOneTimeUsed()
	{
		return mOneTimeUsed;
	}

	public void setOneTimeUsed(boolean oneTimeUsed)
	{
		mOneTimeUsed = oneTimeUsed;
	}
	
	public void setViewportFixedType(ViewportFixedType type)
	{
		mViewportType = type;
	}
	public ViewportFixedType getViewportFixedType()
	{
		return mViewportType;
	}

	public void setUpdateListener(OnUpdateListener updateListener)
	{
		mUpdateListener = updateListener;
	}

	public void setCreateListener(OnCreateListener createListener)
	{
		mCreateListener = createListener;
	}

	private boolean _handleTouch(InputType type, int screenX, int screenY, int pointer, int button)
	{
		Vector2 viewportPosition = toUIViewportCoordination(screenX, screenY);
		float viewportX = viewportPosition.x;
		float viewportY = viewportPosition.y;
		
		InputEvent event = Pools.obtain(InputEvent.class);
		event.initTouch(this, type, viewportX, viewportY, pointer, button);
		
		boolean handeled = false;
		UIComponent target = hit(viewportX, viewportY);
		if(target != null)
		{
			handeled = target.fire(event);
		}
		Pools.free(event);
		
		return handeled;
	}
	@Override
	public final boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		lastDragPositions.put(pointer, new TouchPositionInfo(new IntVector2(screenX, screenY), false));
		
		boolean handled = _handleTouch(InputType.TouchDown, screenX, screenY, pointer, button);
		
		return onTouchDown(screenX, screenY, pointer, button) || handled;
	}
	/**
	 * Override it to register touch down event
	 * @param screenX
	 * @param screenY
	 * @param pointer
	 * @param button
	 * @return
	 */
	public boolean onTouchDown(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}
	@Override
	public final boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		lastDragPositions.remove(pointer);

		boolean handled = _handleTouch(InputType.TouchUp, screenX, screenY, pointer, button);
		
		return onTouchUp(screenX, screenY, pointer, button) || handled;
	}
	/**
	 * Override it to register touch up event
	 * @param screenX
	 * @param screenY
	 * @param pointer
	 * @param button
	 * @return
	 */
	public boolean onTouchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	//it behaves odd on Android. When you touch down on screen, this touchDragged happens
	//at every frame no matter weather you did move you finger...
	//F**k EMUI, this bug only appears on my phone(HUAWEI Honor8, with EMUI4),
	//and all code works just fine on my girlfriend's VIVO x9.
	public final boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if(Gdx.app.getType() == ApplicationType.Android && mMotherGame.hasDragBug())
		{
			//when switch a scene this value "lastDragPositions.get(pointer)" will be null
			if(lastDragPositions.get(pointer) != null)
			{
				//fix wrong drag event bug on some platform
				if(!(lastDragPositions.get(pointer).startDrag) &&
						lastDragPositions.get(pointer).position.dst(screenX, screenY) <=
							mMotherGame.getTouchSlop())
				{
					return true;	//block this wrong event
				}
				else if(lastDragPositions.get(pointer).startDrag &&
						lastDragPositions.get(pointer).position.equal(screenX, screenY))
				{
					lastDragPositions.get(pointer).startDrag = false;
					return true;
				}
				else
				{
					lastDragPositions.get(pointer).position.set(screenX, screenY);
					lastDragPositions.get(pointer).startDrag = true;
					
					//UIComponents fire input event
					boolean handled = 
							_handleTouch(InputType.TouchDragged, screenX, screenY, pointer, 
									InputEvent.INVALID_BUTTON);
					
					//actually code dealing with touchDragged event
					return onTouchDrag(screenX, screenY, pointer) || handled;
				}
			}
			else
			{
				return true;
			}
		}
		else
		{
			boolean handled = 
					_handleTouch(InputType.TouchDragged, screenX, screenY, pointer, InputEvent.INVALID_BUTTON);
			
			return onTouchDrag(screenX, screenY, pointer) || handled;
		}
	}
	/**
	 * Override it to register touch drag event
	 * @param screenX
	 * @param screenY
	 * @param pointer
	 * @return
	 */
	public boolean onTouchDrag(int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public final boolean keyDown(int keycode)
	{
		InputEvent event = Pools.obtain(InputEvent.class);
		event.initKey(this, InputType.KeyDown, keycode, InputEvent.INVALID_TYPEDCHAR);
		
		boolean handled = false;
		for(int i = mLayers.size - 1; i >= 0; i--)
		{
			handled |= mLayers.get(i).fireInputEvent(event);
			if(handled)
				break;
		}
		
		Pools.free(event);
		
		return onKeyPressed(keycode) || handled;
	}
	/**
	 * Override it to register key down event
	 * @param keycode
	 * @return
	 */
	public boolean onKeyPressed(int keycode)
	{
		return false;
	}

	@Override
	public final boolean keyUp(int keycode)
	{
		InputEvent event = Pools.obtain(InputEvent.class);
		event.initKey(this, InputType.KeyUp, keycode, InputEvent.INVALID_TYPEDCHAR);
		
		boolean handled = false;
		for(int i = mLayers.size - 1; i >= 0; i--)
		{
			handled |= mLayers.get(i).fireInputEvent(event);
			if(handled)
				break;
		}
		
		Pools.free(event);
		
		return onKeyReleased(keycode) || handled;
	}
	/**
	 * Override it to register key up event
	 * @param keycode
	 * @return
	 */
	public boolean onKeyReleased(int keycode)
	{
		return false;
	}

	@Override
	public final boolean keyTyped(char character)
	{
		InputEvent event = Pools.obtain(InputEvent.class);
		event.initKey(this, InputType.KeyTyped, InputEvent.INVALID_KEYCODE, character);
		
		boolean handled = false;
		for(int i = mLayers.size - 1; i >= 0; i--)
		{
			handled |= mLayers.get(i).fireInputEvent(event);
			if(handled)
				break;
		}
		
		Pools.free(event);
		
		return onKeyTyped(character) || handled;
	}
	/**
	 * Override it to register key typed event
	 * @param character
	 * @return
	 */
	public boolean onKeyTyped(char character)
	{
		return false;
	}

	@Override
	public final boolean mouseMoved(int screenX, int screenY)
	{
		Vector2 viewportPosition = toUIViewportCoordination(screenX, screenY);
		float viewportX = viewportPosition.x;
		float viewportY = viewportPosition.y;
		
		InputEvent event = Pools.obtain(InputEvent.class);
		event.initMouse(this, InputType.MouseMoved, viewportX, viewportY, 0);
		
		boolean handled = false;
		UIComponent target = hit(viewportX, viewportY);
		if(target != null)
		{
			handled = target.fire(event);
		}
		Pools.free(event);
		
		return onMouseMoved(screenX, screenY) || handled;
	}
	/**
	 * Override it to register mouse moved event
	 * @param screenX
	 * @param screenY
	 * @return
	 */
	private boolean onMouseMoved(int screenX, int screenY)
	{
		return false;
	}

	@Override
	public final boolean scrolled(int amount)
	{
		return onScrolled(amount) || false;
	}
	/**
	 * Override it to register mouse scrolled event
	 * @param amount
	 * @return
	 */
	public boolean onScrolled(int amount)
	{
		return false;
	}

	public Vector2 toUIViewportCoordination(int screenX, int screenY)
	{
		Vector3 screenPosition = Pools.obtain(Vector3.class);
		screenPosition.set(screenX, screenY, 0f);
		Vector3 viewportPosition = mUICamera.unproject(screenPosition);
		
		Vector2 result = new Vector2(viewportPosition.x, viewportPosition.y);
		Pools.free(screenPosition);
		
		return result;
	}
	
	public Vector2 toViewportCoordination(int screenX, int screenY)
	{
		Vector3 screenPosition = Pools.obtain(Vector3.class);
		screenPosition.set(screenX, screenY, 0f);
		Vector3 viewportPosition = mCamera.unproject(screenPosition);
		
		Vector2 result = new Vector2(viewportPosition.x, viewportPosition.y);
		Pools.free(screenPosition);
		
		return result;
	}

	private UIComponent hit(float viewportX, float viewportY)
	{
		for(int i = mLayers.size - 1; i >= 0; i--)
		{
			UIComponent ui = mLayers.get(i).hit(viewportX, viewportY);
			if(ui != null)
				return ui;
		}
		return null;
	}

	//FIXME clear on every UI Layer
	/**
	 * Called after another Scene shows up.
	 * This will reset all the UIComponents to the very origin state
	 * where nothing has ever happened on them.
	 * As well as clear this scene's unnecessary state.
	 */
	public void clearSceneState()
	{
		for(int i = mLayers.size - 1; i >= 0; i--)
		{
			mLayers.get(i).clearInputEvent();
		}
	}
	
	//FIXME remove or add some unnecessary or necessary lines
	@Override
	public void dispose()
	{
		// TODO remember to check everything need to be disposed
		if(mBatch != null)
			mBatch.dispose();
		
		for(int i = mLayers.size - 1; i >= 0; i--)
		{
			mLayers.get(i).dispose();
		}
		
		if(mSceneAssets != null)
			mSceneAssets.dispose();
		
		if(mWorld != null)
			mWorld.dispose();
		
		if(mShapeRenderer != null)
			mShapeRenderer.dispose();
		
		if(mPhysicsDebugRenderer != null)
			mPhysicsDebugRenderer.dispose();
		
		Selector.cancelScene(this);
	}

	private static class TouchPositionInfo
	{
		public IntVector2 position;
		public boolean startDrag;
		public TouchPositionInfo(IntVector2 position, boolean startDrag)
		{
			this.position = position;
			this.startDrag = startDrag;
		}
	}
	
	public interface OnUpdateListener
	{
		public void onUpdate(Scene scene, float deltaTime);
	}
	public interface OnCreateListener
	{
		public void onCreate(Scene scene);
	}
}
