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
package com.ravenclaw.swing.windows.inspector;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.jdesktop.swingx.JXPanel;

import com.jme3.scene.Spatial;

@SuppressWarnings("serial")
public abstract class InspectorElement extends JPanel {
	
	// Stuff
	private JToggleButton toggleButton;
	private JLabel lableTitle;

	private JXCollapsiblePane container;
	private JXPanel content;
	
	public InspectorElement(String title) {
		super(new FlowLayout(FlowLayout.LEFT));
		
		//setBackground(Color.LIGHT_GRAY);
		lableTitle = new JLabel(title);
		
		container = new JXCollapsiblePane(Direction.DOWN);
		content = (JXPanel) container.getContentPane();
		content.setBackground(Color.LIGHT_GRAY);
		
		try {
			final ImageIcon imageCollapse = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Icons/icon_collapse.png")));
			final ImageIcon imageExpand = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Icons/icon_expand.png")));
			
			toggleButton = new JToggleButton(imageExpand, true);
			toggleButton.setBorderPainted(false);
	        toggleButton.setFocusable(false);
	        toggleButton.setMargin(new Insets(0, 0, 0, 0));
	        toggleButton.setContentAreaFilled(false);
	        toggleButton.setSelectedIcon(imageCollapse);
	        toggleButton.addActionListener(container.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
			this.add(toggleButton);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		add(lableTitle);
	}
	
	public abstract void setTarget(Spatial target);
	
	/**
	 * @return the content
	 */
	public JXPanel getContent() {
		return content;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#getParent()
	 */
	@Override
	public Container getParent() {
		return super.getParent();
	}
	/**
	 * @return the container
	 */
	public JXCollapsiblePane getCollapsibleContainer() {
		return container;
	}
	
	public boolean isSelected() {
		return toggleButton.isSelected();
	}
	
	public void setSelected(boolean enabled) {
		toggleButton.setSelected(enabled);
	}
}