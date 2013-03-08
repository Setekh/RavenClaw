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
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.managers.SelectionManager;
import com.ravenclaw.managers.TransformManager;

import corvus.corax.processing.annotation.Inject;

/**
 * @author scorn
 * @author mifth
 * 
 * This class is a prototype, using mifth's code.
 */
public final class RotateTool extends TransformTool {

	@Inject
	private SelectionManager selectionManager;
	
	@Inject
	private TransformManager transformManager;
	
	@Inject
	private SceneGraph app;
	
	private EditorTransformConstraint constraintTool;
	private Spatial collisionPlane;

	public RotateTool(Node toolShape) {
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
        // Set PickedAxis
        String type = rz.getGeometry().getName();
        if (type.indexOf("rot_x") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.X);
        } else if (type.indexOf("rot_y") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.Y);
        } else if (type.indexOf("rot_z") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.Z);
        } 
        
        // set the collision Plane location and rotation
        collisionPlane.setLocalTranslation(transformManager.getSelectionTransformCenter().getTranslation().clone());
        collisionPlane.getLocalRotation().lookAt(app.getCamera().getDirection(), Vector3f.UNIT_Y); //equals to angleZ
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.transform.TransformTool#update(float)
	 */
	@Override
	public void update(float tpf) {
        // cursor position and selected position vectors
        Vector2f cursorPos = new Vector2f(app.getInputManager().getCursorPosition());
        Vector3f vectorScreenSelected = app.getCamera().getScreenCoordinates(selectionManager.getSelectionCenter().getTranslation());
        Vector2f selectedCoords = new Vector2f(vectorScreenSelected.getX(), vectorScreenSelected.getY());

        //set new deltaVector if it's not set
        if (transformManager.getDeltaMoveVector() == null) {
            Vector2f deltaVecPos = new Vector2f(cursorPos.getX(), cursorPos.getY());
            Vector2f vecDelta = selectedCoords.subtract(deltaVecPos);
            transformManager.setDeltaMoveVector(new Vector3f(vecDelta.getX(), vecDelta.getY(), 0));
        }



        Node trNode = transformManager.getTranformParentNode();

        // Picked vector
        TransformManager.PickedAxis pickedAxis = transformManager.getPickedAxis();
        Vector3f pickedVec = Vector3f.UNIT_X;
        if (pickedAxis == TransformManager.PickedAxis.Y) {
            pickedVec = Vector3f.UNIT_Y;
        } else if (pickedAxis == TransformManager.PickedAxis.Z) {
            pickedVec = Vector3f.UNIT_Z;
        }


        // rotate according to angle
        Vector2f vec1 = selectedCoords.subtract(cursorPos).normalizeLocal();
        float angle = vec1.angleBetween(new Vector2f(transformManager.getDeltaMoveVector().getX(), transformManager.getDeltaMoveVector().getY()));
        angle = constraintTool.constraintValue(FastMath.RAD_TO_DEG * angle) * FastMath.DEG_TO_RAD;
        Quaternion rotationOfSelection = transformManager.getSelectionTransformCenter().getRotation();
        
        Vector3f axisToRotate = rotationOfSelection.mult(pickedVec);
        float angleCheck = axisToRotate.angleBetween(app.getCamera().getDirection());
        if (angleCheck > FastMath.HALF_PI) angle = -angle;
        
        Quaternion rot = rotationOfSelection.mult(rotationOfSelection.clone().fromAngleAxis(angle, pickedVec));
        
//            Quaternion newRotation = rotationOfSelection.mult(new Quaternion().fromAngleAxis(-angle, axisToRotate));
        trNode.setLocalRotation(rot);
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.transform.TransformTool#flush()
	 */
	@Override
	public void flush() {
	}
}
