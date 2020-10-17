package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack_installer.Target
import org.ethelred.mc.pack_installer.WebTarget

class SourceBuilder {
    String path
    boolean development

    Source build() {
        if (path) {
            return new Source(path: path, development: development)
        } else {
            log.error("source closure did not set path")
        }
    }

    def path(v) {
        this.path = v
    }

    def development(v) {
        this.development = v
    }
}