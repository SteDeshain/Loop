local M = {}
local moduleName = "className"
_ENV[moduleName] = M;

setmetatable(M, {__index = _ENV})
_ENV = M

Game = "com.stedeshain.loop.Engine.Game"

Button = "com.stedeshain.loop.Engine.Component.Button"
DrawableComponent = "com.stedeshain.loop.Engine.Component.DrawableComponent"
InputEvent = "com.stedeshain.loop.Engine.Component.InputEvent"
Label = "com.stedeshain.loop.Engine.Component.Label"
SceneComponent = "com.stedeshain.loop.Engine.Component.SceneComponent"
Selectable = "com.stedeshain.loop.Engine.Component.Selectable"
Selector = "com.stedeshain.loop.Engine.Component.Selector"
UIComponent = "com.stedeshain.loop.Engine.Component.UIComponent"

BoxBody = "com.stedeshain.loop.Engine.Component.Body.BoxBody"
CircleBody = "com.stedeshain.loop.Engine.Component.Body.CircleBody"

lgame = "com.stedeshain.loop.Engine.Lua.lgame"
lutils = "com.stedeshain.loop.Engine.Lua.lutils"

LoadScene = "com.stedeshain.loop.Engine.Scene.LoadScene"
Scene = "com.stedeshain.loop.Engine.Scene.Scene"
SceneOnUpdateListener = "com.stedeshain.loop.Engine.Scene.Scene$OnUpdateListener"
SceneOnCreateListener = "com.stedeshain.loop.Engine.Scene.Scene$OnCreateListener"
UIScene = "com.stedeshain.loop.Engine.Scene.UIScene"

AssetsHelper = "com.stedeshain.loop.Engine.Utils.AssetsHelper"
Constants = "com.stedeshain.loop.Engine.Utils.Constants"
IntVector2 = "com.stedeshain.loop.Engine.Utils.IntVector2"
LazyBitmapFont = "com.stedeshain.loop.Engine.Utils.LazyBitmapFont"
NinePatch = "com.stedeshain.loop.Engine.Utils.NinePatch"
Utils = "com.stedeshain.loop.Engine.Utils.Utils"

--Gdx
Gdx = "com.badlogic.gdx.Gdx"
Color = "com.badlogic.gdx.graphics.Color"
FreeTypeFontGenerator = "com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator"
Vector2 = "com.badlogic.gdx.math.Vector2"

--Box2D
BodyDef = "com.badlogic.gdx.physics.box2d.BodyDef"
BodyType = "com.badlogic.gdx.physics.box2d.BodyDef$BodyType"
CircleShape = "com.badlogic.gdx.physics.box2d.CircleShape"
FixtureDef = "com.badlogic.gdx.physics.box2d.FixtureDef"
PolygonShape = "com.badlogic.gdx.physics.box2d.PolygonShape"