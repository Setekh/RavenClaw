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
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.system.JmeSystem;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.swing.CanvasFocusListener;
import com.ravenclaw.swing.RCMenuBar;
import com.ravenclaw.utils.ArchidIndex;

import corvus.corax.Corax;
import corvus.corax.processing.annotation.Initiate;

/**
 * @author Seth
 */
public final class RavenClaw {

	private static final Logger _log = Logger.getLogger(RavenClaw.class);

	private final JFrame frame = new JFrame("Raven Claw");
	private Canvas canvas;
	private SceneGraph app;

	private Node mainNode = new Node("RavenClaw: Node");
	
	@Initiate
	private void createFrame() {
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
		
		frame.setJMenuBar(new RCMenuBar());
		
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if(e.getComponent() == frame) {
					Corax.listen(ArchidIndex.FrameResized, null, frame.getSize(), frame);
				}
			}
		});
	}
	
	@Initiate
	public void start() {
		if (app != null) {
			app.stop(true);
			frame.remove(canvas);
		}

		app = new SceneGraph();

		// Init
		Corax.instance().addSingleton(app.getClass(), app);

		AppSettings settings;
		app.setSettings(settings = new AppSettings(true));

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

		settings.setWidth((int) (size.width / 1.23));
		settings.setHeight((int) (size.height / 1.23));

		File file = new File("./binary/Settings.ini");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			writeSettings(settings, file);
		} else {
			try {
				FileInputStream in = new FileInputStream(file);
				settings.load(in);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		app.createCanvas();
		app.startCanvas();

		canvas = ((JmeCanvasContext)app.getContext()).getCanvas();
		canvas.addFocusListener(new CanvasFocusListener());

		canvas.setSize(settings.getWidth(), settings.getHeight());
		
		frame.add(canvas, BorderLayout.CENTER);
		//setAlwaysOnTop(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Needs to be here, since low permision flag means the native libs wont be extracted at startup
		// and native bullet wont run.
		JmeSystem.setLowPermissions(true);
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * @return the mainNode
	 */
	public Node getMainNode() {
		return mainNode;
	}

	/**
	 * @return the canvas
	 */
	public Canvas getCanvas() {
		return canvas;
	}
	
	/**
	 * @return the app
	 */
	public SceneGraph getAppplication() {
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		
		try
		{
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		Corax.create(new Setup());
		
//		RavenClaw cw = Corax.getInstance(RavenClaw.class);
//		
//		int ticks = 0;
//		while (true) {
//			if(ticks == 6) {
//				cw.start();
//				ticks = 0;
//			}
//			
//			Thread.sleep(800);
//			ticks++;
//		}
	}

	public void writeSettings(AppSettings settings, File file) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			settings.save(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
