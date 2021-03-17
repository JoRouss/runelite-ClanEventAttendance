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

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
class ClanEventAttendancePanel extends PluginPanel
{
    private final JButton startButton = new JButton();
    private final JButton copyTextButton = new JButton();

    private final JLabel textLabel = new JLabel();

    private static final String BTN_START_TEXT = "Start event";
    private static final String BTN_STOP_TEXT = "Stop event";
    private static final String BTN_COPY_TEXT_TEXT = "Copy text to clipboard";


    void init(ClanEventAttendanceConfig config, ClanEventAttendancePlugin plugin)
    {
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel topButtonsPanel = new JPanel();
        topButtonsPanel.setLayout(new BorderLayout());
        topButtonsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        startButton.setText(plugin.eventRunning ? BTN_STOP_TEXT : BTN_START_TEXT);
        startButton.setFocusable(false);

        topButtonsPanel.add(startButton, BorderLayout.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textPanel.setBorder(new EmptyBorder(0, 5, 0, 5));

        textLabel.setOpaque(false);
        textLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        textPanel.add(textLabel, BorderLayout.NORTH);

        JPanel bottomButtonsPanel = new JPanel();
        bottomButtonsPanel.setLayout(new BorderLayout());
        bottomButtonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        copyTextButton.setText(BTN_COPY_TEXT_TEXT);
        copyTextButton.setFocusable(false);

        bottomButtonsPanel.add(copyTextButton, BorderLayout.CENTER);

        add(topButtonsPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        add(bottomButtonsPanel, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (plugin.eventRunning)
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
                    final int result = JOptionPane.showOptionDialog(topButtonsPanel,
                            "Are you sure you want to START a new event?\nThis will delete current data.",
                            "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, new String[]{"Yes", "No"}, "No");

                    if(result == JOptionPane.YES_OPTION){
                        plugin.startEvent();
                    }
                }
            }
        });

        copyTextButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String text = textLabel.getText();
                text = text.replaceAll("(<br/>)", "\n");
                text = text.replaceAll("<[^>]*>", "");

                // Copy to clipboard
                StringSelection stringSelection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

                plugin.addChatMessage("Text copied to clipboard!");
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
        copyTextButton.setEnabled(config.getBlockCopyButtons() ? !plugin.eventRunning : true);
    }
}
