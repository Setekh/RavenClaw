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
package com.ravenclaw.game;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.ravenclaw.game.appstates.StartedRendering;
import com.ravenclaw.managers.ActionManager;
import com.ravenclaw.managers.InputStateManager;
import com.ravenclaw.managers.Inspector;
import com.ravenclaw.managers.ObjectManager;
import com.ravenclaw.managers.SelectionManager;
import com.ravenclaw.managers.TransformManager;

import corvus.corax.Corax;
import corvus.corax.processing.annotation.Finalize;
import corvus.corax.processing.annotation.Provide;
/**
 * @author Vlad
 */
public class SceneGraph extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		viewPort.setBackgroundColor(ColorRGBA.DarkGray);
		stateManager.attach(new StartedRendering());
	}

	/* (non-Javadoc)
	 * @see com.jme3.app.Application#getAssetManager()
	 */
	@Provide
	@Override
	public AssetManager getAssetManager() {
		return super.getAssetManager();
	}

	/* (non-Javadoc)
	 * @see com.jme3.app.Application#getCamera()
	 */
	@Provide
	@Override
	public Camera getCamera() {
		return super.getCamera();
	}
	
	/* (non-Javadoc)
	 * @see com.jme3.app.Application#getInputManager()
	 */
	@Provide
	@Override
	public InputManager getInputManager() {
		return super.getInputManager();
	}

	@Finalize
	public void clean() {
		System.out.println("Disposed");
		
		Corax corax = Corax.instance();
		
		// Needs to get cleaned for the new SceneGraph
		corax.disposeInstance(ActionManager.class);
		corax.disposeInstance(InputStateManager.class);
		corax.disposeInstance(Inspector.class);
		corax.disposeInstance(ObjectManager.class);
		corax.disposeInstance(SelectionManager.class);
		corax.disposeInstance(TransformManager.class);
	}
}
