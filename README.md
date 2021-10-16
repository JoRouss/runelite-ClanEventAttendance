Clan Event Attendance [![Plugin Installs](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/installs/plugin/clan-event-attendance)](https://runelite.net/plugin-hub/JoRouss) [![Plugin Rank](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/rank/plugin/clan-event-attendance)](https://runelite.net/plugin-hub)
=====================

A plugin used to generate a list of clan members attending an event.

It is designed so any clan member can take attendance at an event and post attendance to their clan's social media.

It tracks the event duration and for every clan member, the time spent at the event and how late they joined the event.

At the end of the event, you can copy/paste the attendance list to your clan's social media, like Discord.

![Discord report](./assets/ClanEventAttendance6.png "Discord report")

| NOTE: If your event takes place in multiple instances, you can have multiple members of your clan run the plugin and merge the resulting lists manually afterward |
| --- |

How to use
----------

1. Open the Clan Event Attendance panel.
2. Click "Start event". The plugin will start tracking clan members around you.  
![How to](./assets/howto1.PNG "How to")
3. When the event is over, click "Stop event".  
![How to](./assets/howto2.PNG "How to")
4. Click "Copy text to clipboard". This will place the text in your clipboard for you to paste it in your social media.  
![How to](./assets/howto3.png "How to")
5. Paste the result in your social media (ex.: Discord) and edit the event name and host if needed.  
![How to](./assets/howto4.png "How to")
6. Hit "Enter" to submit the event.  
![How to](./assets/howto5.png "How to")

Config
------

The config page allows to:
- Time threshold: Set a time threshold to consider a member part of the event
- Late threshold: Set a time threshold to consider a member late at the event
- Discord markdown: Surround the final list with multiline code blocks markdown for better Discord display
- Text prefix: Type a text block that will be added on top of the final result
- Text suffix: Type a text block that will be added at the bottom of the final result
- Present/Absent colors: The color to display members while the event is running
- Block copy button: Prevents copying the text while the event is still running

![Config Page](./assets/config2.png "Config Page")

Screenshots
-----------

![Panel](./assets/screen1.png "Panel")
![Event1](./assets/example1.jpg "Event1")
![Event2](./assets/example2.png "Event2")

License
-------
Clan Event Attendance is licensed under the BSD 2-Clause License License. See LICENSE for details.

Author
------
JoRouss