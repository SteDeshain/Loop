package com.stedeshain.loop.Engine.Component;

import com.badlogic.gdx.math.Vector2;

public interface Selectable
{
	public Vector2 getSize();
	public Vector2 getCenterAnchor();
	
	public boolean isSelected();
	
	public void select();
	public void deselect();
	
	/**
	 * get the left Selectable component of this Selectable component
	 * @return null if there is no one
	 */
	public Selectable toLeft();
	/**
	 * see {@link #toLeft()}.
	 * @return
	 */
	public Selectable toTop();
	/**
	 * see {@link #toLeft()}.
	 * @return
	 */
	public Selectable toRight();
	/**
	 * see {@link #toLeft()}.
	 * @return
	 */
	public Selectable toBottom();
}
