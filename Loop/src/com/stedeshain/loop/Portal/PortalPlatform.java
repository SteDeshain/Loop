//this package is not a part of Loop.Engine
//but a part of the Loop game
package com.stedeshain.loop.Portal;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.stedeshain.loop.Engine.Component.Body.OneSidedPlatform;

/**
 * A portal has a rectangle shape<br>
 * It behaves like OneSidedPlatform: won't block any Body's movement
 * @author SteDeshain
 */
public class PortalPlatform extends OneSidedPlatform
{
	public PortalPlatform(Vector2 position, Vector2 size, float horizontalMargin, float verticalMargin,
			TextureRegion textureRegion)
	{
		super(position, size, horizontalMargin, verticalMargin, textureRegion);
	}
}
