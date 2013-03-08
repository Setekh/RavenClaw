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
package test.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.VerticalLayout;

/**
 * @author scorn
 */
public final class SwingX {

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new JFrame();
		
		@SuppressWarnings("serial")
		JPanel InspectorRoot = new JPanel(new VerticalLayout()) {

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
		
		JScrollPane con = new JScrollPane(InspectorRoot);
		
		for (int i = 0; i < 20; i++) {
			InspectorElement elem = new InspectorElement("Some Control v"+i);
			JXPanel panel =  elem.getContent();
			
			JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			pane.add(new JButton("Add Me"));
			pane.add(new JButton("Delete Me"));
			pane.add(new JButton("Cut Me"));
			pane.add(new JButton("Slaughter Me"));
			pane.add(new JButton("Lynch Me"));
			panel.add(pane);
			
			InspectorRoot.add(elem);
		}
		
		frame.setContentPane(con);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 400);
		frame.setVisible(true);
	}
	
	@SuppressWarnings("serial")
	public static class InspectorElement extends JPanel {
		
		// Stuff
		private JToggleButton toggleButton;
		private JLabel lableTitle;

		private JXCollapsiblePane container;
		private JXPanel content;
		
		public InspectorElement(String title) {
			super(new FlowLayout(FlowLayout.LEFT));
			
			setBackground(Color.LIGHT_GRAY);
			lableTitle = new JLabel(title);
			
			container = new JXCollapsiblePane(Direction.DOWN);
			content = (JXPanel) container.getContentPane();
			content.setBackground(Color.DARK_GRAY);
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
}
