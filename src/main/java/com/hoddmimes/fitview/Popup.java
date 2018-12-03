package com.hoddmimes.fitview;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Popup extends JDialog {

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mContentPane = new JPanel();
        mContentPane.setLayout(new GridLayoutManager(2, 1, new Insets(15, 15, 5, 15), -1, -1));
        mButtonPanel = new JPanel();
        mButtonPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mContentPane.add(mButtonPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        mOKButton = new JButton();
        mOKButton.setText("OK");
        mButtonPanel.add(mOKButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        mButtonPanel.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mButtonPanel.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        mMessagePanel = new JPanel();
        mMessagePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mContentPane.add(mMessagePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mTextPanel = new JTextPane();
        mTextPanel.setContentType("text/plain");
        mTextPanel.setEditable(false);
        Font mTextPanelFont = this.$$$getFont$$$("Arial", Font.PLAIN, 14, mTextPanel.getFont());
        if (mTextPanelFont != null) mTextPanel.setFont(mTextPanelFont);
        mTextPanel.setText("");
        mMessagePanel.add(mTextPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mContentPane;
    }

    public static enum SEVERITY {PLAIN, INFO, WARNING, ERROR}

    ;

    private JPanel mContentPane;
    private JButton mOKButton;
    private JPanel mButtonPanel;
    private JTextPane mTextPanel;
    private JPanel mMessagePanel;
    private JDialog mSelf;


    private Popup() {
        setContentPane(mContentPane);
        setModal(true);
        getRootPane().setDefaultButton(mOKButton);
        mOKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Popup.this.mSelf.dispose();
            }
        });
    }

    public static void showError(String pTitle, String pMessage, Component pParent) {
        Popup.show(SEVERITY.ERROR, pTitle, pMessage, 0, pParent);
    }

    public static void showError(String pTitle, String pMessage) {
        Popup.show(SEVERITY.ERROR, pTitle, pMessage, 0, null);
    }

    public static void show(SEVERITY pSeverity, String pTitle, String pMessage, long pToastTime) {
        Popup.show(pSeverity, pTitle, pMessage, 0, null);
    }

    public static void show(SEVERITY pSeverity, String pTitle, String pMessage, long pToastTime, Component pParent) {
        Popup tDialog = new Popup();
        String tTitle = null;

        if (pSeverity == SEVERITY.ERROR) {
            tDialog.mContentPane.setBackground(Color.RED);
            tDialog.mButtonPanel.setBackground(Color.RED);
            tTitle = (pTitle == null) ? "ERROR" : "ERROR: " + pTitle;
        }
        if (pSeverity == SEVERITY.WARNING) {
            tDialog.mContentPane.setBackground(Color.YELLOW);
            tDialog.mButtonPanel.setBackground(Color.YELLOW);
            tTitle = (pTitle == null) ? "Warning" : "Warning: " + pTitle;
        }

        if (pSeverity == SEVERITY.INFO) {
            tDialog.mContentPane.setBackground(new Color(227, 227, 227));
            tDialog.mButtonPanel.setBackground(new Color(227, 227, 227));
            tTitle = (pTitle == null) ? "Information" : "Information: " + pTitle;
        }

        if (pSeverity == SEVERITY.PLAIN) {
            tTitle = (pTitle == null) ? null : pTitle;
        }

        if (tTitle != null) {
            tDialog.setTitle(tTitle);
        }

        tDialog.mTextPanel.setText(pMessage);

        if (pParent != null) {
            Point tPos = pParent.getLocation();
            Dimension tDim = pParent.getSize();
            tDialog.setLocation(tPos.x + (tDim.width / 4), tPos.y + (tDim.height / 4));
        } else {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int tWidth = gd.getDisplayMode().getWidth();
            int tHeight = gd.getDisplayMode().getHeight();
            tDialog.setLocation((tWidth / 2), (tHeight / 2));
        }
        Point tPos = tDialog.getParent().getLocation();
        Dimension tDim = tDialog.getParent().getSize();


        tDialog.mSelf = tDialog;
        tDialog.pack();
        if (pToastTime > 0) {
            tDialog.mButtonPanel.setVisible(false);
            DialogCloseThread tCloseThread = new DialogCloseThread(tDialog, pToastTime);
            tCloseThread.start();
        }
        tDialog.setVisible(true);
    }


    public static void main(String[] args) {
        Popup.show(SEVERITY.INFO, "POP-Title", "det är en test  rad två och rad tre " +
                "detta är mer text fast på nästa rad. ", 0);
        System.exit(0);
    }

    static class DialogCloseThread extends Thread {
        JDialog mDialog;
        long mDismissTime;

        DialogCloseThread(JDialog pDialog, long pDismissTime) {
            mDialog = pDialog;
            mDismissTime = pDismissTime;
        }

        public void run() {
            try {
                Thread.sleep(mDismissTime);
            } catch (InterruptedException ie) {
            }
            mDialog.dispose();
        }

    }
}
