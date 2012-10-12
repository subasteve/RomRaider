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

import gnu.io.CommPortIdentifier;
import static gnu.io.CommPortIdentifier.PORT_SERIAL;
import static gnu.io.CommPortIdentifier.getPortIdentifiers;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collection;

import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

public final class SerialPortDiscovererImpl implements SerialPortDiscoverer {

	private final static Collection<String> PORTS = new ArrayList<String>();

    @SuppressWarnings({"unchecked"})
    public List<CommPortIdentifier> listPorts() {
    	List<CommPortIdentifier> serialPortIdentifiers = new ArrayList<CommPortIdentifier>();
    	final String OS = System.getProperty("os.name");
    	if(OS.contains("windows") || OS.contains("Windows")){
		Enumeration<CommPortIdentifier> portEnum = getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
		    CommPortIdentifier portIdentifier = portEnum.nextElement();
		    if (portIdentifier.getPortType() == PORT_SERIAL) {
		        serialPortIdentifiers.add(portIdentifier);
		    }
		}
        }else{
        	PORTS.clear();
        	try{
			Files.walkFileTree(Paths.get("/dev"), new SerialPortDiscovererImpl.Visit());
		}catch(final Exception e){
			//LOGGER.writeError(e);
		}
		for(final String PORT : PORTS){
			try{
				serialPortIdentifiers.add(CommPortIdentifier.getPortIdentifier(PORT));
			}catch(final Exception e){}
		}
        }
        return serialPortIdentifiers;
    }
    
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
