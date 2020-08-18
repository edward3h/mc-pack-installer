/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import org.ethelred.mc.pack.Pack
import org.ethelred.mc.pack.PackType

import groovy.transform.Canonical

import java.nio.file.Files
import java.nio.file.Path

import net.java.truevfs.access.TPath
/**
 * Target - place to install packs to i.e. client or server pack location
 */
@Canonical
class Target {
     Target(String path) {
         this.path = new TPath(path)
     }

    Target(Path path) {
        this.path = path
    }

    void writePacks(List<List<Pack>> lists) {
        lists.each { l ->
            l.each { pack ->
                writePack pack
            }
        }
    }

    void writePack( pack) {
        if (!pack.isIn(path)) {
            pack.writeUnder(getPackRoot(pack.type))
        }
    }

    Path path

    Path getPackRoot(type) {
        switch (type) {
            case PackType.RESOURCE:
                return path.resolve("development_resource_packs")
            case PackType.BEHAVIOR:
                return path.resolve("development_behavior_packs")
            case PackType.SKIN:
                return path.resolve("skin_packs")
            default:
                throw new IllegalArgumentException("Unknown pack type $type")
        }
    }

    static List<Target> findCandidates(List<Target> configTargets) {
        configTargets.findAll { Files.isDirectory(it.path) }
    }


}
