package com.stedeshain.loop.Engine;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.stedeshain.loop.Engine.Utils.AssetsHelper;
import com.stedeshain.loop.Engine.Utils.Constants;
import com.stedeshain.loop.Engine.Utils.Utils;

public abstract class Game implements ApplicationListener
{
	//TODO maybe remove these static methods ?
	private static Game mGameInstance = null;
	public static Game getInstance()
	{
		return Game.mGameInstance;
	}
	
	private ResourceFinder mAndroidLuaScriptFinder;
	private Globals mLuaGlobals;
	private String mScriptPath;
	
	private Scene mCurrentScene;
	private Scene mNextScene;
	
	private AssetsHelper mGlobalAssets = new AssetsHelper();
	private AssetsHelper mLoadingAssets = null;
	
	private boolean mDebugMode = false;
	/**
	 * All scenes in a game should have the same UIViewport
	 */
	private Vector2 mUIViewport;
	
	private boolean mIsTransiting = false;
	private float mTransitionProgress = 0f;
	private float mTransitionElapsed = 0f;
	private boolean mFadeIn = false;
	private boolean mFadeOut = false;
	private boolean mIsLoadingAssets = false;
	private FrameBuffer mFrameBuffer;
	private SpriteBatch mGameBatch;
	private Color mBatchColor;
	
	private boolean mHasDragBug = false;
	private int mTouchSlop;
	
	public Game()
	{
		this(null, null, 0);
	}
	
	public Game(String scriptPath, ResourceFinder androidLuaScriptFinder, int androidTouchSlop)
	{
		mScriptPath = scriptPath;
		mAndroidLuaScriptFinder = androidLuaScriptFinder;
		mTouchSlop = androidTouchSlop;

		if(Game.mGameInstance == null)
			Game.mGameInstance = this;
		else
			throw new RuntimeException("Only one Game instance can be created !");
	}
	
