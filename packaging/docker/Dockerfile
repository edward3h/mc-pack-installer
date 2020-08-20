FROM openjdk:8-alpine
COPY bin bin/
COPY lib lib/
VOLUME /home
ENV MC_PACK_INSTALLER_OPTS="-Duser.home=/home"
ENTRYPOINT [ "/bin/mc-pack-installer" ]