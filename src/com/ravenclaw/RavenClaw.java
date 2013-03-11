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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.logging.Handler;
import java.util.logging.LogManager;

import javax.imageio.ImageIO;
import javax.swing.FocusManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jdesktop.swingx.JXFrame;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.ravenclaw.game.SceneGraph;
import com.ravenclaw.swing.CanvasFocusListener;
import com.ravenclaw.swing.RCMenuBar;
import com.ravenclaw.swing.ToolBar;
import com.ravenclaw.swing.WindowService;
import com.ravenclaw.swing.windows.InspectorWindow;
import com.ravenclaw.swing.windows.NodeExplorerWindow;
import com.ravenclaw.swing.windows.PreviewWindow;
import com.ravenclaw.swing.windows.ProjectWindow;
import com.ravenclaw.swing.windows.SceneWindow;
import com.ravenclaw.utils.ArchidIndex;
import com.ravenclaw.utils.log.MelloriLogHandler;
import com.ravenclaw.utils.log.ThrowableMessageFactory;

import corvus.corax.Corax;
import corvus.corax.processing.annotation.Initiate;
import corvus.corax.processing.annotation.Provide;

/**
 * @author Seth
 */
public final class RavenClaw {

	private static final Logger _log = Logger.getLogger(RavenClaw.class);

	private Canvas canvas;
	private SceneGraph app;

	private WindowService windowService;
	
	private Node mainNode = new Node("RavenClaw: Node");

	private final JXFrame frame;

	private boolean _1rst;
	
	@SuppressWarnings("serial")
	public RavenClaw() {
		frame = new JXFrame("Raven Claw") {
			public void dispose() {
				
				int ret = JOptionPane.showConfirmDialog(Corax.getInstance(RavenClaw.class).getFrame(),
						"Are you sure you want to exit?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
				
				if(ret == 0) { // OK
					super.dispose();

					windowService.save(); // Save last
					System.out.println("Good bye.");
					System.exit(0);
				}
			};
		};
		
		windowService = new WindowService(this, frame);
		createFrame();
	}
	
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
		frame.setLayout(new BorderLayout());

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new ToolBar(), BorderLayout.NORTH);
		
		//panel.add(contentpanel, BorderLayout.CENTER);
		
		//frame.setContentPane(contentpanel);
		frame.setContentPane(panel);

		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if(e.getComponent() == frame) {
					Corax.listen(ArchidIndex.FrameResized, null, frame.getSize(), frame);
				}
			}
		});
		
		frame.pack();
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

		frame.setSize((int)(size.width / 1.23), (int) (size.height / 1.23));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		_1rst = true;
	}
	
	@Initiate
	public void start() {
		Corax corax = Corax.instance();

		if (app != null) {
			mainNode.removeFromParent();
			app.stop(true);
			corax.disposeInstance(app);
		}

		app = new SceneGraph();

		AppSettings settings = new AppSettings(true);
		app.setSettings(settings);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

		settings.setWidth((int) (size.width / 2.23));
		settings.setHeight((int) (size.height / 2.23));

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

		// Init
		corax.addSingleton(app.getClass(), app);

        app.setPauseOnLostFocus(false);
        
		app.createCanvas();
		app.startCanvas(true);

		canvas = ((JmeCanvasContext)app.getContext()).getCanvas();
		canvas.addFocusListener(new CanvasFocusListener());

		//canvas.setMinimumSize(new Dimension((int)(settings.getWidth() / 1.23), (int)(settings.getHeight() / 1.23)));
		canvas.setMinimumSize(new Dimension(100, 10));
		canvas.setPreferredSize(new Dimension((int) (size.width / 1.23), (int) (size.height / 1.23)));
		
		if(_1rst) {
			canvas.setBackground(Color.DARK_GRAY);

			windowService.registerWindow(new SceneWindow(canvas), false);
			windowService.registerWindow(new InspectorWindow(), false);
			windowService.registerWindow(new NodeExplorerWindow(), false);
			windowService.registerWindow(new ProjectWindow(), false);
			windowService.registerWindow(new PreviewWindow(), false);

			_1rst = false;

			windowService.loadLast();
			frame.setVisible(true);
		}
		else {
			windowService.restartAll();
		}
		
		windowService.getWindow(SceneWindow.class).setCanvas(canvas);
	}

	/**
	 * @return the frame
	 */
	public JXFrame getFrame() {
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
	@Provide
	public SceneGraph getApplication() {
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Remember to disable it at first if running in eclipse, so all the needed dependences get extracted.
		//JmeSystem.setLowPermissions(true);

		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			_log.warn("Unable to set system default theme!", e);
		}

		java.util.logging.Logger logger = LogManager.getLogManager().getLogger("");
		for(Handler h : logger.getHandlers())
			logger.removeHandler(h);

		logger.addHandler(new MelloriLogHandler());
		
		DOMConfigurator.configure("./log/log4j.xml");

		Hierarchy lr = (Hierarchy) org.apache.log4j.LogManager.getLoggerRepository();
		try
		{
			Field field = lr.getClass().getDeclaredField("defaultFactory");
			field.setAccessible(true);
			Class<?> factory = Class.forName(System.getProperty("log4j.loggerfactory", ThrowableMessageFactory.class.getName()));
			field.set(lr, factory.newInstance());
			field.setAccessible(false);
		}
		catch(Exception e) {
			_log.warn("Unable to set custom log factory!", e);
		}
		
		Corax.create(new Setup());
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

	/** 
	 * Recommended to add spatials from this method, so the listener can trigger an event
	 */
	public void attachChild(final Spatial spat) {
		getApplication().enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				getMainNode().attachChild(spat);
				Corax.monitor().listen(ArchidIndex.NewEntity, null, spat);
				return null;
			}
		});
	}
	
	public static void safety(Callable<Void> voids) {
		Corax.getInstance(RavenClaw.class).getApplication().enqueue(voids);
	}
	
	/**
	 * Returns true if the canvas is in mouse focus
	 */
	public static boolean checkFocus() {
		RavenClaw claw = Corax.getInstance(RavenClaw.class);
		Point xy = MouseInfo.getPointerInfo().getLocation();
		
		if(FocusManager.getCurrentManager().getActiveWindow() != null) {// Another window in focus
			//System.out.println("Returning False");
			return false;
		}
			
		Component comp = claw.frame.findComponentAt(xy);
//		if(comp != null)
//			System.out.println("Current Component in view: "+comp.getClass().getSimpleName());
//		else
//			System.out.println("Comp Null");
		return comp == claw.getCanvas();
	}

}
