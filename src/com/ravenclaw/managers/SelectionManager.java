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

import java.util.concurrent.Callable;

import javolution.util.FastList;

import org.apache.log4j.Logger;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.managers.ActionManager.UseAction;
import com.ravenclaw.managers.ObjectManager.ObjectData;
import com.ravenclaw.utils.ArchidIndex;

import corvus.corax.Corax;
import corvus.corax.event.EventMonitor;
import corvus.corax.event.listeners.CoraxListener;
import corvus.corax.processing.annotation.Inject;
import corvus.corax.tools.MelloriObjectBuffer;

/**
 * @author Vlad
 */
public final class SelectionManager implements CoraxListener {

	private static final Logger _log = Logger.getLogger(SelectionManager.class);
	
	private final FastList<Selection> selected = new FastList<Selection>().shared();

	@Inject
	private RavenClaw claw;
	
	@Inject
	private ActionManager actionManager;
	
	@Inject
	private ObjectManager objectManager;
	
    private Transform selectionCenter;

    public void calculateSelectionCenter() {
        if (selected.isEmpty()) {
            selectionCenter = null;
        }
        else if (selected.size() == 1) {
        	ObjectData objData = selected.get(0).objData;
            Spatial nd = objData.getMaster();
            selectionCenter = nd.getWorldTransform().clone();
        }
        else if (selected.size() > 1) {

            if (selectionCenter == null) {
                selectionCenter = new Transform();
            }

            // FIND CENTROID OF center POSITION
            Vector3f centerPosition = new Vector3f();
            for (Selection ID : selected) {
//                // POSITION
                Spatial ndPos = ID.objData.getMaster();
                centerPosition.addLocal(ndPos.getWorldTranslation());
            }
            
            centerPosition.divideLocal(selected.size());
            selectionCenter.setTranslation(centerPosition);

            // Rotation of the last selected is Local Rotation (like in Blender)
            Quaternion rot = selected.get(selected.size() - 1).getObjData().getMaster().getLocalRotation();
            selectionCenter.setRotation(rot); //Local coordinates of the last object            
        }
    }

	/**
	 * @return the selectionCenter
	 */
	public Transform getSelectionCenter() {
		return selectionCenter;
	}
	
	/**
	 * @return the selected
	 */
	public FastList<Selection> getSelected() {
		return selected;
	}
	
	public void select(Spatial target, boolean isShiftPressed) {
	
		Selection select = getBySpatial(target);
		
		if (select != null) {
			
			if(isShiftPressed) {
				unselect(select);
			}
			else {
				selected.clear();
				selected.add(select);
			}
		}
		else {
			select = new Selection(target);

			if(isShiftPressed) {
				selected.add(select);
			}
			else {
				selected.clear();
				selected.add(select);
			}
		}

		Corax.listen(ArchidIndex.Selected, null, select);
		calculateSelectionCenter();
		Corax.getInstance(TransformManager.class).updateCursor();
	}
	
	public void unselect(Spatial spatial) {
		Selection select = getBySpatial(spatial);
		
		if(select != null) {
			unselect(select);
		}
		else
			_log.warn("Trying to select smth that dose not exist.", new RuntimeException());
	}
	
	public void unselectAll() {
		for (int i = 0; i < selected.size(); i++) {
			unselect(selected.get(i));
		}
	}
	
	private void unselect(Selection select) {
		
		if(select == null)
			return;
		
		selected.remove(select);
		System.out.println("Unselected "+select);

		calculateSelectionCenter();
		Corax.getInstance(TransformManager.class).updateCursor();

		Corax.listen(ArchidIndex.Unselected, null, select);
	}

	public void delete() {
		claw.getApplication().enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				
				for (Selection sel : selected) {
					sel.getTarget().removeFromParent();
					objectManager.delete(sel.objData);
				}

				selected.clear();
				
				calculateSelectionCenter();
				Corax.getInstance(TransformManager.class).updateCursor();

				Corax.listen(ArchidIndex.DeleteSelected, null);
				return null;
			}
		});
	}
	
	public class Selection {
		private final Spatial target;
		protected final ObjectData objData;
		
		public Selection(Spatial target) {
			this.target = target;
			
			objData = objectManager.get(target);
			
			if(objData == null)
				_log.warn("Trying to select something not registerd with ObjectManager");
		}
		
		public Spatial getTarget() {
			return target;
		}
		
		/**
		 * @return the objData
		 */
		public ObjectData getObjData() {
			return objData;
		}
	}
	
	public class ActionSelect implements UseAction {

		private final Selection select;
		private final Callable<Void> callable;
		
		private boolean isUndone;
		
		public ActionSelect(Selection select, Callable<Void> callable) {
			this.select = select;
			this.callable = callable;
		}
		
		@Override
		public void action() {
			RavenClaw cw = Corax.getInstance(RavenClaw.class);
			cw.getApplication().enqueue(callable);
		}

		@Override
		public void undoAction() {
			SelectionManager cw = Corax.getInstance(SelectionManager.class);
			cw.unselect(select);
			
			isUndone = true;
		}

		@Override
		public boolean isUndone() {
			return isUndone;
		}

		@Override
		public String getName() {
			return "Selection: "+select.getTarget().getName();
		}
		
	}

	public Selection getBySpatial(Spatial target) {
		
		for (int i = 0; i < selected.size(); i++) {
			Selection sel = selected.get(i);
			
			if(sel != null) { // Not sure if fastlist reorder.
				if(sel.getObjData().getTarget() == target || sel.getObjData().getHolds().contains(target))
					return sel;
			}
		}
		
		return null;
	}
	
	/**
	 * SelectionManager
	 */
	public int count() {
		return selected.size();
	}

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxAbstractListener#enabled()
	 */
	@Override
	public void enabled() {
	}

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxAbstractListener#disabled()
	 */
	@Override
	public void disabled() {
	}

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxListener#onEvent(int, corvus.corax.tools.MelloriObjectBuffer)
	 */
	@Override
	public Object onEvent(int key, MelloriObjectBuffer buff) {
		switch (key) {
			case ArchidIndex.DeleteSelected:
				//.getActionListeners()[0].actionPerformed(null)
				break;
			default:
				break;
		}
		
		return EventMonitor.Nothing;
	}

}
