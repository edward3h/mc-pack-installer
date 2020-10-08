package org.ethelred.mc.pack_installer

class TargetBuilder {
    String path
    String type
    String remote

    Target build() {
        if (path) {
            if (type && "web".equalsIgnoreCase(type)) {
                return new WebTarget(path: path, remote: remote)
            } else {
                if (remote) {
                    log.warn "Property \"remote\" is not supported for standard target, ignoring"
                }
                return new Target(path: path)
            }
        } else {
            log.error("target closure did not set path")
        }
    }

    def path(v) {
        this.path = v
    }

    def type(v) {
        this.type = v
    }

    def remote(v) {
        this.remote = v
    }
}