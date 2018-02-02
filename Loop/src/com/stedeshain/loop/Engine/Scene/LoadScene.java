package com.stedeshain.loop.Engine.Scene;

import com.badlogic.gdx.utils.Array;
import com.stedeshain.loop.Engine.Game;
import com.stedeshain.loop.Engine.Utils.AssetsHelper;
import com.sun.istack.internal.NotNull;

/**
 * Must be a very simple one, like contains only one texture to shown,
 * and the only texture may be loaded before everything starts in main Game,
 * which takes very little time.
 * So a LoadScene will not load its own assets
 * @author SteDeshain
 */
public class LoadScene extends UIScene
{
	/**
	 * if a LoadScene find the Game assets has not been loaded yet,
	 * this LoadScene will load them, as well as its next scene's assets
	 */
	private static boolean sHasLoadedGlobalAssets = false;

	private Scene mNextScene;
	private Array<AssetsHelper>	allAssetsToLoading;
	
	private float mMinWaitingTime = 0f;
	private boolean mCarryGlobalAssets = false;
	private float mProgress = 0f;
	
	private int mCurrentLoadingIndex = 0;

	/**
	 * new a LoadScene must be done after its nextScene has added all its assets to loading
	 * @param motherGame
	 * @param nextScene
	 */
	public LoadScene(Game motherGame, @NotNull Scene nextScene)
	{
		super(motherGame);
		
		mNextScene = nextScene;
		allAssetsToLoading = new Array<AssetsHelper>();
		if(nextScene.getAssetsHelper() != null)	//not use hasAssets() cause LoadScene behaves odd there
			allAssetsToLoading.add(nextScene.getAssetsHelper());
		if(!LoadScene.sHasLoadedGlobalAssets)
		{
			allAssetsToLoading.add(motherGame.getGlobalAssets());
			mCarryGlobalAssets = true;
		}
	}

	private void onFinishingLoad()
	{
		mNextScene.finishLoading();
		
		if(mCarryGlobalAssets)
			LoadScene.setHasLoadedGlobalAssets();

		//TODO begin next scene
		getMotherGame().beginScene(mNextScene);
	}
	
	public float getProgress()
	{
		return mProgress;
	}
	
	public float getMinWaitingTime()
	{
		return mMinWaitingTime;
	}
	public void setMinWaitingTime(float minWaitingTime)
	{
		if(minWaitingTime < 0)
			minWaitingTime = 0;
		mMinWaitingTime = minWaitingTime;
	}

	private float mElipsedTime = 0f;
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		
		mElipsedTime += deltaTime;
		
		if(mMinWaitingTime == 0f && mCurrentLoadingIndex >= allAssetsToLoading.size)
			return;
		else if(mMinWaitingTime != 0f && mCurrentLoadingIndex >= allAssetsToLoading.size)
		{
			if(mElipsedTime >= mMinWaitingTime)
				return;
		}
		
		if(allAssetsToLoading.get(mCurrentLoadingIndex).update())
		{
			//finished mCurrentLoadingIndex loading
			mCurrentLoadingIndex++;
			if(mCurrentLoadingIndex >= allAssetsToLoading.size)
			{
				//finish all loading
				mProgress = 1f;
				if(mMinWaitingTime == 0f)
					onFinishingLoad();
				else
				{
					mCurrentLoadingIndex--;	//valid the operation of "allAssetsToLoading.get(mCurrentLoadingIndex)"
					if(mElipsedTime >= mMinWaitingTime)
					{
						mCurrentLoadingIndex++;
						onFinishingLoad();
					}
				}
			}
		}
		else
		{
			float currentProgress = allAssetsToLoading.get(mCurrentLoadingIndex).getProgress();
			mProgress = ((float)mCurrentLoadingIndex + currentProgress) / (float)allAssetsToLoading.size;
		}
	}
	
	@Override
	public void clearSceneState()
	{
		super.clearSceneState();
		
		mCurrentLoadingIndex = 0;
		mProgress = 0f;
	}

	public static boolean hasLoadedGlobalAssets()
	{
		return sHasLoadedGlobalAssets;
	}
	public static void setHasLoadedGlobalAssets()
	{
		sHasLoadedGlobalAssets = true;
	}
}
