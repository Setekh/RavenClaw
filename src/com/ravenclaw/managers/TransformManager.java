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
package com.ravenclaw.managers;

import java.util.List;

import com.jme3.app.state.AbstractAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.managers.SelectionManager.Selection;
import com.ravenclaw.managers.transform.EditorTransformConstraint;
import com.ravenclaw.managers.transform.MoveTool;
import com.ravenclaw.managers.transform.RotateTool;
import com.ravenclaw.managers.transform.ScaleTool;
import com.ravenclaw.managers.transform.TransformTool;
import com.ravenclaw.utils.FastGeoms;

import corvus.corax.Corax;
import corvus.corax.processing.annotation.Initiate;
import corvus.corax.processing.annotation.Inject;

/**
 * @author Vlad
 * @author mifth
 * 
 * This class is a prototype, using mifth's code.
 */
public final class TransformManager extends AbstractAppState {

	//private static final Logger _log = Logger.getLogger(TransformManager.class);

	@Inject 
	private RavenClaw claw;
	
	@Inject
	private SceneGraph app;
	
	@Inject
	private SelectionManager selectionManager;
	
	private TransformTool moveTool, rotateTool, scaleTool, currentTool;
	private Node collisionPlane;
	private final EditorTransformConstraint constraint = new EditorTransformConstraint();
	
    private Transform selectionTransformCenter;
    private TransformCoordinates trCoordinates;

    private Node tranformParentNode;
    private Node ndParent1;
    private Node ndParent2;
	private Vector3f deltaMoveVector;

    private PickedAxis pickedAxis;

    public TransformType currentAction = TransformType.Translate;

	public enum TransformType {
		None,
		Rotate,
		Translate,
		Scale;
	}
	
    public enum TransformCoordinates {
        WorldCoords, LocalCoords, ViewCoords
    };

    public enum PickedAxis {
        X, Y, Z, XY, XZ, YZ, View, scaleAll, None
    };

	@Initiate
	public void load() {
		Node[] nods = FastGeoms.createManipulators();
		moveTool = new MoveTool(nods[0]);
		rotateTool = new RotateTool(nods[1]);
		scaleTool = new ScaleTool(nods[2]);

		setTransformToolScale(0.2f);
		
		app.getStateManager().attach(this);

        ndParent1 = new Node();
        ndParent2 = new Node();

        tranformParentNode = new Node("tranformParentNode");
        app.getRootNode().attachChild(tranformParentNode);
        ndParent1.attachChild(ndParent2); // this is for rotation compensation

        pickedAxis = PickedAxis.None;
        trCoordinates = TransformCoordinates.WorldCoords;

        float size = 3000;
        Geometry g = new Geometry("plane", new Quad(size, size));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setWireframe(true);
        g.setMaterial(mat);
        g.setLocalTranslation(-size / 2, -size / 2, 0);
        collisionPlane = new Node();
        collisionPlane.attachChild(g);
        //app.getRootNode().attachChild(collisionPlane);
	}
	
	/**
	 * TransformManager
	 */
	public void setTransformToolScale(float f) {
		moveTool.setLocalScale(new Vector3f(f, f, f));
		rotateTool.setLocalScale(new Vector3f(f, f, f));
		scaleTool.setLocalScale(new Vector3f(f, f, f));
	}

	/* (non-Javadoc)
	 * @see com.jme3.app.state.AbstractAppState#update(float)
	 */
	@Override
	public void update(float tpf) {

		if(currentTool == null)
			return;
		
		if(currentTool.isActive()) {
			currentTool.update(tpf);
		}
		else if (selectionManager.getSelected().size() > 0 && selectionManager.getSelectionCenter() != null) {
			updateTransformCoords();
		
			Transform center = selectionManager.getSelectionCenter().clone();
	        Vector3f vec = center.getTranslation().subtract(app.getCamera().getLocation()).normalize().multLocal(app.getCamera().getFrustumNear() + 1.1f); // Temp
	
	        currentTool.setLocalTranslation(app.getCamera().getLocation().add(vec));
	        currentTool.setLocalRotation(selectionTransformCenter.getRotation());
		}
	}
	
