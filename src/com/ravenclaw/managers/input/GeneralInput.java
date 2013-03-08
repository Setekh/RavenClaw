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
package com.ravenclaw.managers.input;

import java.text.DecimalFormat;

import javax.swing.SwingUtilities;

import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Spatial;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.managers.ActionManager;
import com.ravenclaw.managers.SelectionManager;
import com.ravenclaw.managers.TransformManager;
import com.ravenclaw.managers.transform.TransformTool;
import com.ravenclaw.swing.misc.S_ConfirmDeletion;
import com.ravenclaw.utils.Utils;

import corvus.corax.processing.annotation.Inject;

/**
 * @author Vlad
 */
public final class GeneralInput extends RavenClawInput implements ActionListener, AnalogListener {

	@Inject
	private SelectionManager selectionManager;
	
	@Inject
	private ActionManager actionManager;
	
	@Inject
	private TransformManager transformManager;
	
	private boolean isCtrlPressed, isShiftPressed;
	
	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.input.RavenClawInput#registerInputImpl()
	 */
	@Override
	protected void registerInputImpl() {
		addInput("click", this, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		addInput("delete", this, new KeyTrigger(KeyInput.KEY_DELETE));
		
		addInput("isCtrlPressed", this, new KeyTrigger(KeyInput.KEY_LCONTROL));
		addInput("isShiftPressed", this, new KeyTrigger(KeyInput.KEY_LSHIFT));

		addInput("zKey", this, new KeyTrigger(KeyInput.KEY_Z));
		
		addInput("moveX+", this, new MouseAxisTrigger(MouseInput.AXIS_X, false));
		addInput("moveX-", this, new MouseAxisTrigger(MouseInput.AXIS_X, true));
		addInput("moveY+", this, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		addInput("moveY-", this, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.controls.ActionListener#onAction(java.lang.String, boolean, float)
	 */
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		
		if(!RavenClaw.checkFocus()) {
			return; // Skipping
		}

		if(name.equals("isCtrlPressed")) {
			isCtrlPressed = isPressed;
		}
		else if(name.equals("isShiftPressed")) {
			isShiftPressed = isPressed;
		}

		if(isPressed) {
			switch (name) {
				case "zKey": {
					if(isCtrlPressed) {
						actionManager.undoLast();
						System.out.println("Undone last.");
					}
					break;
				}
				case "click": {
					TransformTool tool = transformManager.getCurrentTool();
					if(tool != null) {
						CollisionResults rz = Utils.pick(tool.getToolShape());
						if(rz.size() > 0) {
							// W/e action xD
							transformManager.actionPerformed(rz.getClosestCollision());
							return;
						}
					}
					
					CollisionResults rz = Utils.pick(claw.getMainNode());
					if(rz.size() > 0) {
						Spatial target = rz.getClosestCollision().getGeometry();
						
						if(target != null)
							selectionManager.select(target, isShiftPressed);
					}
					break;
				}
				case "delete": {
					SwingUtilities.invokeLater(new S_ConfirmDeletion());
					break;
				}
				default:
					//System.out.println("Unhandled: "+name);
					break;
			}
		}
		else {
			switch (name) {
				case "click":
					transformManager.deactivate();
					break;
				default:
					break;
			}
		}
	}
	
	DecimalFormat frm = new DecimalFormat("#.###");
	/* (non-Javadoc)
	 * @see com.jme3.input.controls.AnalogListener#onAnalog(java.lang.String, float, float)
	 */
	@Override
	public void onAnalog(String name, float value, float tpf) {

		switch (name) {
			case "click": case "delete":
			break;
			default:
				//System.out.println("Unhandled: "+name+" value = "+frm.format(value) + " X["+Mouse.getX()+"]Y["+Mouse.getY()+"]");
				break;
		}
	}
}
