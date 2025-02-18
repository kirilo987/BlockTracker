# CoreProtect Glowing Wand

## Description
CoreProtect Glowing Wand is a Minecraft plugin for Paper 1.21 that allows players to interact with blocks and track the last player who modified them using CoreProtect. The selected block's information is stored in a custom wand, and players can use it to highlight the last known user with a glowing effect.

## Features
- **Custom Wand**: A special wand crafted from a bone and glowing ink sac.
- **Block Selection**: Right-click a block to store its last interacting player.
- **Player Highlighting**: Right-click a player to make them glow if they were the last one who modified the selected block.
- **Custom Model Support**: The wand can have a custom texture using resource packs.
- **Persistent Data**: Selected block information is stored in the item's metadata.

## Requirements
- Minecraft Server running **Paper 1.21**
- **CoreProtect** plugin installed and enabled

## Installation
1. Download the plugin JAR file.
2. Place it into the `plugins` folder of your Paper server.
3. Ensure **CoreProtect** is installed.
4. Restart your server.

## How to Use
### Crafting the Wand
The wand is crafted using:
- **1x Bone**
- **1x Glowing Ink Sac**
- **1x Glowstone Dust**

### Selecting a Block
1. Right-click a block with the wand.
2. The wand stores the block's type, coordinates, and the last player who interacted with it.
3. The information is displayed in the wand's lore.

### Highlighting a Player
1. Right-click a player with the wand.
2. If the player was the last one to interact with the selected block, they will glow for 5-10 seconds.

## Custom Textures
To apply a custom texture to the wand:
1. Create a **resource pack**.
2. Add a `custom_model_data` entry in the `item_model.json` file for `minecraft:bone`.
3. Set `custom_model_data: 1001` for the wand's texture.
4. Apply the resource pack to your server or client.

## Configuration
No additional configuration is required. The plugin automatically integrates with CoreProtect.

## Troubleshooting
- **Plugin Not Loading?**
  - Check if **CoreProtect** is installed and running.
  - Run `/plugins` and ensure `CoreProtect` and `BlockTracker` appear in green.

- **Wand Not Working?**
  - Ensure the wand is crafted correctly.
  - Check that CoreProtect is logging block interactions.
  
## License
This plugin is open-source and can be modified freely.

## Contact
For issues or feature requests, feel free to open a ticket on GitHub or contact the developer.

