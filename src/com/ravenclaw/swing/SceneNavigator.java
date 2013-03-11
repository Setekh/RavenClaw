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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.utils.ArchidIndex;

import corvus.corax.Corax;
import corvus.corax.event.EventMonitor;
import corvus.corax.event.listeners.CoraxListener;
import corvus.corax.tools.MelloriObjectBuffer;

/**
 * @author Seth
 */
@SuppressWarnings("serial")
public class SceneNavigator extends JPanel implements TreeWillExpandListener, CoraxListener {

	private final JTree tree;
	private RavenClaw claw;
	
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel model;

	public SceneNavigator() {
		super(new BorderLayout());
		
		Corax.monitor().registerListener(this);
		
		claw = Corax.getInstance(RavenClaw.class);
		
		setName(getClass().getSimpleName());
		
		tree = new JTree();
		JScrollPane scroll = new JScrollPane(tree);
		
		tree.setCellRenderer(new SceneNodeTreeRenderer());
		
		rootNode = new DefaultMutableTreeNode(claw.getMainNode());
		model = new DefaultTreeModel(rootNode);

		//tree.addTreeSelectionListener(this);
		//tree.addTreeExpansionListener((TreeExpansionListener)this);
		//model.addTreeModelListener(this);
		
		parseLights(claw.getMainNode(), rootNode, model);
		parseControls(claw.getMainNode(), rootNode, model);
		
		for(Spatial spat : claw.getMainNode().getChildren()) {
			parseNodes(spat, rootNode, model);
		}
		
		tree.setModel(model);

		add(scroll, BorderLayout.CENTER);
		
		Dimension sizes = new Dimension(0, 18);
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
		bar.setPreferredSize(sizes);
		bar.setMaximumSize(sizes);
		
		JButton button = new JButton("Refresh");
		button.setPreferredSize(new Dimension(26, 10));

		button.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reload();
			}
		});
		bar.add(button);
		
		add(bar, BorderLayout.NORTH);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(0, 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		JLabel statusLabel = new JLabel("status");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
	}

	public void parseNodes(Spatial spat, DefaultMutableTreeNode parent, DefaultTreeModel model) {

		DefaultMutableTreeNode rez = new DefaultMutableTreeNode(spat); 
		model.insertNodeInto(rez, parent, parent.getChildCount());
		
		parseLights(spat, rez, model);
		parseControls(spat, rez, model);
		
		if(spat instanceof Node && ((Node)spat).getChildren().size() > 0)
		{
			Node fat = (Node) spat;
			
			for(Spatial child : fat.getChildren()) {
				parseNodes(child, rez, model);
			}
			
		}
	}
	
	public void parseLights(Spatial owner, DefaultMutableTreeNode parent, DefaultTreeModel model ) {
		
		for(Light light : owner.getLocalLightList()) {
			
			if(light == null)
				continue;
			
			model.insertNodeInto(new DefaultMutableTreeNode(light), parent, parent.getChildCount());
		}
	}

	public void parseControls(Spatial owner, DefaultMutableTreeNode parent, DefaultTreeModel model ) {

		for (int i = 0; i < owner.getNumControls(); i++) {
			Control control = owner.getControl(i);
			
			if(control == null)
				continue;
			model.insertNodeInto(new DefaultMutableTreeNode(control), parent, parent.getChildCount());
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
	{
		System.out.println(event);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
	}

	public void reload() {
		rootNode.removeAllChildren();
		rootNode.removeFromParent();
		
		rootNode = new DefaultMutableTreeNode(claw.getMainNode());
		model = new DefaultTreeModel(rootNode);

		updateUI();
		
		parseLights(claw.getMainNode(), rootNode, model);
		parseControls(claw.getMainNode(), rootNode, model);
		
		for(Spatial spat : claw.getMainNode().getChildren()) {
			parseNodes(spat, rootNode, model);
		}
		
		tree.setModel(model);
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
			case ArchidIndex.NewEntity:
				reload();
				break;
		}
		return EventMonitor.Nothing;
	}

}
