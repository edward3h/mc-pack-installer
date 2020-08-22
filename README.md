# Minecraft Addon Pack Installer

For Minecraft Bedrock AKA MCPE, MCBE.

## What does it do?

Searches your computer for addons, then repacks them and copies them to the right place for Minecraft to use them. (It assumes you are using mcpelauncher to play Minecraft).

It can find packs in directories, zip, mcpack or mcaddon files.

## Installation

Depends on `java` 1.8 or higher, unless you run the Docker version - which depends on docker.

### Docker

See <packaging/docker/Readme.md>

### Mac OS (Homebrew)

Install with homebrew:

    brew install edward3h/tap/mc-pack-installer

### Build from source

Either download the release zip and unzip it, or git clone this repo, then:

    cd mc-pack-installer
    ./gradlew installDist
    export PATH=$PATH:$PWD/build/install/mc-pack-installer/bin