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

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import javolution.util.FastMap;

/**
 * @author Vlad
 */
@SuppressWarnings("serial")
public class ContentPanel extends JSplitPane {

	private CenterPanel center = new CenterPanel();
	private RightPanel right = new RightPanel();
	private BottomPanel bottom = new BottomPanel();

	private static final FastMap<String, Component> registeredComponents = new FastMap<>();
	
	public ContentPanel() {
		super(HORIZONTAL_SPLIT);
		add(center, LEFT);
		add(right, RIGHT);

		center.add(bottom, BOTTOM);
	}

	public void load(Canvas canvas) {
		center.canvas = canvas;
		
		canvas.setName("Render Canvas");

		registerComponent(canvas.getName(), canvas);
		
		center.add(canvas, TOP);
	}
	
	public static void registerComponent(Class<?> type, Component comp) {
		registerComponent(type.getSimpleName(), comp);
	}
	
	public static void registerComponent(String name, Component comp) {

		if(comp.getName() == null)
			comp.setName(name);
		
		Component old = registeredComponents.put(name, comp);
		
		if(old != null) {
			// Do smth?
		}
	}

	/**
	 * This works only for components with the name of it's class name
	 */
	public static <T extends Component> T getRegisteredComponent(Class<T> type) {
		return getRegisteredComponent(type.getSimpleName());
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Component> T getRegisteredComponent(String name) {
		return (T) registeredComponents.get(name);
	}

	/**
	 * @return the center
	 */
	public CenterPanel getCenter() {
		return center;
	}
	
	/**
	 * @return the bottom
	 */
	public BottomPanel getBottom() {
		return bottom;
	}

	public class CenterPanel extends JSplitPane {
		public Canvas canvas;

		public CenterPanel() {
			super(VERTICAL_SPLIT);
		}
		
	}

	public class RightPanel extends JPanel {
		{{ setPreferredSize(new Dimension(100, 100)); }}
		{{ setMaximumSize(new Dimension(200, 200)); }}
	}

	public class BottomPanel extends JSplitPane {
		{{ setPreferredSize(new Dimension(100, 100)); }}
		{{ setMaximumSize(new Dimension(200, 200)); }}
	}

	public void parseRootNode() {
		SceneNavigator naviagtor = getRegisteredComponent(SceneNavigator.class);
		
		if(naviagtor == null) {
			SceneNavigator navi = new SceneNavigator();
			bottom.add(navi, JSplitPane.LEFT);

			registerComponent(navi.getClass(), navi);
		}
		else
			naviagtor.reload();
	}
}
