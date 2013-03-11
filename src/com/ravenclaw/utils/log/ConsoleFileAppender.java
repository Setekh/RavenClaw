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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Seth
 */
public class ConsoleFileAppender extends FileAppender
{
	
	/* (non-Javadoc)
	 * @see org.apache.log4j.FileAppender#setFile(java.lang.String, boolean, boolean, int)
	 */
	@Override
	public synchronized void setFile(String filePath, boolean arg1, boolean arg2, int arg3) throws IOException
	{
		if(!arg1)
		{
			File dir = new File("log/backup");
			File file = new File(filePath);
			
			if(!dir.exists())
				dir.mkdir();
			
			if(file.exists())
			{
				SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				FileOutputStream fos = new FileOutputStream(new File("log/backup/"+file.getName()+"-"+formater.format(new Date(file.lastModified()))+".log"));
				BufferedReader br = new BufferedReader(new FileReader(file));
				
				while(br.ready())
					fos.write(br.read());
				
				br.close();
				fos.flush();
				fos.close();
			}
		}
		
		super.setFile(filePath, arg1, arg2, arg3);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.log4j.WriterAppender#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	public void append(LoggingEvent event) {

		super.append(event);
	}
}