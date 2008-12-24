/*
 * RomRaider Open-Source Tuning, Logging and Reflashing
 * Copyright (C) 2006-2008 RomRaider.com
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

package com.romraider.logger.utec.plugin;

import com.romraider.logger.ecu.external.ExternalDataItem;
import com.romraider.logger.utec.gui.mapTabs.UtecDataManager;

public class LoadExternalDataItem implements ExternalDataItem {

    public String getName() {
        return "TXS LOAD";
    }

    public String getDescription() {
        return "TXS Utec Load";
    }

    public String getUnits() {
        return "n/a";
    }

    public double getData() {
        return UtecDataManager.getLoadData();
    }

}