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
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.managers.TransformManager;
import com.ravenclaw.managers.TransformManager.PickedAxis;

import corvus.corax.processing.annotation.Inject;

/**
 * @author scorn
 * @author mifth
 * 
 * This class is a prototype, using mifth's code.
 */
public final class MoveTool extends TransformTool {

	@Inject
	private TransformManager transformManager;
	
	@Inject
	private SceneGraph app;
	
	private EditorTransformConstraint constraintTool;
	private Spatial collisionPlane;

	public MoveTool(Node toolShape) {
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

        if(selectedCenter == null) {
        	transformManager.updateTransformCoords();
        	selectedCenter = transformManager.getSelectionTransformCenter();
        }
        
        // Set PickedAxis
        String type = rz.getGeometry().getName();
        if (type.indexOf("move_x") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.X);
        } else if (type.indexOf("move_y") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.Y);
        } else if (type.indexOf("move_z") >= 0) {
            transformManager.setPickedAxis(TransformManager.PickedAxis.Z);
        }
//        else if (type.indexOf("move_view") > 0) {
//            trManager.setPickedAxis(TransformManager.PickedAxis.View);
//        }
        PickedAxis pickedAxis = transformManager.getPickedAxis();

        // select an angle between 0 and 90 degrees (from 0 to 1.57 in radians) (for collisionPlane)
        float angleX = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_X));
        if (angleX > 1.57) {
            angleX = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_X).negateLocal());
        }

        float angleY = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_Y));
        if (angleY > 1.57) {
            angleY = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_Y).negateLocal());
        }

        float angleZ = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_Z));
        if (angleZ > 1.57) {
            angleZ = app.getCamera().getDirection().angleBetween(selectedCenter.getRotation().mult(Vector3f.UNIT_Z).negateLocal());
        }

        //select the less angle for collisionPlane
        float lessAngle = angleX;
        if (lessAngle > angleY) {
            lessAngle = angleY;
        }
        if (lessAngle > angleZ) {
            lessAngle = angleZ;
        }

        // set the collision Plane location and rotation
        collisionPlane.setLocalTranslation(selectedCenter.getTranslation().clone());
        collisionPlane.setLocalRotation(selectedCenter.getRotation().clone()); //equals to angleZ
        //Quaternion planeRot = collisionPlane.getLocalRotation();

        // rotate the plane for constraints
        if (lessAngle == angleX) {
//            System.out.println("XXXAngle");

            if (pickedAxis == PickedAxis.X && angleY > angleZ) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.X && angleY < angleZ) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Y) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
            } else if (pickedAxis == PickedAxis.Z) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            }
        } else if (lessAngle == angleY) {
            if (pickedAxis == PickedAxis.X) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Y && angleX < angleZ) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
            } else if (pickedAxis == PickedAxis.Z) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
            }
        } else if (lessAngle == angleZ) {
            if (pickedAxis == PickedAxis.X) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Z && angleY < angleX) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
            } else if (pickedAxis == PickedAxis.Z && angleY > angleX) {
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
                collisionPlane.getLocalRotation().multLocal(new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            }
        }
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.transform.TransformTool#update(float)
	 */
	@Override
	public void update(float tpf) {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray();
        Vector3f pos = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), 0f).clone();
        Vector3f dir = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(), .3f).clone();
        dir.subtractLocal(pos).normalizeLocal();
        ray.setOrigin(pos);
        ray.setDirection(dir);
        collisionPlane.collideWith(ray, results);
        CollisionResult result = results.getClosestCollision();

        // Complex trigonometry formula based on sin(angle)*distance
        if (results.size() > 0) {

            Vector3f contactPoint = result.getContactPoint(); // get a point of collisionPlane
            Transform selectedCenter = transformManager.getSelectionTransformCenter();

            //set new deltaVector if it's not set
            if (transformManager.getDeltaMoveVector() == null) {
            	transformManager.setDeltaMoveVector(selectedCenter.getTranslation().subtract(contactPoint));
            }

            contactPoint = contactPoint.add(transformManager.getDeltaMoveVector()); // add delta of the picked place

            Vector3f vec1 = contactPoint.subtract(selectedCenter.getTranslation());
            float distanceVec1 = selectedCenter.getTranslation().distance(contactPoint);

            // Picked vector
            PickedAxis pickedAxis = transformManager.getPickedAxis();
            Vector3f pickedVec = Vector3f.UNIT_X;
            if (pickedAxis == TransformManager.PickedAxis.Y) {
                pickedVec = Vector3f.UNIT_Y;
            } else if (pickedAxis == TransformManager.PickedAxis.Z) {
                pickedVec = Vector3f.UNIT_Z;
            }
            // the main formula for constraint axis
            float angle = vec1.clone().normalizeLocal().angleBetween(selectedCenter.getRotation().mult(pickedVec).normalizeLocal());
            float distanceVec2 = distanceVec1 * FastMath.sin(angle);

            // fix if angle>90 degrees
            Vector3f perendicularVec = collisionPlane.getLocalRotation().mult(Vector3f.UNIT_X).mult(distanceVec2);
            Vector3f checkVec = contactPoint.add(perendicularVec).subtractLocal(contactPoint).normalizeLocal();
            float angleCheck = checkVec.angleBetween(vec1.clone().normalizeLocal());
            if (angleCheck < FastMath.HALF_PI) {
                perendicularVec.negateLocal();
            }


            // find distance to mave
            float distanceToMove = contactPoint.add(perendicularVec).distance(selectedCenter.getTranslation());

            distanceToMove = constraintTool.constraintValue(distanceToMove);
            
            // invert value if it's needed for negative movement
            if (angle > FastMath.HALF_PI) {
                distanceToMove = -distanceToMove;
            }


            translateObjects(distanceToMove, pickedAxis, transformManager.getTranformParentNode(), selectedCenter);
//            System.out.println("Vec: " + selectedCenter.getTranslation().toString() + "   angle: " + angle);
            
            
        }
	}

    private void translateObjects(float distance, PickedAxis pickedAxis, Node tranformParentNode, Transform selectedCenter) {

        tranformParentNode.setLocalTranslation(selectedCenter.getTranslation().clone());

        if (pickedAxis == PickedAxis.X) {
            tranformParentNode.getLocalTranslation().addLocal(selectedCenter.getRotation().getRotationColumn(0).mult(distance));
        } else if (pickedAxis == PickedAxis.Y) {
            tranformParentNode.getLocalTranslation().addLocal(selectedCenter.getRotation().getRotationColumn(1).mult(distance));
        } else if (pickedAxis == PickedAxis.Z) {
            tranformParentNode.getLocalTranslation().addLocal(selectedCenter.getRotation().getRotationColumn(2).mult(distance));
        }

    }

	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.transform.TransformTool#flush()
	 */
	@Override
	public void flush() {
	}
}
