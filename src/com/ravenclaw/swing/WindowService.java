/*
 * Copyright (c) 2009-2012 jMonkeyEngine & Corvus Corax
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine', 'Corvus Corax', 'Raven Claw' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ravenclaw.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.util.HashMap;

import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXFrame;

import com.javadocking.DockingManager;
import com.javadocking.dock.SplitDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.model.FloatDockModel;
import com.javadocking.model.codec.DockModelPropertiesDecoder;
import com.javadocking.model.codec.DockModelPropertiesEncoder;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.swing.windows.SceneWindow;

import corvus.corax.Corax;
import corvus.corax.processing.annotation.Initiate;
import corvus.corax.processing.annotation.Inject;

/**
 * @author scorn
 */
public final class WindowService {
	
	private static final Logger _log = Logger.getLogger(WindowService.class);
	
	private FloatDockModel dockModel;
	private SplitDock rootDock;
	
	private final RavenClaw claw;
	private final JXFrame frame;
	
	private FastMap<String, RavenClawWindow> windows = new FastMap<>();
	
	public WindowService(RavenClaw claw, JXFrame frame) {
		this.claw = claw;
		this.frame =  frame;
		
		Corax.instance().addSingleton(getClass(), this);
	}

	@Initiate
	public void load() { // thise are the core ones and should allways be there
	}
	
	public void registerWindow(RavenClawWindow window) {
		registerWindow(window, true);
	}
	
	public void registerWindow(RavenClawWindow window, boolean addToDock) {

		RavenClawWindow win = windows.put(window.getClass().getName(), window);

		if(!window.isInitiate)
			window.start(addToDock);

		if(win != null) {
			win.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends RavenClawWindow> T getWindow(Class<T> windowType) {
		return (T) windows.get(windowType.getName());
	}
	
	public void unregisterWindow(Class<?> type) {
		
		RavenClawWindow win = windows.remove(type.getName());
		
		if(win != null) {
			win.close();
		}
	}
	
	public void loadDefault() {
		load("./assets/sdk/default.dck");
		
		if(dockModel == null)
			doUnorganisedLayout();
	}
	
	public void loadLast() {
		File file = new File("./assets/sdk/", "lastSessions.dck");
		
		if(file.exists())
			load(file);
		else {
			loadDefault();
		}
		
		if(dockModel == null)
			doUnorganisedLayout();
	}
	
	public void load(String source) {
		File file = new File(source + (source.endsWith(".dck") ? "" : ".dck"));
		load(file);
	}
	
	public void load(File file) {
		DockModelPropertiesDecoder dockModelDecoder = new DockModelPropertiesDecoder();
		if(file.exists() && dockModelDecoder.canDecodeSource(file.getPath())) {
			try {
	
				// Create the map with the dockables, that the decoder needs.
				HashMap<String, Dockable> dockablesMap = new HashMap<String, Dockable>();

				// Create the map with the owner windows, that the decoder needs.
				HashMap<String, Component> ownersMap = new HashMap<String, Component>();
				ownersMap.put("RavenClawWindow", frame);

				for (RavenClawWindow i : windows.values()) {
					dockablesMap.put(i.getID(), i);
				}
				
				// Decode the file.
				dockModel = (FloatDockModel) dockModelDecoder.decode(file.getPath(), dockablesMap, ownersMap, null);
	
				rootDock = (SplitDock) dockModel.getRootDock("Main");
				frame.getContentPane().add(rootDock, BorderLayout.CENTER);
			} catch (Exception e) {
				_log.warn("Failed laoding docking config["+file.getName()+"]! ", e);
			}
		}
		else {
			_log.warn("Cannot decode dock layout file["+file+"]. Using core layout.");
			doUnorganisedLayout();
		}
	}

	private void doUnorganisedLayout() {

		dockModel = new FloatDockModel();
		dockModel.addOwner("RavenClawWindow", frame);
		DockingManager.setDockModel(dockModel);

		rootDock = new SplitDock();
		rootDock.setAutoscrolls(true);

		dockModel.addRootDock("Main", rootDock, frame);

		for (RavenClawWindow i : windows.values()) {
			addDockable(i);
		}
		
		frame.getContentPane().add(rootDock, BorderLayout.CENTER);
	}

	public void addDockable(RavenClawWindow win) {
		
		if(rootDock == null)
			return;

		addDockable(win, null, null);
	}
	
	/**
	 * if point is null it will try to find a posible position, if it cant it will be set as floating
	 */
	public void addDockable(RavenClawWindow win, Point point, Point off) {
		
		if(off == null)
			off = new Point();
		
		boolean rz = false;
		if(point == null) {
			top:
			for (int i = 0; i < claw.getFrame().getWidth(); i++) {
				for (int j = 0; j < claw.getFrame().getHeight(); j++) {
					 rz = rootDock.addDockable(win, new Point(i, j), off);

					if(rz)
						break top;
				}
			}

			if(!rz) {
				dockModel.getFloatDock(frame).addDockable(win, frame.getLocation(), new Point());
			}
		}
		else {
			rz = rootDock.addDockable(win, point, off);
			
			if(!rz) {
				dockModel.getFloatDock(frame).addDockable(win, frame.getLocation(), new Point());
			}
		}
	}
	
	public void save() {
		File file = new File("./assets/sdk/", "lastSessions.dck");
		
		save(file);
	}

	public void save(File file) {
		try {
			DockModelPropertiesEncoder encoder = new DockModelPropertiesEncoder();
			encoder.export(dockModel, file.getPath());
			_log.info("Saved "+file);
		}
		catch (Exception e) {
			_log.warn("Error while saving the dock model.", e);
		}
	}
	
	/**
	 * @return the dockModel
	 */
	public FloatDockModel getDockModel() {
		return dockModel;
	}
	
	/**
	 * @return the rootDock
	 */
	public SplitDock getRootDock() {
		return rootDock;
	}
	
	public static abstract class RavenClawWindow extends DefaultDockable {
		
		@Inject
		protected WindowService windowService;

		private boolean isInitiate;

		public RavenClawWindow(String id, String title) {
			super(id, title);
		}

		/* (non-Javadoc)
		 * @see com.javadocking.dockable.DefaultDockable#getContent()
		 */
		@Override
		public final Component getContent() {
			return getComponent();
		}
		
		public abstract Component getComponent();
		
		@SuppressWarnings("unchecked")
		public final <T extends Component> T getDockedComponent() {
			return (T) getContent();
		}
		
		@SuppressWarnings("unchecked")
		public final <T extends Component> T getDockedComponent(Class<T> type) {
			return (T) getContent();
		}
		
		public final void start() {
			start(true);
		}
		
		public final void start(boolean addToDock) {
			Corax.pDep(this);
			onStart();
			isInitiate = true;

			if(addToDock)
				windowService.addDockable(this);
		}
		
		public final void close() {
			onClose();

//			DefaultDockableStateAction state = new DefaultDockableStateAction(this, DockableState.CLOSED);
//			state.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Close"));
//			closed = true;
		}

		public final void restart() {
			onRestart();
			isInitiate = false;
			start(false);
		}
		
		/**
		 * @return the isInitiate
		 */
		public boolean isInitiate() {
			return isInitiate;
		}
		
		public abstract void onStart();
		public abstract void onRestart();
		public abstract void onClose();
	}

	public void restartAll() {
		for (RavenClawWindow win : windows.values()) {
			if(!(win instanceof SceneWindow))
				win.restart();
		}
	}

}
