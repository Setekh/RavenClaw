/*
 * This file is strictly bounded by Corvus Corax Entertainment and its prohibited
 * for commercial use, or any use what so ever.
 * This application can be ran only by an Corvus - Corax Employee
 */
package com.ravenclaw;

import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.log4j.Logger;

import com.ravenclaw.utils.Utils;

/**
 * @author Seth
 */
public final class GroovyScriptManager {

	private static final Logger _log = Logger.getLogger(GroovyScriptManager.class);

	private static final File scripts = new File("./assets/");
	private static final GroovyScriptManager instance = new GroovyScriptManager();
	
	private final GroovyScriptEngine _engine;
	
	public GroovyScriptManager() {
		_engine = new GroovyScriptEngine(getPackageURLs());
		_log.info("Ready.");
	}
	
	/**
	 * Creates an instance of the specified class, if the class has not been loaded yet, it will be now.
	 * @return the specified initiated object, with the default constructor
	 * @see GroovyScriptManager#loadClass(String)
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T load(String scriptName) {
		try
		{
			T obj = (T) loadClass(scriptName).newInstance();
			return obj;
			
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			_log.warn("Failed loading script: "+scriptName+". ", e);
			return null;
		}
	}
	
	/**
	 * Creates an instance of the specified class, if the class has not been loaded yet, it will be now.<br>
	 * Supports native types.
	 * @return the specified initiated object
	 * @see GroovyScriptManager#loadClass(String)
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T loadNative(String scriptName, Object... objects) {
		try
		{
			Class<?>[] types = new Class<?>[objects.length] ;
			
			int pointer = 0;
			for(Object obj : objects) {
				types[pointer++] = Utils.identifyAndGet(obj.getClass());
			}
			
			T obj = (T) load(scriptName, types, objects);
			return obj;
		}
		catch (Exception e)
		{
			_log.warn("Failed loading script: "+scriptName+". ", e);
			return null;
		}
	}
	
	/**
	 * Creates an instance of the specified class, if the class has not been loaded yet, it will be now.<br>
	 * <b><font color = #FF0000>Warning:</font></b> It dose not support native types. Use: {@link #loadNative(String, Object...)}
	 * @return the specified initiated object
	 * @see GroovyScriptManager#loadClass(String)
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T load(String scriptName, Object... objects) {
		try
		{
			Class<?>[] types = new Class<?>[objects.length] ;
			
			int pointer = 0;
			for(Object obj : objects) {
				types[pointer++] = obj.getClass();
			}
			
			T obj = (T) load(scriptName, types, objects);
			return obj;
		}
		catch (Exception e)
		{
			_log.warn("Failed loading script: "+scriptName+". ", e);
			return null;
		}
	}
	
	/**
	 * Creates an instance of the specified class, if the class has not been loaded yet, it will be now.
	 * @return the specified initiated object
	 * @see GroovyScriptManager#loadClass(String)
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T load(String scriptName, Class<?>[] types, Object... objects) {
		try
		{
			T obj = (T) loadClass(scriptName).getConstructor(types).newInstance(objects);
			return obj;
		}
		catch (Exception e)
		{
			_log.warn("Failed loading script: "+scriptName+". ", e);
			return null;
		}
	}
	
	/**
	 * Loads the specified script, will return null if the script is not a returnable object
	 * @param scriptName must not contain file extension
	 * @return class or null
	 */
	public synchronized Class<?> loadClass(String scriptName) {
		try
		{
			//Class<?> clz = _engine.loadScriptByName(scriptName+ (scriptName.endsWith(".groovy") ? "" : ".groovy"));
			Class<?> clz = _engine.loadScriptByName(scriptName);
			return clz;
		}
		catch (ResourceException | ScriptException e)
		{
			_log.warn("Failed loading script: ", e);
		}
		
		return null;
	}

	/**
	 * @return the instance
	 */
	public static GroovyScriptManager getInstance()
	{
		return instance;
	}
	
	/**
	 * @return the engine
	 */
	public GroovyScriptEngine getEngine()
	{
		return _engine;
	}
	
	public void reparsePackages() {
		URL[] urls = getPackageURLs();
		
		for(URL url : urls) {
			
			if(!isUsed(url))
				_engine.getGroovyClassLoader().addURL(url);
		}
		
	}
	
	private boolean isUsed(URL url)
	{
		URL[] usedURLS = _engine.getGroovyClassLoader().getURLs();
		for(URL link : usedURLS) {
			if(link.equals(url)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static URL[] getPackageURLs() {
		Collection<File> files = FileUtils.listFilesAndDirs(scripts, FalseFileFilter.FALSE, new AbstractFileFilter() {
			
			@Override
			public boolean accept(File dir, String name)
			{
				return name.lastIndexOf(".svn") == -1;
			}
		});
		URL[] urls = new URL[files.size()];
		
		int position = 0;
		for(File dir : files)
		{
			try
			{
				urls[position++] = dir.toURI().toURL();
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
		}
		
		return urls;
	}
}
