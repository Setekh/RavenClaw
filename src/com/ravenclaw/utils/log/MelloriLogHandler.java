/*
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Neither the name of 'Corvus Corax' and 'Corvus Mellori' nor the names of its contributors
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
 *
 * Copyright (c) 2010-2011 Corvus Corax
 * All rights reserved.
 */
package com.ravenclaw.utils.log;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;

/**
 * @author Seth
 */
public class MelloriLogHandler extends Handler
{
	private final FastMap<Level, org.apache.log4j.Level> _levels = new FastMap<Level, org.apache.log4j.Level>();
	private final FastList<String> _prohibited = FastList.newInstance();
	
	public MelloriLogHandler()
	{//de.lessvoid.nifty.NiftyStopwatch
		_levels.put(Level.ALL, org.apache.log4j.Level.ALL);
		_levels.put(Level.OFF, org.apache.log4j.Level.OFF);
		_levels.put(Level.WARNING, org.apache.log4j.Level.WARN);
		_levels.put(Level.SEVERE, org.apache.log4j.Level.ERROR);
		_levels.put(Level.INFO, org.apache.log4j.Level.INFO);
		_levels.put(Level.CONFIG, org.apache.log4j.Level.DEBUG);
		_levels.put(Level.FINE, org.apache.log4j.Level.DEBUG);
		_levels.put(Level.FINER, org.apache.log4j.Level.TRACE);
		_levels.put(Level.FINEST, org.apache.log4j.Level.TRACE);
		
		_prohibited.add("de.lessvoid.nifty.NiftyStopwatch");
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(LogRecord record)
	{
		if(_prohibited.contains(record.getSourceClassName()))
			return;
		
		String loggerName = record.getLoggerName();
		if(loggerName == null)
		{
			loggerName = "";
		}

		Logger log = Logger.getLogger(loggerName);
		
		org.apache.log4j.Level level = _levels.get(record.getLevel());
		Level nlevel = record.getLevel();

		if(level == null)
		{
			LogLog.warn("Warning: cannot find log4j level for: " + nlevel.getName(), new Exception());
			level = new LoggerDummy(nlevel.intValue(), nlevel.getName(), nlevel.intValue());
		}
		String message = record.getParameters() != null && record.getParameters().length > 0 ? MessageFormat.format(record.getMessage(), record.getParameters()) : record.getMessage();
		log.log(java.util.logging.Logger.class.getName(), level, message, record.getThrown());
	}

	public class LoggerDummy extends org.apache.log4j.Level
	{
		private static final long serialVersionUID = -7491387917132716880L;

		/**
		 * @param level
		 * @param levelStr
		 * @param syslogEquivalent
		 */
		protected LoggerDummy(int level, String levelStr, int syslogEquivalent) {
			super(level, levelStr, syslogEquivalent);
		}
		
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() throws SecurityException {
	}
}