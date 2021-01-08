/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import groovy.transform.EqualsAndHashCode
import org.ethelred.mc.pack.Pack
import org.ethelred.mc.pack.PackType

import groovy.transform.Canonical

import java.nio.file.Files
import java.nio.file.Path

import net.java.truevfs.access.TPath
/**
 * Target - place to install packs to i.e. client or server pack location
 */
@EqualsAndHashCode(callSuper = true)
class Target extends Location {

    void writePacks(List<List<PackInstances>> lists) {
        lists.each { l ->
            l.each { pack ->
                writePack pack
            }
        }
    }

    void finish() {
        // no-op for extension
    }

    void writePack( pack) {
        if (!matches(pack)) {
            return
        }
        if (!pack.hasSource()) {
            return
        }
        if (pack.isDevelopment() || !pack.isInstalled(this)) {
            pack.writeUnder(getPackRoot(pack.type, pack.isDevelopment()))
        }
    }

    Path getPackRoot(type, dev) {
        switch (type) {
            case PackType.RESOURCE:
                return path.resolve(dev ? "development_resource_packs" : "resource_packs")
            case PackType.BEHAVIOR:
                return path.resolve(dev ? "development_behavior_packs" :"behavior_packs")
            case PackType.SKIN:
                return path.resolve("skin_packs")
            default:
                throw new IllegalArgumentException("Unknown pack type $type")
        }
    }

    static Set<Target> findCandidates(Set<Target> configTargets) {
        configTargets.findAll { Files.isDirectory(it.path) }
    }
}
