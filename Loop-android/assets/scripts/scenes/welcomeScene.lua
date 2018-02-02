local M = {}
local moduleName = "welcomeScene"
_G[moduleName] = M;

setmetatable(M, {__index = _ENV})
_ENV = M

sceneName = "welcomeScene"

require "tools/className"
require "tools/pathHelper"
require "tools/log"

logoPath = pathHelper.getImagePath("logo.atlas")

require "scenes/testScene"
sceneInstance = luajava.newInstance(className.LoadScene, game.gameInstance, testScene.sceneInstance)
sceneInstance:setBackColor(1, 1, 1, 1)
sceneInstance:addTextureAtlasAsset(logoPath)
sceneInstance:setMinWaitingTime(1)

createListener = {}
function createListener.onCreate(scene)
	sceneAsset = sceneInstance:getAssetsHelper()

	gdxLogo = luajava.newInstance(className.UIComponent, sceneAsset:getTextureAtlas(logoPath):findRegion("libgdx"))
	gdxLogo:setHeightToFitRegion(20)
	gdxLogo:setSourceAnchor(0, 0.5)
	gdxLogo:setViewportAnchor(0.5, 0.5)
	scene:addComponent(gdxLogo)
	
	gdxLabel = luajava.newInstance(className.Label, globalAssets.smallCalibriFont, "Powered by")
	gdxLabel:setColor(0, 0, 0, 1)
	gdxLabel:setSourceAnchor(1, 0.8)
	gdxLabel:setViewportAnchor(0.49, 0.5)
	scene:addComponent(gdxLabel)
end
createProxy = luajava.createProxy(className.SceneOnCreateListener, createListener)
sceneInstance:setCreateListener(createProxy)
