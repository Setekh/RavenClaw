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
package com.ravenclaw.swing.misc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

import com.jme3.input.FlyByCamera;
import com.jme3.scene.Spatial;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.managers.ObjectManager;
import com.ravenclaw.swing.ContentPanel;
import com.ravenclaw.swing.SceneNavigator;
import com.ravenclaw.utils.FastGeoms;

import corvus.corax.Corax;

/**
 * @author scorn
 */
public class MenuBarButtonListener implements ActionListener {

	private static final Logger _log = Logger.getLogger(MenuBarButtonListener.class);
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() instanceof JMenuItem) {
			JMenuItem item = (JMenuItem)e.getSource();
			
			switch (e.getActionCommand()) {
				
				// File Menu ==========================
				case "exit": {
					int ret = JOptionPane.showConfirmDialog(Corax.getInstance(RavenClaw.class).getFrame(),
							"Are you sure you want to exit?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);

					if(ret == 0) { // OK
						System.out.println("Good bye.");
						System.exit(0);
					}
					break;
				}
				case "options": {
					boolean dialog = getAndCreateOptionsPanel(new JDialog(Corax.getInstance(RavenClaw.class).getFrame()));
					System.out.println("Update Settings ? "+dialog);
					break;
				}
				
				// Viewport Menu ==========================
				case "restartVP":{
					RavenClaw rc = Corax.getInstance(RavenClaw.class);
					SwingUtilities.invokeLater(new S_RequestRestart(rc));
					break;
				}
				case "reloadExp":{
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							ContentPanel.getRegisteredComponent(SceneNavigator.class).reload();
						}
					});
					break;
				}
				case "flyCamOnOff":{ // - Camera menu
					RavenClaw rc = Corax.getInstance(RavenClaw.class);
					
					SceneGraph app = rc.getApplication();
					FlyByCamera cam = app.getFlyByCamera();
					cam.setEnabled(!cam.isEnabled());
					
					if(cam.isEnabled())
						item.setForeground(Color.BLACK);
					else
						item.setForeground(Color.RED);
					break;
				}
				case "flyCamDRotate":{
					RavenClaw rc = Corax.getInstance(RavenClaw.class);
					
					SceneGraph app = rc.getApplication();
					FlyByCamera cam = app.getFlyByCamera();
					cam.setDragToRotate(!cam.isDragToRotate());
					
					if(cam.isEnabled())
						item.setForeground(Color.BLACK);
					else
						item.setForeground(Color.RED);
					break;
				}

				// Assets
				case "addTestCube":{
					Spatial spat = FastGeoms.genBoxGeometry(false);
					RavenClaw rc = Corax.getInstance(RavenClaw.class);
					ObjectManager manager = Corax.getInstance(ObjectManager.class);
					manager.register(spat);
					
					rc.attachChild(spat);

					ContentPanel.getRegisteredComponent(SceneNavigator.class).reload();
					break;
				}
				default:
					_log.warn("Unhandled menu bar action "+e.getActionCommand()+ " by "+e.getSource());
					break;
				}
		}
		else if(e.getSource() instanceof JButton) {
			JButton item = (JButton)e.getSource();
		}
		
	}

	public static void main(String[] args) throws Exception {
		
		LookAndFeelInfo[] inst = UIManager.getInstalledLookAndFeels();
		String[] names = new String[inst.length];
		
		for (int i = 0; i < inst.length; i++) {
			System.out.println(names[i] = inst[i].getName());
		}

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		System.out.println("Update ? "+ getAndCreateOptionsPanel(null));
	}
	
	@SuppressWarnings("serial")
	private static boolean getAndCreateOptionsPanel(JDialog dlg) {
		final AtomicBoolean updateChanges = new AtomicBoolean(false);

		final ActionListener savelist = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateChanges.set(true);
			}
		};
		
		final JDialog dialog = dlg == null ? new JDialog() : dlg;
		dialog.setResizable(false);
		dialog.setSize(650, 400);
		
		dialog.setModalityType(ModalityType.TOOLKIT_MODAL);

		dialog.setTitle("Options");
		
		JPanel content = new JPanel(new BorderLayout());
		
		JTabbedPane tabs = new JTabbedPane();
		JScrollPane scroll = new JScrollPane(tabs);

		JPanel display = new JPanel();
		display.setName("Display System");
		display.setBackground(Color.LIGHT_GRAY);
		tabs.add(display);
		
		JPanel input = new JPanel();
		input.setName("Input System");
		
		input.setBackground(Color.LIGHT_GRAY);
		tabs.add(input);
		
		content.add(scroll, BorderLayout.CENTER);
		
		JPanel stat = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton saveDef = new JButton("Save as Defaults");
		saveDef.addActionListener(savelist);
		stat.add(saveDef);
		
		JPanel gap = new JPanel();
		gap.setPreferredSize(new Dimension(445, 0));

		stat.add(gap);
		stat.add(new JButton("Save"){{
			addActionListener(savelist);
		}});
		
		stat.add(new JButton("Close"){{
				addActionListener(new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
		}});
		
		//stat.setBackground(Color.DARK_GRAY);
		Dimension preferredSize = new Dimension(0, 40);
		stat.setPreferredSize(preferredSize);
		
		content.add(stat, BorderLayout.SOUTH);
		
		dialog.setContentPane(content);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		
		return updateChanges.get();
	}
}
