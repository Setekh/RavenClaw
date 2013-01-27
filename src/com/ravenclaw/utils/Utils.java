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
package com.ravenclaw.utils;

import java.util.ArrayList;

import javolution.util.FastMap;

import org.lwjgl.input.Mouse;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ravenclaw.RavenClaw;
import com.ravenclaw.game.SceneGraph;

import corvus.corax.Corax;

/**
 * @author Vlad
 */
public class Utils {

    private static final FastMap<Class<?>, Class<?>> nativeTypes = new FastMap<Class<?>, Class<?>>();

    static
    {
        nativeTypes.put(Boolean.class, boolean.class);
        nativeTypes.put(Character.class, char.class);
        nativeTypes.put(Byte.class, byte.class);
        nativeTypes.put(Short.class, short.class);
        nativeTypes.put(Integer.class, int.class);
        nativeTypes.put(Long.class, long.class);
        nativeTypes.put(Float.class, float.class);
        nativeTypes.put(Double.class, double.class);
    }

    /**
     * If it's not a primitive it will return the type parameter
     * @param type
     */
    public static Class<?> identifyAndGet(Class<?> type) {
		return nativeTypes.containsKey(type) ? nativeTypes.get(type) : type;
    }

	public static CollisionResults pick(Node node)
	{
		SceneGraph claw = Corax.getInstance(RavenClaw.class).getAppplication();
		Vector2f v = new Vector2f(Mouse.getX(), Mouse.getY());
		Vector3f pos = claw.getCamera().getWorldCoordinates(v, 0.0f);
		Vector3f dir = claw.getCamera().getWorldCoordinates(v, 0.3f);
		dir.subtractLocal(pos).normalizeLocal();

		Ray r = new Ray(pos, dir);

		CollisionResults results = new CollisionResults();
		node.collideWith(r, results);
		
		return results;
	}
	
	public static CollisionResult pickOne(Node node)
	{
		SceneGraph claw = Corax.getInstance(RavenClaw.class).getAppplication();
		Vector2f v = new Vector2f(Mouse.getX(), Mouse.getY());
		Vector3f pos = claw.getCamera().getWorldCoordinates(v, 0.0f);
		Vector3f dir = claw.getCamera().getWorldCoordinates(v, 0.3f);
		dir.subtractLocal(pos).normalizeLocal();

		Ray r = new Ray(pos, dir);

		CollisionResults results = new CollisionResults();
		node.collideWith(r, results);
		
		return results.getClosestCollision();
	}

	public static ArrayList<Spatial> parseSpatials(Node node) {
		ArrayList<Spatial> array = new ArrayList<>();
		
		int size = node.getChildren().size();

		for (int i = 0; i < size; i++) {
			Spatial spat = node.getChild(i);
			
			if(spat instanceof Node) {
				ArrayList<Spatial> list = parseSpatials((Node) spat);
				array.addAll(list);
				list.clear();
			}
			else
				array.add(spat);
		}
		
		return array;
	}
}
