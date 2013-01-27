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

import java.util.ArrayList;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ravenclaw.utils.Utils;

/**
 * @author Vlad
 * Map: holds geometry and objectMaste
 * 
 * object master = new class, containing all nodes of the model/object
 * 
 * TODO: Needs to keep track of things that get deleted.
 */
public final class ObjectManager {
	private static final Logger _log = Logger.getLogger(ObjectManager.class);
	
	private final FastMap<Spatial, ObjectData> data = new FastMap<Spatial, ObjectData>().shared();

	protected void process(ObjectData od) {
		int size = od.holds.size();

		for (int i = 0; i < size; i++) {
			Spatial spat = od.holds.get(i);
			data.put(spat, od);
		}
	}
	
	public void register(Spatial spat) {
		new ObjectData(spat);
	}
	
	public ObjectData get(Spatial target) {
		ObjectData od = data.get(target);
		
		if(od != null) {
			od.checkUpdate();
		}
		
		return od;
	}
	
	public class ObjectData {
		
		private final ArrayList<Spatial> holds;
		
		private Spatial target;
		private Node rootNode; // If any
		
		public ObjectData(Node rootNode) {
			this.rootNode = rootNode;
			holds = Utils.parseSpatials(rootNode);

			process(this);
		}
		
		public ObjectData(Spatial spat) {
			if(spat instanceof Node) {
				this.rootNode = (Node) spat;
				holds = Utils.parseSpatials(rootNode);
			}
			else {
				holds = null;
				holds.add(target = spat);
			}

			process(this);
		}
		
		public void checkUpdate() {
			if((rootNode != null && holds != null) && rootNode.getChildren().size() != holds.size()) {
				update();
			}
		}

		public void update() {
			if(rootNode == null) {
				_log.warn("Tryed to update on a single spatial object.", new RuntimeException());
				return;
			}
			
			int size = holds.size();
			for (int i = 0; i < size; i++) {
				data.remove(holds.get(i));
			}

			holds.clear();
			
			ArrayList<Spatial> list = Utils.parseSpatials(rootNode);
			holds.addAll(list);
			list.clear();

			process(this);
		}
		
		/**
		 * @return the target
		 */
		public Spatial getTarget() {
			return target;
		}

		/**
		 * @return the rootNode
		 */
		public Node getRootNode() {
			return rootNode;
		}
		
		public ArrayList<Spatial> getHolds() {
			return holds;
		}
	}
	
}
