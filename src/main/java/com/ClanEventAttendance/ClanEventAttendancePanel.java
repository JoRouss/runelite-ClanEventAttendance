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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
class ClanEventAttendancePanel extends PluginPanel
{
    private final JTextArea textArea = new JTextArea();
    private final JButton startButton = new JButton();

    void init(ClanEventAttendanceConfig config, ClanEventAttendancePlugin plugin)
    {
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel startButtonContainer = new JPanel();
        startButtonContainer.setLayout(new BorderLayout());
        startButtonContainer.setBorder(new EmptyBorder(0, 0, 10, 0));

        startButton.setText(plugin.eventRunning ? "Stop event" : "Start event");

        startButtonContainer.add(startButton, BorderLayout.CENTER);

        JPanel textAreaContainer = new JPanel();
        textAreaContainer.setLayout(new BorderLayout());
        textAreaContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textAreaContainer.setBorder(new EmptyBorder(5, 5, 5, 5));

        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));

        textAreaContainer.add(textArea, BorderLayout.CENTER);

        add(startButtonContainer, BorderLayout.NORTH);
        add(textAreaContainer, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (plugin.eventRunning)
                {
                    final int result = JOptionPane.showOptionDialog(startButtonContainer,
                            "Are you sure you want to TERMINATE the event?\nYou won't be able to restart it.",
                            "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, new String[]{"Yes", "No"}, "No");

                    if(result == JOptionPane.YES_OPTION){
                        plugin.stopEvent();
                    }
                }
                else
                {
                    final int result = JOptionPane.showOptionDialog(startButtonContainer,
                            "Are you sure you want to START a new event?\nThis will delete current data.",
                            "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, new String[]{"Yes", "No"}, "No");

                    if(result == JOptionPane.YES_OPTION){
                        plugin.startEvent();
                    }
                }

                startButton.setText(plugin.eventRunning ? "Stop event" : "Start event");
            }
        });
    }

    void setText(String data)
    {
        textArea.setText(data);
    }
}
