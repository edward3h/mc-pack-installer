package org.ethelred.mc.pack_installer

import net.java.truevfs.access.TPath

abstract class ConfigScript extends Script {
    DefaultConfig appConfig

    def setAppConfig(v) {
        appConfig = v
    }

    def target(String path) {
        appConfig.targets << new Target(path: path)
    }

    def source(String path) {
        appConfig.sources << new Source(path: new TPath(path))
    }

    def target(Closure block) {
        def t = new TargetBuilder().tap(block).build()
        if (t) {
            appConfig.targets << t
        }
    }
}