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
package com.ravenclaw.swing.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.VerticalLayout;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.managers.ObjectManager.ObjectData;
import com.ravenclaw.managers.SelectionManager.Selection;
import com.ravenclaw.swing.WindowService;
import com.ravenclaw.swing.WindowService.RavenClawWindow;
import com.ravenclaw.swing.windows.inspector.InspectorElement;
import com.ravenclaw.utils.ArchidIndex;

import corvus.corax.Corax;
import corvus.corax.event.EventMonitor;
import corvus.corax.event.listeners.CoraxListener;
import corvus.corax.processing.annotation.Inject;
import corvus.corax.tools.MelloriObjectBuffer;

/**
 * @author scorn
 */
public class InspectorWindow extends RavenClawWindow implements CoraxListener{

	@Inject
	private RavenClaw claw;
	
	@Inject
	private WindowService winService;
	
	private JScrollPane scrollPane;
	private JPanel InspectorRoot;
	private Selection target;
	private JTextField namefield;
	
	private static final JLabel emptyLabel = new JLabel("No target");
	
	@SuppressWarnings("serial")
	public InspectorWindow() {
		super("insp", "Inspector");
		
		InspectorRoot = new JPanel(new VerticalLayout()) {

			@Override
			public Component add(Component comp) {
				Component toAdd = super.add(comp);

				if(comp instanceof InspectorElement)
					add(((InspectorElement) comp).getCollapsibleContainer());

				return toAdd;
			}

			@Override
			public void remove(Component comp) {
				super.remove(comp);
				if(comp instanceof InspectorElement)
					remove(((InspectorElement) comp).getCollapsibleContainer());
			}
		};
		
		JPanel panel = new JPanel(new VerticalLayout());

		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

		try {
			row1.add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Icons/HQ/rubik_s_pocket_cube.png")))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		namefield = new JTextField();
		namefield.setMaximumSize(new Dimension(0, 9));
		namefield.setText("Nothing Selected.");
		namefield.setEnabled(false);
		
		namefield.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(InspectorWindow.this.getTarget() != null) {
					InspectorWindow.this.getTarget().getObjData().getMaster().setName(namefield.getText());
					claw.getCanvas().requestFocus();
					winService.getWindow(NodeExplorerWindow.class).reload();
				}
			}
		});
		
		row1.add(namefield);
		panel.add(row1);

		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//Todo more stuff?
		panel.add(row2);

		InspectorRoot.add(panel);

		InspectorRoot.setBackground(Color.LIGHT_GRAY);
		scrollPane = new JScrollPane(InspectorRoot);
		
		Corax.monitor().registerListener(this);
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.swing.WindowService.RavenClawWindow#getComponent()
	 */
	@Override
	public Component getComponent() {
		return scrollPane;
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.swing.WindowService.RavenClawWindow#onStart()
	 */
	@Override
	public void onStart() {
		// just a demo

		if(!isInitiate()) {
			InspectorRoot.add(emptyLabel);
		}
	}

	/* (non-Javadoc)
	 * @see com.ravenclaw.swing.WindowService.RavenClawWindow#onRestart()
	 */
	@Override
	public void onRestart() {
		InspectorRoot.removeAll();
		
		if(target == null)
			InspectorRoot.add(emptyLabel);
		else
			processTarget(target);
	}

	public void processTarget(Selection target) {
		//TODO Make a new kind of edit mode, edit a specific node or smth.
		// Atm its set just to work.. fu
		
		ObjectData data = target.getObjData();
		
		Spatial master = data.getMaster();
		
		namefield.setEnabled(true);
		namefield.setText(master.getName());
		
		if(master instanceof Node) {
			Node parentNode = (Node)master;

			
			parentNode.getChild(0);
			
		}
		else {
			
		}
		
	}

	/**
	 * @return the target
	 */
	public Selection getTarget() {
		return target;
	}
	
	/* (non-Javadoc)
	 * @see com.ravenclaw.swing.WindowService.RavenClawWindow#onClose()
	 */
	@Override
	public void onClose() {
		
	}

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxAbstractListener#enabled()
	 */
	@Override
	public void enabled() { }

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxAbstractListener#disabled()
	 */
	@Override
	public void disabled() { }

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxListener#onEvent(int, corvus.corax.tools.MelloriObjectBuffer)
	 */
	@Override
	public Object onEvent(int key, MelloriObjectBuffer buff) {
		
		switch (key) {
			case ArchidIndex.Selected:
				target = buff.get();
				processTarget(target);
				break;
			default:
				break;
		}
		
		return EventMonitor.Nothing;
	}
}
