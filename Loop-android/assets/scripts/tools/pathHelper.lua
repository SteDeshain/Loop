local M = {}
local moduleName = "pathHelper"
_ENV[moduleName] = M;

setmetatable(M, {__index = _ENV})
_ENV = M

function getFontPath(fileName)
	return "fonts/" .. fileName
end

function getImagePath(fileName)
	return "images/" .. fileName
end

function getMusicPath(fileName)
	return "music/" .. fileName
end

function getSoundPath(fileName)
	return "sounds/" .. fileName
end

function getScriptPath(fileName)
	return "scripts/" .. fileName
end