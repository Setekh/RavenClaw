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
package com.ravenclaw.swing.windows;

import java.awt.Canvas;
import java.awt.Component;

import org.apache.log4j.Logger;

import com.ravenclaw.swing.WindowService.RavenClawWindow;

/**
 * @author scorn
 */
public class SceneWindow extends RavenClawWindow {

	private static final Logger _log = Logger.getLogger(SceneWindow.class);

//	private JPanel panel = new JPanel(new BorderLayout());
	
	private Canvas canvas;
	
	public SceneWindow(Canvas canvas) {
		super("Scene", "Scene");
		this.canvas = canvas;
	}
	
	/**
	 * @param canvas the canvas to set
	 */
	public void setCanvas(Canvas canvas) {
//		if(this.canvas != null)
//			panel.remove(this.canvas);
		
		this.canvas = canvas;
		//panel.add(canvas, BorderLayout.CENTER);
	}
	
	/* (non-Javadoc)
	 * @see com.ravenclaw.swing.WindowService.RavenClawWindow#getComponent()
	 */
	@Override
	public Component getComponent() {
		return canvas;//panel;
	}
	
	/* (non-Javadoc)
	 * @see com.ravenclaw.swing.WindowService.RavenClawWindow#onStart()
	 */
	@Override
	public void onStart() {
		_log.info("Started Scene Window!");
		//_log.info("Traced: ", new RuntimeException());
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.swing.WindowService.RavenClawWindow#onRestart()
	 */
	@Override
	public void onRestart() {
		
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.swing.WindowService.RavenClawWindow#onStop()
	 */
	@Override
	public void onClose() {
		_log.info("Closed Scene Window!");
	}

}
