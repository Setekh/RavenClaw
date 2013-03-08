/*
 * This file is strictly bounded by Corvus Corax Entertainment and its prohibited
 * for commercial use, or any use what so ever.
 * This application can be ran only by an Corvus - Corax Employee
 */
package com.ravenclaw.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import javolution.util.FastMap;


import com.jme3.animation.AnimControl;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsVehicle;
import com.jme3.bullet.objects.VehicleWheel;
import com.jme3.effect.ParticleEmitter;
import com.jme3.font.BitmapText;
import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.ui.Picture;

/**
 * @author Seth
 */
public class SceneNodeTreeRenderer extends DefaultTreeCellRenderer {

	/*
	 * long
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8887684748693577427L;
	private final FastMap<Class<?>[], BufferedImage> icons = new FastMap<>();
	
	public SceneNodeTreeRenderer() {
		//setBackground(Color.GRAY);
	    //setBackgroundNonSelectionColor(Color.GRAY);
	    //setBackgroundSelectionColor(Color.ORANGE);
	    //setTextNonSelectionColor(Color.RED);
	    //setTextSelectionColor(Color.BLUE);
		try {
			BufferedImage img = ImageIO.read(getClass().getResourceAsStream("/Icons/animationcontrol.gif"));
			addIcons(img, AnimControl.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/audionode.gif"));
			addIcons(img, AudioNode.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/bitmaptext.gif"));
			addIcons(img, BitmapText.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/charactercontrol.gif"));
			addIcons(img, CharacterControl.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/HQ/rubik_s_pocket_cube.png"));//geometry.gif"));
			addIcons(img, Geometry.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/ghostcontrol.gif"));
			addIcons(img, PhysicsGhostObject.class);
			
			//img = ImageIO.read(getClass().getResourceAsStream("/Icons/ghostnode.gif"));
			//addIcons(img, PhysicsGhostObject.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/HQ/light-on.png"));
			addIcons(img, Light.class);
			
			//img = ImageIO.read(getClass().getResourceAsStream("/Icons/kweather.png"));
			//addIcons(img, SkyDome.class);
			
			//img = ImageIO.read(getClass().getResourceAsStream("/Icons/linknode.gif"));
			//addIcons(img, .class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/mesh.gif"));
			addIcons(img, Mesh.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/node.gif"));
			addIcons(img, Node.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/particleemitter.gif"));
			addIcons(img, ParticleEmitter.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/physicscontrol.gif"));
			addIcons(img, PhysicsControl.class);
			
			//img = ImageIO.read(getClass().getResourceAsStream("/Icons/physicsnode.gif"));
			//addIcons(img, PhysicsN.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/physicswheel.gif"));
			addIcons(img, VehicleWheel.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/picture.gif"));
			addIcons(img, Picture.class);
			
			//img = ImageIO.read(getClass().getResourceAsStream("/Icons/player.gif"));
			//addIcons(img, .class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/terrain.png"));
			addIcons(img, TerrainQuad.class, Terrain.class);
			
			img = ImageIO.read(getClass().getResourceAsStream("/Icons/vehicle.png"));
			addIcons(img, PhysicsVehicle.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addIcons(BufferedImage img, Class<?>... clzs) {
		icons.put(clzs, img);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if(value == null)
			return this;
		
		if(value instanceof DefaultMutableTreeNode) {
			value = ((DefaultMutableTreeNode)value).getUserObject();
		}
		
//		BufferedImage icon = icons.get(value.getClass());
		
		if(value instanceof Spatial) {
			setText(((Spatial) value).getName());
		}
		else
			setText(value.getClass().getSimpleName());
		
//		if(icon != null) {
//			setIcon(new ImageIcon((Image)icon));
//			return this;
//		}
//		
		for(Entry<Class<?>[], BufferedImage> ent : icons.entrySet()) {
			
			Class<?>[] clzs = ent.getKey();
			
			for(Class<?> cls : clzs)
			{
//				System.out.println("Scanning: "+cls+ " - "+value.getClass());
				if(cls == value.getClass()) {
					setIcon(new ImageIcon((Image)ent.getValue()));
					return this;
				}
			}
		}

		for(Entry<Class<?>[], BufferedImage> ent : icons.entrySet()) {
			
			Class<?>[] clzs = ent.getKey();
			
			for(Class<?> cls : clzs)
			{
				if(cls.isInstance(value)) {
					setIcon(new ImageIcon((Image)ent.getValue()));
					return this;
				}
			}
		}

		return this;
	}
}
