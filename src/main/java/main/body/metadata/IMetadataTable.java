package main.body.metadata;

import main.IPanelProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public interface IMetadataTable extends IPanelProvider
{
    JTable getTable();
    DefaultTableModel getTableModel();
}
