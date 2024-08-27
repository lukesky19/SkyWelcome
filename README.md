# SkyShop
## Description
* A plugin that allows players to toggle join, leave, MOTD messages, and to choose custom join and leave messages.
## Features
* Player togglable join, leave, and MOTD messages.
* Player selectable join and leave messages via a GUI.
* Custom permissions per join and leave message.
* Global toggle for join, leave, and MOTD messages.
## Soft Dependencies
* HeadDatabaseAPI
## Commands
- /skywelcome help - Displays the help message.
- /skywelcome reload - Reloads the plugin.
- /skyshop gui join - The GUI to select custom join messages.
- /skyshop gui leave - The GUI to select custom leave messages.
- /skyshop gui quit - The GUI to select custom leave messages.
## Permisisons
- `skyshop.commands.shop` - The permission to access the shop.
- `skyshop.commands.sellall` - The permission to access the sellall GUI.
- `skyshop.commands.reload` - The permission to reload the plugin.
## Issues, Bugs, or Suggestions
* Please create a new [Github Issue](https://github.com/lukesky19/SkyWelcome/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkyShop and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.
## FAQ
Q: What versions does this plugin support?

A: 1.21 and 1.21.1

Q: Are there any plans to support any other versions?

A: I will always support newer versions of the game. I have no plans to support any version older than 1.21 at this time.

Q: Does this work on Spigot and Paper?

A: This plugin only works with Paper, it makes use of many newer API features that don't exist in the Spigot API. There are no plans to support Spigot.

Q: Is Folia supported?

A: There is no Folia support at this time. I may look into it in the future though.

## Building
```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
