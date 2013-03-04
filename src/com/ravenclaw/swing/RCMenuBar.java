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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.ravenclaw.swing.misc.MenuBarButtonListener;

/**
 * @author Vlad
 */
public final class RCMenuBar extends JMenuBar {

	private static final long serialVersionUID = 7505987949704493445L;

	private final MenuBarButtonListener actionListener = new MenuBarButtonListener();
	
	public RCMenuBar() {
		JMenu f_menu = new JMenu("File");
		
		JMenuItem fm_options = new JMenuItem("Options");
		fm_options.setActionCommand("options");
		fm_options.addActionListener(actionListener);
		f_menu.add(fm_options);

		f_menu.add(new JSeparator());
		
		JMenuItem fm_exit = new JMenuItem("Exit");
		fm_exit.setActionCommand("exit");
		fm_exit.addActionListener(actionListener);
		f_menu.add(fm_exit);

		add(f_menu);

		JMenu vp_menu = new JMenu("View Port");
		
		JMenuItem vp_r_restart = new JMenuItem("Restart");
		vp_r_restart.setActionCommand("restartVP");
		vp_r_restart.addActionListener(actionListener);
		vp_menu.add(vp_r_restart);

		JMenuItem vp_explorer = new JMenuItem("Explorer");
		vp_explorer.setActionCommand("reloadExp");
		vp_explorer.addActionListener(actionListener);
		vp_menu.add(vp_explorer);
		
		vp_menu.add(new JSeparator());
		
		JMenu vp_camera = new JMenu("Camera");

		final JMenuItem vp_ca_onoff = new JMenuItem("FlyCam on/off");
		vp_ca_onoff.setActionCommand("flyCamOnOff");
		vp_ca_onoff.addActionListener(actionListener);
		vp_camera.add(vp_ca_onoff);
		
		final JMenuItem vp_ca_dragToRotate = new JMenuItem("FlyCam DTRotate");
		vp_ca_dragToRotate.setActionCommand("flyCamDRotate");
		vp_ca_dragToRotate.addActionListener(actionListener);

		vp_camera.add(vp_ca_dragToRotate);
		vp_menu.add(vp_camera);

		add(vp_menu);
		
		JMenu a_menu = new JMenu("Assets");
		
		JMenuItem a_test = new JMenuItem("Test Cube");
		a_test.setActionCommand("addTestCube");
		a_test.addActionListener(actionListener);
		a_menu.add(a_test);

		add(a_menu);

	}
}

