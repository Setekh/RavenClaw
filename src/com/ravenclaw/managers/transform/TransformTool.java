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
package com.ravenclaw.managers.transform;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.utils.VoidCallable;

import corvus.corax.processing.annotation.Initiate;
import corvus.corax.processing.annotation.Inject;

/**
 * @author scorn
 */
public abstract class TransformTool {

	@Inject
	protected RavenClaw claw;
	
	protected final Node toolShape;
	private volatile boolean initiated, active;
	
	public TransformTool(Node toolShape) {
		this.toolShape = toolShape;
	}

	@Initiate
	protected void load() {
		initiate();
		initiated = true;
	}

	public void initiate() {
		// If ever needed
	}
	
	public abstract void setCursorCollisionData(CollisionResult rz);
	public abstract void update(float tpf);
	public abstract void flush();
	
	/**
	 * @return the toolShape
	 */
	public Node getToolShape() {
		return toolShape;
	}

	public void setLocalTranslation(Vector3f vec) {
		toolShape.setLocalTranslation(vec);
	}

	public void setLocalRotation(Quaternion rotation) {
		toolShape.setLocalRotation(rotation);
	}

	public Node getParent() {
		return toolShape.getParent();
	}

	public void setLocalTransform(Transform trans) {
		toolShape.setLocalTransform(trans);
	}

	public void setLocalScale(Vector3f vector3f) {
		toolShape.setLocalScale(vector3f);
	}
	
	/**
	 * @return the initiated
	 */
	public boolean isInitiated() {
		return initiated;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
		
		if(active)
			hide();
		else
			show();
	}

	public void hide() {
		if(!isHidden()) {
			claw.getApplication().enqueue(new VoidCallable() {
				@Override
				public Void call() throws Exception {
					toolShape.removeFromParent();
					return null;
				}
			});
		}
	}

	public void show() {

		if(isHidden()) {
			claw.getApplication().enqueue(new VoidCallable() {
				@Override
				public Void call() throws Exception {
					claw.getApplication().getRootNode().attachChild(toolShape);
					return null;
				}
			});
		}
	}
	
	public boolean isHidden() {
		return getParent() == null;
	}
}
