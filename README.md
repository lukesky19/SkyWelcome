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
- `skywelcome.commands.skywelcome.reload` - The permission to use /skywelcome reload.
- `skywelcome.commands.skywelcome.help` - The permission to use /skywelcome help.
- `skywelcome.commands.skywelcome.toggle.join` - The permission to use /skywelcome toggle join.
- `skywelcome.commands.skywelcome.toggle.leave` - The permission to use /skywelcome toggle leave.
- `skywelcome.commands.skywelcome.toggle.quit` - The permission to use /skywelcome toggle quit.
- `skywelcome.commands.skywelcome.toggle.motd` - The permission to use /skywelcome toggle motd.
- `skywelcome.commands.skywelcome.gui.join` -  The permission to use /skywelcome gui join.
- `skywelcome.commands.skywelcome.gui.leave` - The permission to use /skywelcome gui leave.
- `skywelcome.commands.skywelcome.gui.quit` - The permission to use /skywelcome gui quit.

## Issues, Bugs, or Suggestions
* Please create a new [GitHub Issue](https://github.com/lukesky19/SkyWelcome/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkyWelcome and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.

## FAQ
Q: What versions does this plugin support?

A: 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, and 1.21.9.

Q: Are there any plans to support any other versions?

A: I will always do my best to support the latest versions of the game. I will sometimes support other versions until I no longer use them.

Q: Does this work on Spigot? Paper? (Insert other server software here)?

A: I only support Paper, but this will likely also work on forks of Paper (untested). There are no plans to support any other server software (i.e., Spigot or Folia).

## For Server Admins/Owners
* Download the plugin [SkyLib](https://github.com/lukesky19/SkyLib/releases).
* Download the plugin from the releases tab and add it to your server.

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
