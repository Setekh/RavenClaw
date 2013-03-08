/*
 * This file is strictly bounded by Corvus Corax Entertainment and its prohibited
 * for commercial use, or any use what so ever.
 * This application can be ran only by an Corvus - Corax Employee
 */
package com.ravenclaw.managers.input;

import java.util.ArrayList;
import java.util.logging.Level;

import com.jme3.input.InputManager;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.Trigger;
import com.jme3.renderer.Camera;
import com.ravenclaw.RavenClaw;

import corvus.corax.processing.annotation.Inject;

/**
 * @author Seth
 */
public abstract class RavenClawInput {

	@Inject
	protected Camera cam;
	
	@Inject
	protected RavenClaw claw;
	
	@Inject
	protected InputManager inputManager;
	
	protected ArrayList<String> mappings = new ArrayList<>();
	
	private boolean addToUpdateLoop;
	private boolean isRegistred;

	public final void addInput(String mapping, InputListener listener, Trigger... triggers) {
		
		if(!mappings.contains(mapping))
			mappings.add(mapping);

		if(triggers != null && triggers.length > 0)
			inputManager.addMapping(mapping, triggers);

		if(listener != null)
			inputManager.addListener(listener, mapping);
	}
	
	public final void addInput(String mapping, Trigger trigger, InputListener listener) {
		addInput(mapping, listener, trigger);
	}

	protected abstract void registerInputImpl();
	
	public final void registerInput() {
		registerInputImpl();
		isRegistred = true;

		//if(isAddedToUpdateLoop())
			//claw.addUpdater(this);
	}
	
	public void unregisterInput()
	{
		for(String map : mappings)
			inputManager.deleteMapping(map);
		
		isRegistred = false;
		mappings.clear();
	}
	
	/**
	 * @return the isRegistred
	 */
	public boolean isRegistred()
	{
		return isRegistred;
	}
	
	/* (non-Javadoc)
	 * @see com.raven.RavenclawUpdate#updateResized(int, int)
	 */
	//@Override
	public void updateResized(int width, int height) {
	}
	
	public void onLevelChanged(Level oldLevel, Level newLevel) {
	}

	/* (non-Javadoc)
	 * @see com.raven.RavenclawUpdate#update(float)
	 */
	//@Override
	public void update(float tpf) {
	}
	
	/**
	 * @return the addToUpdateLoop
	 */
	public boolean isAddedToUpdateLoop() {
		return addToUpdateLoop;
	}
	
	/**
	 * @param addToUpdateLoop the addToUpdateLoop to set
	 */
	public void addToUpdateLoop() {
		addToUpdateLoop = true;
	}
	
}