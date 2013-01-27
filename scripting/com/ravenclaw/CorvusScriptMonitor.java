/*
 * This file is strictly bounded by Corvus Corax Entertainment and its prohibited
 * for commercial use, or any use what so ever.
 * This application can be ran only by an Corvus - Corax Employee
 */
package com.ravenclaw;

import javolution.util.FastMap;

import com.ravenclaw.utils.RavenClawScript;

/**
 * @author Seth
 * @deprecated
 */
final class CorvusScriptMonitor {
	
	/**
	 * This is only use internally here,
	 * to check for script code changes.
	 */
	private final FastMap<String, Class<?>> _cache = new FastMap<String, Class<?>>();

	private final FastMap<String, RavenClawScript> _subscribers = new FastMap<String, RavenClawScript>();
	
	/**
	 * When {@link GroovyScriptManager#loadClass(String)} is called this will check if the 
	 * specific script is already cached, if it is and it's a instance of RavenClawScript, the {@link RavenClawScript#unload} method will be invoked.<br>
	 * The load feature of the class is handled by {@link #loadedObject(Object)}
	 */
	protected void loadedClass(Class<?> cls) {
		Class<?> cached = _cache.get(cls.getName());
		System.out.println("Loaded");

		if(cached != null && cached != cls) { // Means it got modified and recompiled.
			
			System.out.println("Modified!");

			RavenClawScript script = _subscribers.remove(cached.getName());
			script.unload();
			
			System.out.println("Unloading");
		}
		else if(cached == null) {
			_cache.put(cls.getName(), cls);
		}
	}
	
	/**
	 * When {@link GroovyScriptManager#load(String)} methods are called this will check if the 
	 * specific script is already cached, if it is and it's a instance of RavenClawScript and it's flagged as new, the {@link RavenClawScript#load} method will be invoked.<br>
	 */
	protected void loadedObject(Object obj) {
		
		System.out.println("Got script: "+obj.getClass().getSimpleName()+" instanceof: "+(obj instanceof RavenClawScript)+ " cached: "+_subscribers.containsKey(obj.getClass().getName()));
		
		if(obj instanceof RavenClawScript) {
			boolean cached = _subscribers.containsKey(obj.getClass().getName());

			System.out.println("bip");
			if(!cached) {
				System.out.println("bip2");
				((RavenClawScript) obj).load();
				_subscribers.put(obj.getClass().getName(), (RavenClawScript) obj);
			}
		}
	}
}
