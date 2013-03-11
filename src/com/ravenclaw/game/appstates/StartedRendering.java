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
package com.ravenclaw.game.appstates;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.managers.InputStateManager;
import com.ravenclaw.managers.ObjectManager;
import com.ravenclaw.swing.ContentPanel;
import com.ravenclaw.utils.ArchidIndex;
import com.ravenclaw.utils.FastGeoms;

import corvus.corax.Corax;
import corvus.corax.CorvusConfig;

/**
 * @author Seth
 */
public class StartedRendering extends AbstractAppState {

	private SceneGraph app;

	private float con;

	/* (non-Javadoc)
	 * @see com.jme3.app.state.AbstractAppState#initialize(com.jme3.app.state.AppStateManager, com.jme3.app.Application)
	 */
	@Override
	public void initialize(AppStateManager stateManager, Application ap) {
		
		this.app = (SceneGraph) ap;

		// Notify
		Corax.listen(ArchidIndex.RenderingStarted, null, app);

		// Only the annotations are needed
		Corax.instance().processDependancy(new FastGeoms());

		// SO it started
		RavenClaw cw = Corax.getInstance(RavenClaw.class);

		Node main = cw.getMainNode(), rootNode =  app.getRootNode();

		if(!main.removeFromParent()) { // Means startup
			CorvusConfig.addProperty(ArchidIndex.StartupTimeStamp, System.currentTimeMillis());
			System.out.println("Startin: TimeStamp = "+CorvusConfig.getProperty(ArchidIndex.StartupTimeStamp, -1));

			for (int i = 0; i < 2; i++) {
				Spatial spat = FastGeoms.genBoxGeometry(false);
				
				ObjectManager manager = Corax.getInstance(ObjectManager.class);
				manager.register(spat);
				main.attachChild(spat);
				
				spat.setLocalTranslation(0, i * 4, 0);
			}
		}
		
		rootNode.attachChild(FastGeoms.createGrid());
		rootNode.attachChild(cw.getMainNode());

		if(!cw.getFrame().isVisible())
			cw.getFrame().setVisible(true);

		if(!app.getContext().getSettings().isFullscreen()) {
			app.getInputManager().setCursorVisible(true);
			FlyCamAppState state = app.getStateManager().getState(FlyCamAppState.class);
			FlyByCamera cam = state.getCamera();

			app.getInputManager().deleteMapping("FLYCAM_Lower");

			app.getInputManager().deleteMapping("FLYCAM_ZoomIn");
			app.getInputManager().deleteMapping("FLYCAM_ZoomOut");
			app.getInputManager().deleteMapping("FLYCAM_RotateDrag");
			app.getInputManager().addMapping("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
			app.getInputManager().addListener(cam, "FLYCAM_RotateDrag");
			

			cam.setDragToRotate(true);
			cam.setMoveSpeed(cam.getMoveSpeed() * 5.7f);
			//cam.setEnabled(false);
		}

		app.getCamera().setLocation(new Vector3f(8.80987f, 6.771959f, 15.176244f));
		app.getCamera().lookAtDirection(Vector3f.ZERO.subtract(app.getCamera().getLocation()).normalizeLocal(), app.getCamera().getUp());
		
		// Init the tools
		Corax.getInstance(InputStateManager.class);
	}
	
	/* (non-Javadoc)
	 * @see com.jme3.app.state.AbstractAppState#update(float)
	 */
	@Override
	public void update(float tpf) {

		if(con > 1.0f) {
			
			if(!RavenClaw.checkFocus()) {
				System.out.println("Out of Focus");
				app.getInputManager().reset();
			}
			
			con = 0;
		}
		
		con += tpf;
	}
	
}
