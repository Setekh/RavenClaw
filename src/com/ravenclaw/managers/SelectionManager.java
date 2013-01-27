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
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.jme3.scene.Spatial;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.managers.ActionManager.UseAction;

import corvus.corax.Corax;
import corvus.corax.processing.annotation.Inject;

/**
 * @author Vlad
 */
public final class SelectionManager {

	private static final Logger _log = Logger.getLogger(SelectionManager.class);
	
	private ArrayList<Selection> selected = new ArrayList<>(20);

	@Inject
	private ActionManager actionManager;
	
	public void select(Spatial target) {
		final Selection select = new Selection(target);
		
		selected.add(select);
		
		actionManager.record(new ActionSelect(select, new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				System.out.println("Selected: "+select.getTarget());
				return null;
			}
		}));
	}
	
	public void unselect(Spatial spatial) {
		
	}
	
	public void unselect(Selection select) {
		
	}
	
	public class Selection {
		private final Spatial target;
		
		public Selection(Spatial target) {
			this.target = target;
		}
		
		public Spatial getTarget() {
			return target;
		}
	}
	
	public static class ActionSelect implements UseAction {

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
			cw.getAppplication().enqueue(callable);
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
}
