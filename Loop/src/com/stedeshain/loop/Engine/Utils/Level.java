package com.stedeshain.loop.Engine.Utils;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.stedeshain.loop.Engine.Component.DrawableComponent;
import com.stedeshain.loop.Engine.Component.SceneComponent;
import com.stedeshain.loop.Engine.Component.Body.BoxBody;
import com.stedeshain.loop.Engine.Component.Body.ChainBody;
import com.stedeshain.loop.Engine.Component.Body.CircleBody;
import com.stedeshain.loop.Engine.Scene.Scene;
import com.sun.istack.internal.NotNull;

public class Level implements Disposable
{
	private Scene mScene;
	private String mLevelName;
	private AssetsHelper assets = new AssetsHelper();
	
	private float originMetersPerPixel = 0;
	
	public Level(@NotNull Scene scene, @NotNull String levelName)
	{
		mScene = scene;
		mLevelName = levelName;
	}
	
	public void init()
	{
		Globals g = mScene.getMotherGame().getLuaGlobals();
		
		String fileName = Utils.getLevelsPath(mLevelName + ".lua");
		LuaValue levelChunk = g.loadfile(fileName);
		levelChunk.call();
		
		LuaTable t = (LuaTable)g.get(mLevelName);
		
		float viewportWidth = t.get("viewportWidth").tofloat();
		float viewportHeight = t.get("viewportHeight").tofloat();
		mScene.setViewport(viewportWidth, viewportHeight);
		
		float cameraX = t.get("cameraX").tofloat();
		float cameraY = t.get("cameraY").tofloat();
		OrthographicCamera camera = mScene.getCamera();
		//TODO Scene.setCameraPosition() and Scene.moveCamera() ... ...
		camera.position.set(cameraX, cameraY, 0f);
		camera.update();
		
		LuaTable canvasRaw = (LuaTable)t.get("raw");
		originMetersPerPixel = canvasRaw.get("metersPerPixel").tofloat();
		
		LuaTable comps = (LuaTable)t.get("comps");
		int compCount = comps.length();
		for(int i = 1; i <= compCount; i++)
		{
			LuaTable currentComp = (LuaTable)comps.get(i);
			
			String name = null;
			LuaValue lname = currentComp.get("name");
			if(lname == LuaValue.NIL)
			{
				name = currentComp.get("idName").tojstring();
			}
			else
			{
				name = lname.tojstring();
			}
			String tag = currentComp.get("tag").tojstring();
			String compType = currentComp.get("compType").tojstring();
			String bodyType = currentComp.get("bodyType").tojstring();
			int depth = currentComp.get("depth").toint();
			String textureAtlas = currentComp.get("textureAtlas").tojstring();
			String region = currentComp.get("region").tojstring();
			float originXFactor = currentComp.get("originXFactor").tofloat();
			float originYFactor = currentComp.get("originYFactor").tofloat();
			float angle = currentComp.get("angle").tofloat();
			float positionX = currentComp.get("positionX").tofloat();
			float positionY = currentComp.get("positionY").tofloat();
			float width = currentComp.get("width").tofloat();
			float height = currentComp.get("height").tofloat();
			float density = currentComp.get("density").tofloat();
			float restitution = currentComp.get("restitution").tofloat();
			float friction = currentComp.get("friction").tofloat();
			short categoryBits = currentComp.get("categoryBits").toshort();
			short maskBits = currentComp.get("maskBits").toshort();
			short groupIndex = currentComp.get("groupIndex").toshort();
			boolean isSensor = currentComp.get("isSensor").toboolean();
			
			TextureRegion currentRegion = null;
			if(!textureAtlas.equals("null"))
			{
				TextureAtlas atlas = null;
				String atlasFileName = Utils.getImageAssetPath(textureAtlas);
				AssetsHelper sceneAssets = mScene.getAssetsHelper();
				if(sceneAssets != null && sceneAssets.isLoaded(atlasFileName, TextureAtlas.class))
				{
					atlas = sceneAssets.getTextureAtlas(atlasFileName);
				}
				else
				{
					AssetsHelper globalAssets = mScene.getMotherGame().getGlobalAssets();
					if(globalAssets != null && globalAssets.isLoaded(atlasFileName, TextureAtlas.class))
					{
						atlas = globalAssets.getTextureAtlas(atlasFileName);
					}
					else 
					{
						if(!assets.isLoaded(atlasFileName, TextureAtlas.class))
						{
							assets.loadTextureAtlas(atlasFileName);
							assets.finishLoadingAsset(atlasFileName);
							atlas = assets.getTextureAtlas(atlasFileName);
						}
						else
							atlas = assets.getTextureAtlas(atlasFileName);
					}
				}
				
				currentRegion = atlas.findRegion(region);
			}
			
			SceneComponent comp = null;
			if(compType.equals("DrawableComp"))
			{
				//TODO ...
			}
			else if(compType.equals("BoxBody"))
			{
				float marginH = currentComp.get("marginH").tofloat();
				float marginV = currentComp.get("marginV").tofloat();
				BoxBody body = new BoxBody(new Vector2(positionX, positionY), 
						new Vector2(width, height), 
						marginH, marginV, currentRegion);
				body.setBodyTypeDef(BodyType.valueOf(bodyType));
				body.setOriginFactor(originXFactor, originYFactor);
				body.setDepth(depth);
				body.setAngleDef(angle);
				body.setDensityDef(density);
				body.setRestitutionDef(restitution);
				body.setFrictionDef(friction);
				body.setCategoryBitsDef(categoryBits);
				body.setMaskBitsDef(maskBits);
				body.setGroupIndex(groupIndex);
				body.setIsSensorDef(isSensor);
				body.setName(name);
				body.setTag(tag);
				comp = body;
			}
			else if(compType.equals("CircleBody"))
			{
				float margin = currentComp.get("margin").tofloat();
				CircleBody body = new CircleBody(new Vector2(positionX, positionY), 
						width / 2, margin,
						currentRegion);
				body.setBodyTypeDef(BodyType.valueOf(bodyType));
				body.setOriginFactor(originXFactor, originYFactor);
				body.setDepth(depth);
				body.setAngleDef(angle);
				body.setDensityDef(density);
				body.setRestitutionDef(restitution);
				body.setFrictionDef(friction);
				body.setCategoryBitsDef(categoryBits);
				body.setMaskBitsDef(maskBits);
				body.setGroupIndex(groupIndex);
				body.setIsSensorDef(isSensor);
				body.setName(name);
				body.setTag(tag);
				comp = body;
			}
			else if(compType.equals("ChainBody"))
			{
				ChainBody body = new ChainBody(new Vector2(positionX, positionY),
						new Vector2(width, height), 
						currentRegion);
				
				boolean isLoop = currentComp.get("isLoop").toboolean();
				LuaTable points = (LuaTable)currentComp.get("points");
				int pointCount = points.length();
				for(int j = 1; j <= pointCount; j++)
				{
					LuaTable currentPoint = (LuaTable)points.get(j);
					float x = currentPoint.get("x").tofloat();
					float y = currentPoint.get("y").tofloat();
					body.addPoint(x, y);
				}
				
				body.setBodyTypeDef(BodyType.valueOf(bodyType));
				body.setOriginFactor(originXFactor, originYFactor);
				body.setDepth(depth);
				body.setAngleDef(angle);
				body.setDensityDef(density);
				body.setRestitutionDef(restitution);
				body.setFrictionDef(friction);
				body.setCategoryBitsDef(categoryBits);
				body.setMaskBitsDef(maskBits);
				body.setGroupIndex(groupIndex);
				body.setIsSensorDef(isSensor);
				body.setLoop(isLoop);
				body.setName(name);
				body.setTag(tag);
				comp = body;
			}
			//FIXME temp
			if(comp != null)
//				mScene.addComponent(comp);
			{
				if(comp instanceof DrawableComponent)
					mScene.addComponent((DrawableComponent)comp, "main");
			}
		}
	}

	public Scene getScene()
	{
		return mScene;
	}

	public String getLevelName()
	{
		return mLevelName;
	}

	public float getOriginMetersPerPixel()
	{
		return originMetersPerPixel;
	}

	@Override
	public void dispose()
	{
		assets.dispose();
	}
}
