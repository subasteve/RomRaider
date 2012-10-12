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

package com.romraider.io.serial.port;

import static com.romraider.util.ParamChecker.checkNotNull;
import static com.romraider.util.ThreadUtil.sleep;
import gnu.io.CommPortIdentifier;
import org.apache.log4j.Logger;

import static org.apache.log4j.Logger.getLogger;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

public final class SerialPortRefresher implements Runnable {
    private static final Logger LOGGER = getLogger(SerialPortRefresher.class);
    private static final long PORT_REFRESH_INTERVAL = 15000L;
    private final SerialPortDiscoverer serialPortDiscoverer = new SerialPortDiscovererImpl();
    private final SerialPortRefreshListener listener;
    private final String defaultLoggerPort;
    private boolean started;
    private boolean refreshMode;

    public SerialPortRefresher(SerialPortRefreshListener listener, String defaultLoggerPort) {
        checkNotNull(listener);
        this.listener = listener;
        this.defaultLoggerPort = defaultLoggerPort;
    }

    public void run() {
        refreshPortList();
        started = true;
        while (true) {
            sleep(PORT_REFRESH_INTERVAL);
            if (refreshMode) {
                refreshPortList();
            }
        }
    }

    public boolean isStarted() {
        return started;
    }

    public void setRefreshMode(boolean b) {
        refreshMode = b;
        if (refreshMode) {
            refreshPortList();
        }
    }

    private void refreshPortList() {
        try {
            listener.refreshPortList(listSerialPorts(), defaultLoggerPort);
        } catch (Exception e) {
            LOGGER.error("Error refreshing serial ports", e);
        }
    }

    private Set<String> listSerialPorts() {
    	final String OS = System.getProperty("os.name");
    	Set<String> portNames = new TreeSet<String>();
    	if(OS.contains("windows") || OS.contains("Windows")){
		List<CommPortIdentifier> portIdentifiers = serialPortDiscoverer.listPorts();
		for (CommPortIdentifier portIdentifier : portIdentifiers) {
		    String portName = portIdentifier.getName();
		    if (!portNames.contains(portName)) {
		        portNames.add(portName);
		    }
		}
        }else{
        	PORTS.clear();
        	try{
			Files.walkFileTree(Paths.get("/dev"), new SerialPortRefresher.Visit());
		}catch(final Exception e){
			//LOGGER.writeError(e);
		}
		for(final String PORT : PORTS){
			portNames.add(PORT);
		}
        }
        return portNames;
    }
    
    private final static Collection<String> PORTS = new ArrayList<String>();
    
    public static class Visit extends SimpleFileVisitor<Path>{

		public FileVisitResult preVisitDirectory(final Path DIR, final BasicFileAttributes ATTR){
			//System.out.format("Scanning %s%n", DIR);
			if(DIR.toString().equals("/dev")){
				return FileVisitResult.CONTINUE;
			}else{
				return FileVisitResult.SKIP_SUBTREE;
			}
		}

		public FileVisitResult postVisitDirectory(final Path DIR, final IOException E) {
			//System.out.format("Scaned %s%n", DIR);
			return FileVisitResult.CONTINUE;
		}

		public FileVisitResult visitFileFailed(final Path FILE, final IOException E) {
			E.printStackTrace();
			return FileVisitResult.CONTINUE;
		}

		public FileVisitResult visitFile(final Path FILE, final BasicFileAttributes ATTR) {
			//if(ATTR.isRegularFile()){
				//System.out.format("Regular file: %s%n", FILE);
			//}
			if(FILE.toString().contains("ttyACM")){
				//System.out.println(FILE.toString());
				try{
					if(!PORTS.contains(FILE.toString())){
						PORTS.add(FILE.toString());
					}
				}catch(final Exception e){}
			}
			//System.out.println("(" + ATTR.size() + "bytes)");
			return FileVisitResult.CONTINUE;
		}

	}
}
