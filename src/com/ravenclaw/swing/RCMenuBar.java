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
package com.ravenclaw.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import com.jme3.input.FlyByCamera;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.swing.misc.S_RequestRestart;

import corvus.corax.Corax;

/**
 * @author Vlad
 */
public final class RCMenuBar extends JMenuBar {

	private static final long serialVersionUID = 7505987949704493445L;

	public RCMenuBar() {
		JMenu f_menu = new JMenu("File");
		
		JMenuItem fm_exit = new JMenuItem("Exit");
		f_menu.add(fm_exit);

		add(f_menu);

		JMenu vp_menu = new JMenu("View Port");
		
		JMenuItem vp_r_restart = new JMenuItem("Restart");
		vp_r_restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RavenClaw rc = Corax.getInstance(RavenClaw.class);
				SwingUtilities.invokeLater(new S_RequestRestart(rc));
			}
		});
		
		vp_menu.add(vp_r_restart);

		vp_menu.add(new JSeparator());
		
		JMenu vp_camera = new JMenu("Camera");

		final JMenuItem vp_ca_onoff = new JMenuItem("FlyCam on/off");
		vp_ca_onoff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RavenClaw rc = Corax.getInstance(RavenClaw.class);
				
				SceneGraph app = rc.getAppplication();
				FlyByCamera cam = app.getFlyByCamera();
				cam.setEnabled(!cam.isEnabled());
				
				if(cam.isEnabled())
					vp_ca_onoff.setForeground(Color.BLACK);
				else
					vp_ca_onoff.setForeground(Color.RED);
			}
		});
		vp_camera.add(vp_ca_onoff);
		final JMenuItem vp_ca_dragToRotate = new JMenuItem("FlyCam DTRotate");
		vp_ca_dragToRotate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RavenClaw rc = Corax.getInstance(RavenClaw.class);
				
				SceneGraph app = rc.getAppplication();
				FlyByCamera cam = app.getFlyByCamera();
				cam.setDragToRotate(!cam.isDragToRotate());
				
				if(cam.isEnabled())
					vp_ca_dragToRotate.setForeground(Color.BLACK);
				else
					vp_ca_dragToRotate.setForeground(Color.RED);
			}
		});
		vp_camera.add(vp_ca_dragToRotate);
		vp_menu.add(vp_camera);

		add(vp_menu);
	}
}
