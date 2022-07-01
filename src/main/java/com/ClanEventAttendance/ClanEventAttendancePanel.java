/*
BSD 2-Clause License

Copyright (c) 2021, Jonathan Rousseau <https://github.com/JoRouss>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ClanEventAttendance;

import com.ClanEventAttendance.config.OutputFormat;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;

@Slf4j
class ClanEventAttendancePanel extends PluginPanel
{
    private final JButton startButton = new JButton();
    private final JButton copyTextButton = new JButton();
    private final JLabel textLabel = new JLabel();
    private final JPanel topButtonsPanel = new JPanel();
    private final JPanel textPanel = new JPanel();
    private final JPanel bottomButtonsPanel = new JPanel();

    private static final String BTN_START_TEXT = "Start Event";
    private static final String BTN_STOP_TEXT = "Stop Event";
    private static final String BTN_COPY_TEXT_TEXT = "Copy to Clipboard";


    void init(ClanEventAttendanceConfig config, ClanEventAttendancePlugin plugin)
    {
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        topButtonsPanel.setLayout(new BorderLayout(0, 10));
        topButtonsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        startButton.setText(plugin.eventRunning ? BTN_STOP_TEXT : BTN_START_TEXT);
        startButton.setFocusable(false);

        topButtonsPanel.add(startButton, BorderLayout.CENTER);

        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textPanel.setBorder(new EmptyBorder(0, 5, 0, 5));

        textLabel.setOpaque(false);
        textLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        textPanel.add(textLabel, BorderLayout.NORTH);

        bottomButtonsPanel.setLayout(new BorderLayout());
        bottomButtonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        copyTextButton.setText(BTN_COPY_TEXT_TEXT);
        copyTextButton.setFocusable(false);

        add(topButtonsPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        if (config.topCopyButton())
        {
            topButtonsPanel.add(copyTextButton, BorderLayout.SOUTH);
        }
        else
        {
            bottomButtonsPanel.add(copyTextButton, BorderLayout.CENTER);
            add(bottomButtonsPanel, BorderLayout.SOUTH);
        }

        if (startButton.getActionListeners().length > 0)
        {
            startButton.removeActionListener(startButton.getActionListeners()[0]);
        }

        startButton.addActionListener(e ->
        {
            if (plugin.eventRunning)
            {
                if (config.confirmationMessages())
                {
                    final int result = JOptionPane.showOptionDialog(topButtonsPanel,
                            "Are you sure you want to TERMINATE the event?\nYou won't be able to restart it.",
                            "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, new String[]{"Yes", "No"}, "No");

                    if(result == JOptionPane.YES_OPTION){
                        plugin.stopEvent();
                    }
                }
                else
                {
                    plugin.stopEvent();
                }
            }
            else
            {
                if (config.confirmationMessages())
                {
                    final int result = JOptionPane.showOptionDialog(topButtonsPanel,
                            "Are you sure you want to START a new event?\nThis will delete current data.",
                            "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, new String[]{"Yes", "No"}, "No");

                    if (result == JOptionPane.YES_OPTION)
                    {
                        plugin.startEvent();
                    }
                }
                else
                {
                    plugin.startEvent();
                }
            }
        });

        copyTextButton.addActionListener(e ->
        {
            String text = textLabel.getText();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            if (config.outputFormat() == OutputFormat.PNG)
            {
                BufferedImage image = createImage(textLabel);
                TransferableImage trans = new TransferableImage(image);

                // Copied PNG image to clipboard
                clipboard.setContents(trans, null);
            }
            else
            {
                text = text.replaceAll("(<br/>)", "\n");
                text = text.replaceAll("<[^>]*>", "");
                StringSelection stringSelection = new StringSelection(text);

                // Copied text to clipboard
                clipboard.setContents(stringSelection, null);
            }
        });

        updatePanel(config, plugin);
    }

    void setText(String data)
    {
        textLabel.setText(data);
    }

    void updatePanel(ClanEventAttendanceConfig config, ClanEventAttendancePlugin plugin)
    {

        startButton.setText(plugin.eventRunning ? BTN_STOP_TEXT : BTN_START_TEXT);
        copyTextButton.setEnabled(!config.blockCopyButton() || !plugin.eventRunning);
    }

    public BufferedImage createImage(JLabel label) {

        int w = label.getWidth();
        int h = label.getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        label.paint(g);
        g.dispose();
        return bi;
    }

    private static class TransferableImage implements Transferable {

        Image i;

        public TransferableImage( Image i ) {
            this.i = i;
        }

        public Object getTransferData( DataFlavor flavor )
                throws UnsupportedFlavorException
        {
            if ( flavor.equals( DataFlavor.imageFlavor ) && i != null ) {
                return i;
            }
            else {
                throw new UnsupportedFlavorException( flavor );
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[ 1 ];
            flavors[ 0 ] = DataFlavor.imageFlavor;
            return flavors;
        }

        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (DataFlavor dataFlavor : flavors)
            {
                if (flavor.equals(dataFlavor))
                {
                    return true;
                }
            }

            return false;
        }
    }
}
