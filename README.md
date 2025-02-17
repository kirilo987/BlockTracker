# BlockTracker

**BlockTracker** is a Minecraft plugin that allows players to track who placed a specific block in the game. It uses the **CoreProtect API** to retrieve block history and provides a convenient tool for analyzing block ownership.

## Key Features

- **Block Tracking**: Players can use a special stick ("Block Tracker") to find out who placed a specific block.
- **Player Highlighting**: If the player who placed the block is online, they will be highlighted temporarily.
- **Custom Item**: The plugin provides a custom stick item that is used to track blocks.
- **Integration with CoreProtect**: The plugin relies on CoreProtect to fetch block history data.

## How It Works

1. **Block Tracker Stick**: Players can use the custom "Block Tracker" stick to interact with a block.
2. **Block Lookup**: When a player right-clicks a block with the stick, the plugin queries CoreProtect to find out who placed the block.
3. **Player Highlighting**: If the player who placed the block is online, their name is highlighted in the game for a few seconds.
4. **Messages**: The plugin sends messages to the player, indicating the owner of the block or if the data is unavailable.

## Usage

1. **Give the Block Tracker Stick**:
   - Use the `/give` command or a similar method to provide players with the custom "Block Tracker" stick.
   - The stick is created using the `createTrackerStick()` method in the plugin.

2. **Track a Block**:
   - Right-click any block while holding the "Block Tracker" stick.
   - The plugin will display the name of the player who placed the block (if available).

3. **Player Highlighting**:
   - If the player who placed the block is online, their name will be highlighted in yellow for a short duration.

## Dependencies

- **CoreProtect**: This plugin requires CoreProtect to be installed on the server. CoreProtect is used to fetch block history data.

## Code Overview

### Main Class: `BlockTracker.java`

- **`onEnable()`**: Registers events and checks for CoreProtect availability. If CoreProtect is not found, the plugin disables itself.
- **`onBlockInteract(PlayerInteractEvent event)`**: Handles the interaction event when a player right-clicks a block with the "Block Tracker" stick.
- **`isTrackerStick(ItemStack item)`**: Checks if the item in the player's hand is the custom "Block Tracker" stick.
- **`createTrackerStick()`**: Creates and returns the custom "Block Tracker" stick with a unique identifier.
- **`highlightPlayer(Player player)`**: Temporarily highlights the player's name in the game.
- **`getCoreProtect()`**: Retrieves the CoreProtect API instance.

## Installation

1. Download the plugin `.jar` file.
2. Place the `.jar` file in your server's `plugins` folder.
3. Ensure **CoreProtect** is installed and running on your server.
4. Restart the server.

## Commands

Currently, the plugin does not provide any commands. Players interact with blocks using the "Block Tracker" stick.

## Configuration

The plugin does not require any configuration files. It works out of the box with CoreProtect.

## Contributing

Feel free to contribute to this project by submitting issues or pull requests on GitHub.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Enjoy tracking blocks and uncovering the history of your Minecraft world with **BlockTracker**!
