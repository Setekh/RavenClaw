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

import java.util.ArrayList;

import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.ravenclaw.game.appstates.StartedRendering;
import com.ravenclaw.managers.ActionManager;
import com.ravenclaw.managers.InputStateManager;
import com.ravenclaw.managers.Inspector;
import com.ravenclaw.managers.ObjectManager;
import com.ravenclaw.managers.SelectionManager;
import com.ravenclaw.managers.TransformManager;
import com.ravenclaw.utils.Utils;

import corvus.corax.Corax;
import corvus.corax.processing.annotation.Finalize;
import corvus.corax.processing.annotation.Provide;
/**
 * @author Vlad
 */
public class SceneGraph extends SimpleApplication {

    private Node gridNode;

	private void createGrid() {
        gridNode = new Node("GridNode");

        //Create a grid plane
        Geometry g = new Geometry("GRID", new Grid(201, 201, 10f));
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floor_mat.getAdditionalRenderState().setWireframe(true);
        floor_mat.setColor("Color", new ColorRGBA(0.4f, 0.4f, 0.4f, 0.15f));
        floor_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        g.setCullHint(Spatial.CullHint.Never);
        g.setShadowMode(RenderQueue.ShadowMode.Off);
        g.setQueueBucket(RenderQueue.Bucket.Transparent);
        g.setMaterial(floor_mat);
        g.center().move(new Vector3f(0f, 0f, 0f));
        gridNode.attachChild(g);

        // Red line for X axis
        final Line xAxis = new Line(new Vector3f(-1000f, 0f, 0f), new Vector3f(1000f, 0f, 0f));
        xAxis.setLineWidth(2f);
        Geometry gxAxis = new Geometry("XAxis", xAxis);
        gxAxis.setModelBound(new BoundingBox());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1.0f, 0.2f, 0.5f, 0.2f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        gxAxis.setCullHint(Spatial.CullHint.Never);
        gxAxis.setQueueBucket(RenderQueue.Bucket.Transparent);
        gxAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gxAxis.setMaterial(mat);
        gxAxis.setCullHint(Spatial.CullHint.Never);

        gridNode.attachChild(gxAxis);

        // Blue line for Z axis
        final Line zAxis = new Line(new Vector3f(0f, 0f, -1000f), new Vector3f(0f, 0f, 1000f));
        zAxis.setLineWidth(2f);
        Geometry gzAxis = new Geometry("ZAxis", zAxis);
        gzAxis.setModelBound(new BoundingBox());
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.2f, 1.0f, 0.2f, 0.2f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        gzAxis.setCullHint(Spatial.CullHint.Never);
        gzAxis.setQueueBucket(RenderQueue.Bucket.Transparent);
        gzAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gzAxis.setMaterial(mat);
        gzAxis.setCullHint(Spatial.CullHint.Never);
        gridNode.attachChild(gzAxis);

        rootNode.attachChild(gridNode);

    }

	@Override
	public void simpleInitApp() {
		viewPort.setBackgroundColor(ColorRGBA.DarkGray);
		stateManager.attach(new StartedRendering());
		createGrid();
		
		
//		Screen scr = new Screen(this);
//		guiNode.addControl(scr);
//		scr.initialize();
//
//		Window win = new Window(scr, "window1", new Vector2f(settings.getWidth() / 2, settings.getHeight() / 2));
//		win.attachChild(new ButtonAdapter(scr, "close", Vector2f.ZERO) {
//			{
//				setDimensions(new Vector2f(2, 2));
//			}
//		});
//		win.setDimensions(200, 200);
//		
//		scr.addElement(win);
//		
//		Panel panel = new Panel(scr, "0x01", new Vector2f(settings.getWidth() / 2, settings.getHeight() / 2));
//		panel.setDimensions(200, 200);
//		
//		scr.addElement(panel);
//
//		
//		scr.beginInput();
//		
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
		
		ArrayList<Spatial> spats = Utils.parseSpatials(rootNode, true);
		
		for (Spatial spatial : spats) {
			spatial.removeFromParent();
		}

		spats.clear();
		
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
