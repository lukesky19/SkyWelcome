# SkyWelcome
## Description
* A plugin that offers toggleable join, leave, and motd messages, the ability to custom join and leave messages, and welcome rewards.

## Features
* Player togglable join, leave, and MOTD messages.
* Player selectable join and leave messages via a GUI.
* Custom permissions per custom join and leave message.
* Global toggle for join, leave, and MOTD messages.
* HeadDatabaseAPI and PlaceholderAPI support.
* Welcome rewards for saying "welcome" to a new player.

## Dependencies
* HeadDatabaseAPI
* SkyLib

## Commands
- /skywelcome help - Displays the help message.
- /skywelcome reload - Reloads the plugin.
- /skywelcome toggle join - Toggles a player's individual join message.
- /skywelcome toggle quit - Toggles a player's individual quit message.
- /skywelcome toggle leave - Toggles a player's individual leave message.
- /skywelcome toggle motd - Toggles the player seeing the MOTD message.
- /skywelcome gui join - The GUI to select custom join messages.
- /skywelcome gui leave - The GUI to select custom leave messages.
- /skywelcome gui quit - The GUI to select custom quit messages.

## Permisisons
- `skywelcome.command.reload` - The permission to use /skywelcome reload.
- `skywelcome.command.help` - The permission to use /skywelcome help.
- `skywelcome.command.toggle.join` - The permission to use /skywelcome toggle join.
- `skywelcome.command.toggle.leave` - The permission to use /skywelcome toggle leave.
- `skywelcome.command.toggle.quit` - The permission to use /skywelcome toggle quit.
- `skywelcome.command.toggle.motd` - The permission to use /skywelcome toggle motd.
- `skywelcome.command.gui.join` -  The permission to use /skywelcome gui join.
- `skywelcome.command.gui.leave` - The permission to use /skywelcome gui leave.
- `skywelcome.command.gui.quit` - The permission to use /skywelcome gui quit.

## Issues, Bugs, or Suggestions
* Please create a new [GitHub Issue](https://github.com/lukesky19/SkyWelcome/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkyWelcome and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.

## FAQ
Q: What versions does this plugin support?

A: 1.21, 1.21.1, 1.21.2, 1.21.3

Q: Are there any plans to support any other versions?

A: I will always support newer versions of the game. I have no plans to support any version older than 1.21 at this time.

Q: Does this work on Spigot and Paper?

A: This plugin only works with Paper, it makes use of many newer API features that don't exist in the Spigot API. There are no plans to support Spigot.

Q: Is Folia supported?

A: There is no Folia support at this time. I may look into it in the future though.

## For Server Admins/Owners
* Download the plugin [SkyLib](https://github.com/lukesky19/SkyLib/releases).
* Download the plugin from the releases tab and add it to your server.

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
