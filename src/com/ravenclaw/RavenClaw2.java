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
package com.ravenclaw;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.VerticalLayout;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.FloatDockModel;
import com.javadocking.model.codec.DockModelPropertiesDecoder;
import com.javadocking.model.codec.DockModelPropertiesEncoder;
import com.ravenclaw.FirstExample.TextPanel;
import com.ravenclaw.SwingX.InspectorElement;
import com.ravenclaw.swing.RCMenuBar;

/**
 * @author scorn
 */
public class RavenClaw2 {

	private static final Logger _log = Logger.getLogger(RavenClaw.class);
	
	private final JXFrame frame;
	private FloatDockModel dockModel;
	
	@SuppressWarnings("serial")
	public RavenClaw2() {
		frame = new JXFrame() {
			public void dispose() {

				//int ret = JOptionPane.showConfirmDialog(Corax.getInstance(RavenClaw.class).getFrame(), "Are you sure you want to exit?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);

				//if (ret == 0) { // OK
					super.dispose();
					saveDock();
					System.out.println("Good bye.");
					System.exit(0);
				//}
			}

		};
		
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream("Textures/icon.png");
			if(stream != null) {
				frame.setIconImage(ImageIO.read(stream));
			}
			else
				_log .warn(getClass().getSimpleName()+": Image icon is null, cannot find icon file!");
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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

		DefaultDockable[] docks = new DefaultDockable[] {
				new DefaultDockable("Scene-Window", new TextPanel("I am window 1."), "Window 1", null, DockingMode.ALL),
				new DefaultDockable("Inspector-Window", con, "Window 2", null, DockingMode.ALL),
				new DefaultDockable("Project-Window", new TextPanel("I am window 3."), "Window 3", null, DockingMode.ALL),
				new DefaultDockable("Composer-Window", new TextPanel("I am window 4."), "Window 4", null, DockingMode.ALL),
				new DefaultDockable("Explorer-Window", new TextPanel("I am window 5."), "Window 5", null, DockingMode.ALL),
				new DefaultDockable("Preview-Window", new TextPanel("I am window 5."), "Window 5", null, DockingMode.ALL)
			};
			
		for (int i = 0; i < docks.length; i++) {
			docks[i].setTitle(docks[i].getID().split("-")[0]);
		}
		
		frame.setJMenuBar(new RCMenuBar());
		JPanel content = new JPanel(new BorderLayout());
		
		File file = new File("./assets/sdk/", "dockable.dck");

		// Try to decode the dock model from file.
		DockModelPropertiesDecoder dockModelDecoder = new DockModelPropertiesDecoder();
		if (dockModelDecoder.canDecodeSource(file.getPath()))
		{
			try 
			{
				// Create the map with the dockables, that the decoder needs.
				Map dockablesMap = new HashMap();
				for (int index = 0; index < docks.length; index++)
				{
					dockablesMap.put(docks[index].getID(), docks[index]);
				}	
								
				// Create the map with the owner windows, that the decoder needs.
				Map ownersMap = new HashMap();
				ownersMap.put("RavenClawWindow", frame);
				
				// Decode the file.
				dockModel = (FloatDockModel)dockModelDecoder.decode(file.getPath(), dockablesMap, ownersMap, null);
				
				SplitDock rootDock = (SplitDock) dockModel.getRootDock("Main");
				content.add(rootDock, BorderLayout.CENTER);
			}
			catch (FileNotFoundException fileNotFoundException){
				System.out.println("Could not find the file [" + file.getPath() + "] with the saved dock model.");
				System.out.println("Continuing with the default dock model.");
			}
			catch (IOException ioException){
				System.out.println("Could not decode a dock model: [" + ioException + "].");
				ioException.printStackTrace();
				System.out.println("Continuing with the default dock model.");
			}
		}
		else {
			dockModel = new FloatDockModel();
			dockModel.addOwner("RavenClawWindow", frame);
			DockingManager.setDockModel(dockModel);
	
			SplitDock rootDock = new SplitDock();
			rootDock.setAutoscrolls(true);
	
			TabDock dock = new TabDock();
	
			for (int i = 0; i < docks.length; i++) {
				dock.addDockable(docks[i], new Position(i));
			}
			
			rootDock.addChildDock(dock, new Position(Position.CENTER));
			dockModel.addRootDock("Main", rootDock, frame);
	
			content.add(rootDock, BorderLayout.CENTER);
		}
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setContentPane(content);
		frame.setSize((int)(size.width / 1.23), (int) (size.height / 1.23));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void saveDock() {
		DockModelPropertiesEncoder encoder = new DockModelPropertiesEncoder();
		
		try {
			File file = new File("./assets/sdk/", "dockable.dck");
			
			if(!file.exists()) {
				file.createNewFile();
			}
			
			encoder.export(dockModel, file.getPath());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new RavenClaw2().frame.setVisible(true);
	}
}