    public void updateTransformCoords() {
        if (trCoordinates == TransformCoordinates.LocalCoords) {
            selectionTransformCenter = selectionManager.getSelectionCenter().clone();
        } else if (trCoordinates == TransformCoordinates.WorldCoords) {
            selectionTransformCenter = selectionManager.getSelectionCenter().clone();
            selectionTransformCenter.setRotation(new Quaternion());
        } else if (trCoordinates == TransformCoordinates.ViewCoords) {
            selectionTransformCenter = selectionManager.getSelectionCenter().clone();
            selectionTransformCenter.setRotation(app.getCamera().getRotation().mult(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y)));
        }
    }
	
    public void activate() {
    	if(currentTool != null) {
    		attachSelectedToTransformParent();
    	}
    }
    
    public void deactivate() {
    	
    	if(currentTool == null || !currentTool.isActive())
    		return;

    	currentTool.setActive(false);

    	detachSelectedFromTransformParent();
    	deltaMoveVector = null;
    	
    	if (selectionTransformCenter != null) {
        	currentTool.flush();
        	
            selectionManager.calculateSelectionCenter();

            
            if(selectionManager.getSelectionCenter() != null) {
            	selectionTransformCenter = selectionManager.getSelectionCenter().clone();
                updateTransformCoords();
            }
            else 
            	selectionTransformCenter = null;
            
            tranformParentNode.detachAllChildren();
        }
    }
    
    private void attachSelectedToTransformParent() {

        tranformParentNode.setLocalTransform(new Transform());  // clear previous transform
        tranformParentNode.setLocalTranslation(selectionTransformCenter.getTranslation().clone());
        tranformParentNode.setLocalRotation(selectionTransformCenter.getRotation().clone());

        // New node to compensate rotation of tranformParentNode
        ndParent1.setLocalRotation(tranformParentNode.getLocalRotation().clone());
        Quaternion rotNdParent2 = selectionTransformCenter.getRotation().clone();
        rotNdParent2.inverseLocal();
        ndParent2.setLocalRotation(rotNdParent2);

        Vector3f moveDeltaVec = new Vector3f().subtract(tranformParentNode.getLocalTranslation());
        List<Selection> selectedList = selectionManager.getSelected();
        
        for (Selection id : selectedList) {
            Spatial sp = id.getObjData().getMaster();

            ndParent2.attachChild(sp);
            sp.getLocalTranslation().addLocal(moveDeltaVec);
            ndParent1.setLocalRotation(new Quaternion());
            Transform tr = sp.getWorldTransform().clone();
            tranformParentNode.attachChild(sp);
            sp.getLocalTranslation().addLocal(moveDeltaVec);
            sp.setLocalTransform(tr);
            ndParent1.setLocalRotation(tranformParentNode.getLocalRotation().clone());
        }

        ndParent2.setLocalRotation(new Quaternion());
        ndParent1.setLocalRotation(new Quaternion());
    }

    private void detachSelectedFromTransformParent() {
        List<Selection> selectedList = selectionManager.getSelected();
        
        for (Selection id : selectedList) {
            Spatial sp = id.getObjData().getMaster();
            
            Transform tr = sp.getWorldTransform();
            claw.getMainNode().attachChild(sp);
            sp.setLocalTransform(tr);
        }
        
    }

	public void updateCursor() {

		switch (currentAction) {
			case Translate: {
				currentTool = moveTool;
				break;
			}
			case Rotate: {
				currentTool = rotateTool;
				break;
			}
			case Scale: {
				currentTool = scaleTool;
				break;
			}
		}

		// Process it
		if(currentTool != null) {
			if(!currentTool.isInitiated()) {
				Corax.pDep(currentTool);
			}
			
			boolean visibility = selectionManager.getSelectionCenter() != null && selectionManager.getSelected().size() > 0;
			//System.out.println("TransformManager.updateCursor("+selectionManager.getSelected().size()+") = "+(visibility ? "Show" : "Hide"));
			
			if(visibility)
				currentTool.show();
			else
				currentTool.hide();
			
		}
	}

	public void actionPerformed(CollisionResult closestCollision) {
		System.out.println("Clicked: "+closestCollision.getGeometry());
		
		if(currentTool != null) {
			currentTool.setCursorCollisionData(closestCollision);
			currentTool.setActive(true);
			
			activate();
		}
	}

	/**
	 * @return the moveTool
	 */
	public TransformTool getMoveTool() {
		return moveTool;
	}
	
	/**
	 * @return the rotateTool
	 */
	public TransformTool getRotateTool() {
		return rotateTool;
	}
	
	/**
	 * @return the scaleTool
	 */
	public TransformTool getScaleTool() {
		return scaleTool;
	}
	
    /**
	 * @return the selectionTransformCenter
	 */
	public Transform getSelectionTransformCenter() {
		return selectionTransformCenter;
	}

	/**
	 * @param currentAction the currentAction to set
	 */
	public void setCurrentAction(TransformType currentAction) {
		this.currentAction = currentAction;
		
		if(currentTool != null) {
			currentTool.hide();
		}
		
		updateCursor();
	}
	
	/**
	 * @return the pickedAxis
	 */
	public PickedAxis getPickedAxis() {
		return pickedAxis;
	}
	
	/**
	 * @param pickedAxis the pickedAxis to set
	 */
	public void setPickedAxis(PickedAxis pickedAxis) {
		this.pickedAxis = pickedAxis;
	}

	/**
	 * @return the currentAction
	 */
	public TransformType getCurrentAction() {
		return currentAction;
	}

	/**
	 * @return the currentTool
	 */
	public TransformTool getCurrentTool() {
		return currentTool;
	}
	
	/**
	 * @return the collisionPlane
	 */
	public Node getCollisionPlane() {
		return collisionPlane;
	}

	/**
	 * @return the constraint
	 */
	public EditorTransformConstraint getConstraint() {
		return constraint;
	}

	/**
	 * TransformManager
	 */
	public Node getTranformParentNode() {
		return tranformParentNode;
	}
	
	/**
	 * @param deltaMoveVector the deltaMoveVector to set
	 */
	public void setDeltaMoveVector(Vector3f deltaMoveVector) {
		this.deltaMoveVector = deltaMoveVector;
	}
	
	/**
	 * @return the deltaMoveVector
	 */
	public Vector3f getDeltaMoveVector() {
		return deltaMoveVector;
	}
}
