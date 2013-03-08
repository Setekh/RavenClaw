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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.ravenclaw.RavenClaw;
import com.ravenclaw.managers.TransformManager;
import com.ravenclaw.managers.TransformManager.TransformType;

import corvus.corax.Corax;

/**
 * @author Vlad
 */
@SuppressWarnings("serial")
public class ToolBar extends JToolBar implements ActionListener {

	private Color ubuntuMain = new Color(60, 59, 55);
	
	private JToggleButton move, rotate, scale;
	
	public ToolBar() { // The reason it looks like this, its cuz i got bored..
		try {
			BufferedImage[] normals = new BufferedImage[] {
				ImageIO.read(getClass().getResourceAsStream("/Icons/states/move.png")),
				ImageIO.read(getClass().getResourceAsStream("/Icons/states/rotate.png")),
				ImageIO.read(getClass().getResourceAsStream("/Icons/states/resize.png"))
			};
			
			BufferedImage[] lits = new BufferedImage[] {
				ImageIO.read(getClass().getResourceAsStream("/Icons/states/move_lit.png")),
				ImageIO.read(getClass().getResourceAsStream("/Icons/states/rotate_lit.png")),
				ImageIO.read(getClass().getResourceAsStream("/Icons/states/resize_lit.png"))
			};

			for (int i = 0; i < lits.length; i++) {
				JToggleButton button = new JToggleButton(new ImageIcon(normals[i]));
				
				button.setBorderPainted(false);
		        button.setFocusable(false);
		        button.setMargin(new Insets(0, 0, 0, 0));
		        button.setContentAreaFilled(false);

				button.setSelectedIcon(new ImageIcon(lits[i]));
				button.addActionListener(this);
				add(button);

				switch (i) {
					case 0x00:
						move = button;
						move.setSelected(true);
						break;
					case 0x01:
						rotate = button;
						break;
					case 0x02:
						scale = button;
						break;
				}
			}
			
			setMinimumSize(new Dimension(1,1));
			setOpaque(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		TransformManager transformManager = Corax.getInstance(TransformManager.class);
		if(e.getSource() == move) {
			if(move.isSelected()) {
				transformManager.setCurrentAction(TransformType.Translate);
				rotate.setSelected(false);
				scale.setSelected(false);
			}
		}
		else if(e.getSource() == rotate) {
			if(rotate.isSelected()) {
				transformManager.setCurrentAction(TransformType.Rotate);
				move.setSelected(false);
				scale.setSelected(false);
			}
		}
		else if(e.getSource() == scale) {
			if(scale.isSelected()) {
				transformManager.setCurrentAction(TransformType.Scale);
				rotate.setSelected(false);
				move.setSelected(false);
			}
		}
	}
	

	/**
	 * @return the move
	 */
	public JToggleButton getMove() {
		return move;
	}
	
	/**
	 * @return the rotate
	 */
	public JToggleButton getRotate() {
		return rotate;
	}
	
	/**
	 * @return the scale
	 */
	public JToggleButton getScale() {
		return scale;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
	    Graphics2D g2 = (Graphics2D)g;

//	    g2.setPaint(new GradientPaint(0, 0, ubuntuMain, 0, getHeight() / 2.5f, ubuntuMain.darker()));
//	    g2.fillRect(0, 0, getWidth(), getHeight() / 2);
//	    
//	    g2.setPaint(new GradientPaint(0, getHeight() / 2, ubuntuMain.darker(), 0, getHeight(), ubuntuMain));
//	    g2.fillRect(0, getHeight() / 2, getWidth(), getHeight());

	    g2.setPaint(new GradientPaint(0, 0, ubuntuMain, 0, getHeight(), ubuntuMain.brighter()));
	    g2.fillRect(0, 0, getWidth(), getHeight());
	}
}
