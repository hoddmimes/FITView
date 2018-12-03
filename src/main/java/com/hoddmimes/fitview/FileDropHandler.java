package com.hoddmimes.fitview;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

final class FileDropHandler extends TransferHandler
{
    public static final int DROP_EVENT = 0x4711;

    JTextField mInputFileTextField;

    public FileDropHandler( JTextField pInputFileTextField ) {
        mInputFileTextField = pInputFileTextField;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        for (DataFlavor flavor : support.getDataFlavors()) {
            if (flavor.isFlavorJavaFileListType()) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferSupport support) {
        java.util.List<File> tFiles;

        if (!this.canImport(support)) {
            return false;
        }
        try {
            tFiles = (java.util.List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        } catch (UnsupportedFlavorException | IOException ex) {
            // should never happen (or JDK is buggy)
            return false;
        }

        if (tFiles.size() != 1) {
            Popup.showError("Incorrect input file", "Select just one file when dropping to the infile field");
            return false;
        }

        try {
            mInputFileTextField.setText( tFiles.get(0).getCanonicalPath());
            ActionListener[] tListeners = mInputFileTextField.getActionListeners();
            ActionEvent tEvent = new ActionEvent( mInputFileTextField,  DROP_EVENT, "NEW_DROP_FILE" );

            for( ActionListener al : tListeners ) {
                al.actionPerformed( tEvent );
            }
        }
        catch( IOException e) {
            return false;
        }
        return true;
    }
}
