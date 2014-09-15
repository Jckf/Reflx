# Reflx readme

## What is it?

Reflx is a Minecraft server wrapper (and API), but not in the traditional sense. Whereas other wrappers monitor the server process, and/or provide proxy functionality for altering some of the server's behavior, we do neither. Reflx instead loads the Minecraft server into itself, starts it in the same process, and uses Java reflection to attach to its internals. Using this method we expose an abstracted API that plugin developers can use to alter the game's functionality.

## Why this approach?

Using reflections to attach to the official server means that we don't have to include any of Mojang's code in our repository or artifacts, which would violate their EULA. It also means that game updates (even snapshots!) can be supported by simply providing updated class, field, and method mappings.

## Isn't this exactly what [Granite](https://github.com/GraniteTeam) is doing?

Yes! In fact, this project started in dialog with them, and we frequently exchange ideas :) But differences in implementation separate the two projects.

## Building and running

1. `git clone https://github.com/Jckf/Reflx.git`
2. `cd Reflx`
3. `mvn`
4. `./test.sh`