	/**
	 * The Game will call it when the prevScene should vanish.
	 * User can not call it !!!
	 * @param scene
	 */
	private void rawSetScene(Scene scene)
	{
		if(mCurrentScene != null)
		{
			mCurrentScene.clearSceneState();

			if(mCurrentScene.isOneTimeUsed())
			{
				mCurrentScene.dispose();
			}
		}
		
		mCurrentScene = scene;
		mCurrentScene.create();
		mTransitionElapsed = 0;
		mFadeIn = true;
		mFadeOut = false;
		Gdx.input.setInputProcessor(mCurrentScene);

		initBufferFields();
		
		//it is necessary !!!
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/**
	 * start the process that previous scene fade out and then next scene fade in
	 * User can not call it either !!!
	 * @param scene
	 */
	private void postScene(Scene scene)
	{
		mNextScene = scene;
		mTransitionElapsed = 0;
		mFadeOut = true;
		mFadeIn = false;
		
		initBufferFields();
	}

	private void initBufferFields()
	{
		if(mFrameBuffer == null)
			mFrameBuffer = new FrameBuffer(Constants.GAME_PIXEL_FORMAT, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		if(mGameBatch == null)
			mGameBatch = new SpriteBatch();
		if(mBatchColor == null)
			mBatchColor = new Color();
		mBatchColor = Color.WHITE;
		mBatchColor.a = 0f;
	}

	/**
	 * That's the method that a user should call to set a new Scene.
	 * If there is a Scene that hasn't finished its showing(like fading out or fading in), this method will do nothing
	 * @param scene
	 */
	public void beginScene(Scene scene)
	{
		if(mIsTransiting)	//Cannot begin a Scene when previous Scene's showing isn't finished
			return;
		
		mIsTransiting = true;
		//start to load assets of the next scene, if it has any
		if(scene.hasAssets() && !scene.isAssetsLoaded())	//start to load
		{
			mLoadingAssets = scene.getAssetsHelper();
		}
		postScene(scene);
	}
	
	/**
	 * can only be invoked before everything starts, 
	 * after this was called, Game start this scene immediately,
	 * user better set a LoadScene, to load global assets used in whole game.
	 * And the first scene MUST NOT have any assets to loading !
	 * @param scene : first scene shown in the game, like the title scene, or a launch scene,
	 *  or a welcome scene, etc.
	 */
	public void setFirstScene(Scene scene)
	{
		rawSetScene(scene);
	}

	public AssetsHelper getGlobalAssets()
	{
		return mGlobalAssets;
	}
	
	public Globals getLuaGlobals()
	{
		return mLuaGlobals;
	}

	public boolean getDebugMode()
	{
		return mDebugMode;
	}
	public void setDebugMode(boolean debugMode)
	{
		mDebugMode = debugMode;
		if(mDebugMode == true)
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
	public void toogleDebugMode()
	{
		setDebugMode(!mDebugMode);
	}

	public boolean hasDragBug()
	{
		return mHasDragBug;
	}
	//TODO ask the player to select weather his or her phone has a drag bug.
	/**
	 * ask the player to select weather his or her phone has a drag bug.
	 * Maybe use a little scene to test drag event with mHasDragBug == false,
	 * and let the player observe what happens and select weather that bug happened.
	 * (Maybe recommend the user of EMUI to set mHasDragBug as true)
	 * @param hasDragBug
	 */
	public void setHasDragBug(boolean hasDragBug)
	{
		mHasDragBug = hasDragBug;
	}

	public int getTouchSlop()
	{
		return mTouchSlop;
	}
	
	public Vector2 getUIViewport()
	{
		return mUIViewport;
	}
	/**
	 * Better set the UIViewport as the same as the screen pixel size.
	 * Set to any other value may cause some unpredictable issues.
	 * Because when I dealt with the font's size calculation, I just forget about the viewport thing,
	 * and do all the calculation with its original pixel size.
	 * And maybe it's impossible to debug it, I guess.
	 * @param UIViewport
	 */
	public void setUIViewport(Vector2 UIViewport)
	{
		mUIViewport = UIViewport;
	}
	
	public boolean isTransiting()
	{
		return mIsTransiting;
	}
	public float getTransitionProgress()
	{
		return mTransitionProgress;
	}

	public boolean isFullScreen()
	{
		return Gdx.graphics.isFullscreen();
	}
	private int originWindowWidth = 0;
	private int originWindowHeight = 0;
	/**
	 * It's not recommended to change window size or reset full screen while using PixelScene,
	 * because it will cause pixel jittering
	 * @param fullScreen
	 */
	public void setFullScreen(boolean fullScreen)
	{
		if(Gdx.app.getType() != ApplicationType.Desktop)
			return;
	
		if(fullScreen)
		{
			originWindowWidth = Gdx.graphics.getWidth();
			originWindowHeight = Gdx.graphics.getHeight();
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
		else
		{
			Gdx.graphics.setWindowedMode(originWindowWidth, originWindowHeight);
		}
	}
	
	@Override
	public void create()
	{
		mLuaGlobals = JsePlatform.standardGlobals();
		mLuaGlobals.get("package").set("path", "?.lua;" + mScriptPath);
		if(Gdx.app.getType() == ApplicationType.Android)
			mLuaGlobals.finder = mAndroidLuaScriptFinder;
		
		try
		{
			String initFileName = Utils.getScriptsPath("init.lua");
			LuaValue initChunk = mLuaGlobals.loadfile(initFileName);
			initChunk.call(LuaValue.userdataOf(initFileName),
					CoerceJavaToLua.coerce(this));
		}
		catch(LuaError e)
		{
			Utils.error("Cannot load lua init scripts: " + e.toString());
		}
	}

	@Override
	public void resize(int width, int height)
	{
		if(mCurrentScene == null)
			return;
		
		mCurrentScene.resize(width, height);
	}

	@Override
	public void render()
	{
		if(mCurrentScene == null)
			return;
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		//logic update
		mCurrentScene.update(deltaTime);
		mCurrentScene.restrictCamera();
		
		float fadeInDuration = mCurrentScene.getFadeInDuration();
		float fadeOutDuration = mCurrentScene.getFadeOutDuration();
		
		//render
		mFrameBuffer.begin();
		final Color backColor = mCurrentScene.getBackColor();
		Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, backColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mCurrentScene.draw();
		mFrameBuffer.end();
		Texture bufferTexture = mFrameBuffer.getColorBufferTexture();
		
		if(mFadeOut)
		{
			mTransitionElapsed += deltaTime;
			
			//this line will prevent screen blink after fade out before fade in
			if(mTransitionElapsed > fadeOutDuration)
				mTransitionElapsed = fadeOutDuration;

			mTransitionProgress = 1 - mTransitionElapsed / fadeOutDuration;
			mBatchColor.a = mTransitionProgress;
			mGameBatch.setColor(mBatchColor);

			if(mTransitionElapsed >= fadeOutDuration)
			{
				//step into a waiting black screen, to load next scene's assets
				mIsLoadingAssets = true;
				mTransitionProgress = 0f;
				mFadeOut = false;
				mTransitionElapsed = 0f;
			}
		}
		else if(mIsLoadingAssets)
		{
			//load mLoadingAssets here
			if(mLoadingAssets != null)
			{
				if(mLoadingAssets.update())
				{
					//finish loading, show next scene
					mNextScene.finishLoading();
					rawSetScene(mNextScene);
					mNextScene = null;
					mIsLoadingAssets = false;
				}
				else
				{
					//do nothing but show a dark screen
					//TODO should has a better way to log loading progress
					Gdx.app.debug(Constants.ENGINE_TAG, "loading assets... " + mLoadingAssets.getProgress() * 100 + "%");
				}
			}
			else	//no assets to load
			{
				rawSetScene(mNextScene);
				mNextScene = null;
				mIsLoadingAssets = false;
			}
		}
		else if(mFadeIn)
		{
			mTransitionElapsed += deltaTime;

			mTransitionProgress = mTransitionElapsed / fadeInDuration;
			mBatchColor.a = mTransitionProgress;
			mGameBatch.setColor(mBatchColor);

			if(mTransitionElapsed >= fadeInDuration)
			{
				mFadeOut = false;
				mFadeIn = false;
				mTransitionElapsed = 0f;
				mIsTransiting = false;
				mTransitionProgress = 1f;
				mBatchColor.a = 1;
				mGameBatch.setColor(mBatchColor);
			}
		}

		Gdx.gl.glClearColor(Constants.SCREEN_FLUSH_COLOR.r,
				Constants.SCREEN_FLUSH_COLOR.g,
				Constants.SCREEN_FLUSH_COLOR.b,
				Constants.SCREEN_FLUSH_COLOR.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mGameBatch.begin();
		mGameBatch.draw(bufferTexture, 0f, 0f, 0f, 0f, 
				bufferTexture.getWidth(), bufferTexture.getHeight(), 1f, 1f, 0f, 0, 0,
				bufferTexture.getWidth(), bufferTexture.getHeight(),
				false, true);
		mGameBatch.end();
		
		//update physics
		mCurrentScene.updatePhysics(deltaTime);
		
		//update last frame value
		mCurrentScene.updateLastFrameValue();
		
		//MUST call at the end of render()
		mCurrentScene.removeAppendedComponents();
	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose()
	{
		// TODO ...
		if(mCurrentScene != null)
			mCurrentScene.dispose();

		if(mFrameBuffer != null)
			mFrameBuffer.dispose();
		
		if(mGameBatch != null)
			mGameBatch.dispose();
		
		if(mGlobalAssets != null)
			mGlobalAssets.dispose();
		
		try
		{
			String disposeFileName = Utils.getScriptsPath("dispose.lua");
			LuaValue disposeChunk = mLuaGlobals.loadfile(disposeFileName);
			disposeChunk.call(disposeFileName);
		}
		catch(LuaError e)
		{
			Utils.error("Cannot do lua dispose scripts: " + e.toString());
		}
		
		Utils.dispose();
	}
}
