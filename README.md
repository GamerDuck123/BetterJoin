# Better Join Plugin
> **⚠️ Warning: Early Access**    
> The game Hytale is in early access, and so is this project! Features may be
> incomplete, unstable, or change frequently. Please be patient and understanding as development
> continues.

A simple yet very customizable welcome/join/leave message plugin

## Introduction
This plugin is built around fixing the annoying world joined messages and replacing them with
a global join and leave messaging system, as well as a simple welcome message. You can utilize a
simple chat message with colors, or use the fancy title system built into Hytale, the choice is yours!

## Pictures
### Title join message
![images/img.png](images/img.png)

### Chat Join Mesage
TODO

## Commands
There is only one command:

```/betterjoin``` - ```betterjoin.reload``` - A simple command that reloads the config to update any changes you may make without restarting.

## The Default Config
As of right now there is a simple config.json file under the mods folder:
> **Warning: Subject to Change**   
> I have built this config system to be updatable without changing user inputs so in future updates there will be things removed and added but no need for you to lose progress
```json
{
  "WelcomeMessage": [
    "Welcome new player!",
    "{player}"
  ],
  "JoinMessage": [
    "Someone joined the game",
    "{player}"
  ],
  "LeaveMessage": [
    "Someone left the game",
    "{player}"
  ],
  "DisableJoinMessages": true,
  "UseTitles": true,
  "MessageReloaded": "&aConfiguration reloaded successfully!",
  "NoPermission": "&cYou don't have permission to use this command!"
}
```
### WelcomeMessage
>**Warning**
> If UseTitles is enabled WelcomeMessage needs a **minimum** of 2 lines

This is a simple mulilined string that allows you to input multiple lines. If you are using UseTitles the first line will always be the header, while any other line will be the body of the title.
### JoinMessage
> If UseTitles is enabled JoinMessage needs a **minimum** of 2 lines

This is a simple mulilined string that allows you to input multiple lines. If you are using UseTitles the first line will always be the header, while any other line will be the body of the title.

### LeaveMessage
> If UseTitles is enabled LeaveMessage needs a **minimum** of 2 lines

This is a simple mulilined string that allows you to input multiple lines. If you are using UseTitles the first line will always be the header, while any other line will be the body of the title.

### DisableJoinMessages
This is a simple boolean value that disables the default world join/leave messages built into Hytale. This isn't required to be enabled for the plugin to work.

### UseTitles
>**Warning**
> If UseTitles is enabled WelcomeMessage, JoinMessage, and LeaveMessage need a **minimum** of 2 lines

If enabled it will use the fancy title system built directly into Hytale instead of using the chat, this is nice for looks however the messages won't stick around so if someone is afk and misses it, they won't know someone joined. 
### MessageReloaded
This is a simple onelined string that is sent when the config reload command is sent and the config is reloaded successfully. 
### NoPermission
This is a simple onelined string that is sent whenever someone tries to run the reload command without being op or having the permission ```betterjoin.reload```