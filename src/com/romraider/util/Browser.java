/*
 * RomRaider Open-Source Tuning, Logging and Reflashing
 * Copyright (C) 2006-2012 RomRaider.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.romraider.util;

import java.lang.ProcessBuilder;
import java.lang.Process;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;

/**
* A class to open URLs in a browser.
* @author Stephen Hineline
* @version 1.0
*/

public final class Browser{
	
	private final static String OS = System.getProperty("os.name");
	private final static String BROWSERS[] = { "chrome", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "chromium", "iceweasel", "conkeror", "midori", "kazehakase"};
	
	/**
	* Opens a browser to specified URL.
	* @parm URL	URL of requested page
	* @return		boolean: True if opened, False if not
	* @since 1.0
	*/
	public final static boolean openURL(final String URL){
		if(OS.contains("Windows") || OS.contains("windows")){
			try{
				new ProcessBuilder("rundll32.exe", "url.dll,FileProtocolHandler", URL).start();
				return true;
			}catch(final Exception e){}
		}else{ //Linux
			for(int i = 0; i < BROWSERS.length; i++){
				Process PROCESS;
				try{
					PROCESS = new ProcessBuilder(new String[] {"which",BROWSERS[i]}).start();
				}catch(final Exception e){ continue; }
				final InputStream IN_STREAM = PROCESS.getInputStream();
				if(IN_STREAM != null){
					final BufferedReader READER = new BufferedReader(new InputStreamReader(IN_STREAM));
					String line;
					try{
						while ((line = READER.readLine()) != null) {
							if(!line.equals("")){
								try{
									new ProcessBuilder(new String[] {BROWSERS[i],URL}).start();
									return true;
								}catch(final Exception e){}
							}
						}
					}catch(final Exception e){ continue; }
				}
			}
		}
		return false;
	}

}
