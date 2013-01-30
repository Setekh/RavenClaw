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

import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;

/**
 * @author Vlad
 */
public class RawInputAdaptor implements RawInputListener {

	/* (non-Javadoc)
	 * @see com.jme3.input.RawInputListener#beginInput()
	 */
	@Override
	public void beginInput() {
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.RawInputListener#endInput()
	 */
	@Override
	public void endInput() {
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.RawInputListener#onJoyAxisEvent(com.jme3.input.event.JoyAxisEvent)
	 */
	@Override
	public void onJoyAxisEvent(JoyAxisEvent evt) {
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.RawInputListener#onJoyButtonEvent(com.jme3.input.event.JoyButtonEvent)
	 */
	@Override
	public void onJoyButtonEvent(JoyButtonEvent evt) {
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.RawInputListener#onMouseMotionEvent(com.jme3.input.event.MouseMotionEvent)
	 */
	@Override
	public void onMouseMotionEvent(MouseMotionEvent evt) {
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.RawInputListener#onMouseButtonEvent(com.jme3.input.event.MouseButtonEvent)
	 */
	@Override
	public void onMouseButtonEvent(MouseButtonEvent evt) {
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.RawInputListener#onKeyEvent(com.jme3.input.event.KeyInputEvent)
	 */
	@Override
	public void onKeyEvent(KeyInputEvent evt) {
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.RawInputListener#onTouchEvent(com.jme3.input.event.TouchEvent)
	 */
	@Override
	public void onTouchEvent(TouchEvent evt) {
	}

}
