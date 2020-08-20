# Docker

Run the app from docker like this:

    docker run -v $HOME:/home edward3h/mc-pack-installer:0.9.0

This mounts your home directory as `/home` inside the docker image. There are some limitations with this approach but it should work fine for the normal case of copying from your Downloads directory to the mcpelauncher install.
