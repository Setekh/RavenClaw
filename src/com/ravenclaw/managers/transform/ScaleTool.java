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
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.managers.TransformManager;

import corvus.corax.processing.annotation.Inject;

/**
 * @author scorn
 * @author mifth
 * 
 * This class is a prototype, using mifth's code.
 */
public final class ScaleTool extends TransformTool {

	@Inject
	private TransformManager transformManager;
	
	@Inject
	private SceneGraph app;
	
	private EditorTransformConstraint constraintTool;
	private Spatial collisionPlane;

	public ScaleTool(Node toolShape) {
		super(toolShape);
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.transform.TransformTool#initiate()
	 */
	@Override
	public void initiate() {
		collisionPlane = transformManager.getCollisionPlane();
		constraintTool = transformManager.getConstraint();
	}
	
	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.transform.TransformTool#setCursorCollisionData(com.jme3.collision.CollisionResult)
	 */
	@Override
	public void setCursorCollisionData(CollisionResult rz) {
        Transform selectedCenter = transformManager.getSelectionTransformCenter();

        // Set PickedAxis
        String type = rz.getGeometry().getName();
        if (type.indexOf("scale_x") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.X);
        } else if (type.indexOf("scale_y") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.Y);
        } else if (type.indexOf("scale_z") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.Z);
        } 

        // set the collision Plane location and rotation
        collisionPlane.setLocalTranslation(selectedCenter.getTranslation().clone());
        collisionPlane.getLocalRotation().lookAt(app.getCamera().getDirection(), Vector3f.UNIT_Y); //equals to angleZ
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.transform.TransformTool#update(float)
	 */
	@Override
	public void update(float tpf) {
        // cursor position and selected position vectors
        Vector2f cursorPos = new Vector2f(app.getInputManager().getCursorPosition());
        Vector3f vectorScreenSelected = app.getCamera().getScreenCoordinates(transformManager.getSelectionTransformCenter().getTranslation());
        Vector2f selectedCoords = new Vector2f(vectorScreenSelected.getX(), vectorScreenSelected.getY());

        //set new deltaVector if it's not set (scale tool stores position of a cursor)
        if (transformManager.getDeltaMoveVector() == null) {
            Vector2f deltaVecPos = new Vector2f(cursorPos.getX(), cursorPos.getY());
            transformManager.setDeltaMoveVector(new Vector3f(deltaVecPos.getX(), deltaVecPos.getY(), 0));
        }

        Node trNode = transformManager.getTranformParentNode();

        // Picked vector
        TransformManager.PickedAxis pickedAxis = transformManager.getPickedAxis();
        Vector3f pickedVec = Vector3f.UNIT_X;
        if (pickedAxis == TransformManager.PickedAxis.Y) {
            pickedVec = Vector3f.UNIT_Y;
        } else if (pickedAxis == TransformManager.PickedAxis.Z) {
            pickedVec = Vector3f.UNIT_Z;
        } else if (pickedAxis == TransformManager.PickedAxis.scaleAll) {
            pickedVec = new Vector3f(1,1,1);
        }

        // scale according to distance
        Vector2f delta2d = new Vector2f(transformManager.getDeltaMoveVector().getX(), transformManager.getDeltaMoveVector().getY());
        Vector3f baseScale = new Vector3f(1, 1, 1); // default scale

        // scale object
        float disCursor = cursorPos.distance(selectedCoords);
        float disDelta = delta2d.distance(selectedCoords);
        Vector3f scalevec = null;
        float scaleValue = cursorPos.distance(delta2d);
        scaleValue = constraintTool.constraintValue(scaleValue *0.007f);

        if (disCursor > disDelta) {
            scalevec = baseScale.add(pickedVec.mult(scaleValue));
        } else {
            scaleValue = Math.min(scaleValue, 0.999f); // remove negateve values
            scalevec = baseScale.subtract(pickedVec.mult((scaleValue)));
        }

        trNode.setLocalScale(scalevec);
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.transform.TransformTool#flush()
	 */
	@Override
	public void flush() {
	}
}
