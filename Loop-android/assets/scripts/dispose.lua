require "tools/log"

local disposeCount = 0;

if(type(globalAssets) == "table") then
	for _, v in pairs(globalAssets) do
		if type(v) == "userdata" and type(v.dispose) == "function" then
			disposeCount = disposeCount + 1
			v:dispose()
		end
	end
end

log.info("lua disposed " .. disposeCount .. " global assets used in script.")