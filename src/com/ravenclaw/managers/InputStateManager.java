/*
 * This file is strictly bounded by Corvus Corax Entertainment and its prohibited
 * for commercial use, or any use what so ever.
 * This application can be ran only by an Corvus - Corax Employee
 */
package com.ravenclaw.managers;

import javolution.util.FastMap;

import com.ravenclaw.RavenClaw;
import com.ravenclaw.managers.input.GeneralInput;
import com.ravenclaw.managers.input.RavenClawInput;

import corvus.corax.Corax;
import corvus.corax.processing.annotation.Initiate;
import corvus.corax.processing.annotation.Inject;

/**
 * @author Seth
 */
public final class InputStateManager
{
	@Inject
	private RavenClaw _claw;
	
	private final FastMap<Class<?>, RavenClawInput> _data = FastMap.newInstance();
	
	@Initiate
	public void initiate() {
		// Add this inputs later
		addInput(new GeneralInput(), true);
	}

	public void addInput(RavenClawInput rci, boolean loadNow) {
		_data.put(rci.getClass(), rci);

		Corax.instance().processDependancy(rci);

		if(loadNow)
			rci.registerInput();
	}

	public void unregisterAll() {
		for(RavenClawInput input : _data.values()) {
			input.unregisterInput();
		}
	}
	
	/**
	 * Returns the input state. <br>
	 * If the input is not registered, return null.
	 * @param key the class type
	 * @return {@link RavenClawInput}
	 */
	@SuppressWarnings("unchecked")
	public <T extends RavenClawInput> T getInput(Class<T> key) {
		return (T) _data.get(key);
	}
	
}
