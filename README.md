# Minecraft Addon Pack Installer

For Minecraft Bedrock AKA MCPE, MCBE.

## What does it do?

Searches your computer for addons, then repacks them and copies them to the 
right place for Minecraft to use them. (It assumes you are using mcpelauncher 
to play Minecraft).

It can find packs in directories, zip, mcpack or mcaddon files.

## Installation

Depends on `java` 1.8 or higher, unless you run the Docker version - which 
depends on docker.

### Docker

See [my Docker Readme](packaging/docker/Readme.md)

### Mac OS (Homebrew)

Install with [homebrew](https://brew.sh/):

    brew install edward3h/tap/mc-pack-installer

### Build from source

Either download the release zip and unzip it, or git clone this repo, then:

    cd mc-pack-installer
    ./gradlew installDist
    export PATH=$PATH:$PWD/build/install/mc-pack-installer/bin
    
Or link the file `build/install/mc-pack-installer/bin/mc-pack-installer` 
where-ever in the PATH works for you.

### Windows

See [issue #2](https://github.com/edward3h/mc-pack-installer/issues/2). TL;DR 
I don't have a Windows machine so I'm looking for help.

## Running

    mc-pack-installer
    
to run the default behaviour. This searches for packs in all _sources_ and 
_targets_ and copies packs into _targets_ if they aren't already there.

The default _source_ is the user's `Downloads` directory.

The default _target_ is the default location of the 
[mcpelauncher](https://mcpelauncher.readthedocs.io/en/latest/) files for the
user. (Linux and Mac OS)

    mc-pack-installer --help
    
to show all options.

### Configuration
Additional _sources_ and _targets_ can be specified with the `--source` and 
`--target` options. They can be repeated to add more.

Alternatively you can add them in a config file.

On Mac OS the default location for the config file is `$HOME/Library/Application Support/org.ethelred.mc-pack-installer/config.groovy`

On Linux the default location is `$HOME/.config/mc-pack-installer/config.groovy`

A different config file can be specified using the `--config` option.

The configuration file contains lines like:

    target "$mcpelauncher/path/to/target"
    source "$HOME/path/to/source"
    
The `$mcpelauncher` and `$HOME` variables are the only variables currently 
supported.

#### Web Target
One additional type of target is supported by the config file, specified like:

    target {
      type "web"
      path "$HOME/temp/mcpacks"
    }
    
Instead of writing packs to the Minecraft game, it generates a web page listing 
with download links for the packs. _(At home we use this to make it easier for 
my kids to download packs to their iPads)_

## Contributing
Contributions are welcome!

See [Contributing](docs/Contributing.md).

