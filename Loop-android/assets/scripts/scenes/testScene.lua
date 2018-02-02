local M = {}
local moduleName = "testScene"
_G[moduleName] = M;

setmetatable(M, {__index = _ENV})
_ENV = M

sceneName = "testScene"

require "tools/className"
require "tools/pathHelper"
require "tools/log"

sceneInstance = luajava.newInstance(className.Scene, game.gameInstance, luajava.newInstance(className.Vector2, 10, 5))
--sceneInstance:addPhysicsModule()

createListener = {}
function createListener.onCreate(scene)
	scene:addPhysicsModule()
	game.disposeScene(launchScene)
	
	BodyType = luajava.bindClass(className.BodyType)
	utils = luajava.bindClass(className.Utils)
	
	circlrComp = luajava.newInstance(className.CircleBody, luajava.newInstance(className.Vector2, 0, 0), 0.5, nil)
	circlrComp:setBodyTypeDef(BodyType.DynamicBody)
	
	whiteTextureRegion = utils:getColorTextureRegion(luajava.newInstance(className.Color, 1, 1, 1, 1), 10, 10)
	planeComp = luajava.newInstance(className.BoxBody, luajava.newInstance(className.Vector2, -3, -2), luajava.newInstance(className.Vector2, 6, 0.1), whiteTextureRegion)
	planeComp:setBodyTypeDef(BodyType.StaticBody)
	planeComp:setOwnAssets(true)
	
	blueTextureRegion = utils:getColorTextureRegion(luajava.newInstance(className.Color, 0, 1, 0.5, 1), 10, 10)
	boxComp = luajava.newInstance(className.BoxBody, luajava.newInstance(className.Vector2, 1, 1), luajava.newInstance(className.Vector2, 1, 0.4), blueTextureRegion)
	boxComp:setBodyTypeDef(BodyType.DynamicBody)
	boxComp:setOwnAssets(true)
		
	--[[
	blueTextureRegion2 = utils:getColorTextureRegion(luajava.newInstance(className.Color, 0, 1, 0.5, 1), 10, 10)
	boxComp2 = luajava.newInstance(className.BoxBody, luajava.newInstance(className.Vector2, 0.5, 1.5), luajava.newInstance(className.Vector2, 0.8, 0.3), blueTextureRegion2)
	boxComp2:setBodyTypeDef(BodyType.DynamicBody)
	boxComp2:setOwnAssets(true)
	
	blueTextureRegion3 = utils:getColorTextureRegion(luajava.newInstance(className.Color, 0, 1, 0.5, 1), 10, 10)
	boxComp3 = luajava.newInstance(className.BoxBody, luajava.newInstance(className.Vector2, -1, 0.3), luajava.newInstance(className.Vector2, 0.2, 0.7), blueTextureRegion3)
	boxComp3:setBodyTypeDef(BodyType.DynamicBody)
	boxComp3:setOwnAssets(true)
	
	tempTextureRegion = utils:getColorTextureRegion(luajava.newInstance(className.Color, 0, 1, 0.5, 1), 10, 10)
	box2 = luajava.newInstance(className.BoxBody, luajava.newInstance(className.Vector2, 0.1, 2), luajava.newInstance(className.Vector2, 0.4, 0.2), tempTextureRegion)
	box2:setBodyTypeDef(BodyType.DynamicBody)

	box3 = luajava.newInstance(className.BoxBody, luajava.newInstance(className.Vector2, -2, 2), luajava.newInstance(className.Vector2, 0.4, 0.2), nil)
	box3:setBodyTypeDef(BodyType.DynamicBody)
	--box3:setRestitutionDef(0)
	--]]
	
	scene:addComponent(planeComp)
	scene:addComponent(circlrComp)
	scene:addComponent(boxComp)
	--scene:addComponent(boxComp2)
	--scene:addComponent(boxComp3)
	--scene:addComponent(box2)
	--scene:addComponent(box3)
end
createProxy = luajava.createProxy(className.SceneOnCreateListener, createListener)
sceneInstance:setCreateListener(createProxy)