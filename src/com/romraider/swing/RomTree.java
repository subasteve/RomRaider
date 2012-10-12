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

package com.romraider.swing;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.romraider.editor.ecu.ECUEditor;

public class RomTree extends JTree implements MouseListener {

    private static final long serialVersionUID = 1630446543383498886L;
    public static ECUEditor container;

    public RomTree(DefaultMutableTreeNode input) {
        super(input);
        setRootVisible(false);
        setRowHeight(0);
        addMouseListener(this);
        setCellRenderer(new RomCellRenderer());
        setFont(new Font("Tahoma", Font.PLAIN, 11));
    }

    public ECUEditor getContainer() {
        return container;
    }

    public void setContainer(ECUEditor container) {
        RomTree.container = container;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        showHideTable(e);
    }

    private void showHideTable(MouseEvent e) {
        try {

            Object selectedRow = getPathForLocation(e.getX(), e.getY()).getLastPathComponent();

            if (e.getClickCount() >= container.getSettings().getTableClickCount() &&
                    selectedRow instanceof TableTreeNode) {

                TableTreeNode node = (TableTreeNode) selectedRow;

                //if (!(node.getTable().getUserLevel() > container.getSettings().getUserLevel())) {
                container.displayTable(node.getFrame());
                //}
            }

            if (selectedRow instanceof TableTreeNode || selectedRow instanceof CategoryTreeNode || selectedRow instanceof RomTreeNode)
            {
                Object lastSelectedPathComponent = getLastSelectedPathComponent();
                if(lastSelectedPathComponent instanceof TableTreeNode) {
                    TableTreeNode node = (TableTreeNode) getLastSelectedPathComponent();
                    container.setLastSelectedRom(node.getTable().getRom());
                } else if(lastSelectedPathComponent instanceof CategoryTreeNode) {
                    CategoryTreeNode node = (CategoryTreeNode) getLastSelectedPathComponent();
                    container.setLastSelectedRom(node.getRom());
                } else if(lastSelectedPathComponent instanceof RomTreeNode) {
                    RomTreeNode node = (RomTreeNode) getLastSelectedPathComponent();
                    container.setLastSelectedRom(node.getRom());
                }
            }
            container.getEditorMenuBar().updateMenu();
            container.getToolBar().updateButtons();
        } catch (NullPointerException ex) {
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void removeDescendantToggledPaths(Enumeration<TreePath> toRemove) {
        super.removeDescendantToggledPaths(toRemove);
    }
}