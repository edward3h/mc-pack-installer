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
    static def DEFAULT_SEARCH_ROOTS = [
        new TPath(System.getProperty('user.home'), '/Library/Application Support/mcpelauncher/games/com.mojang'),
        // Mac OS mcpelauncher
        new TPath(System.getProperty('user.home'), '/.local/share/mcpelauncher/games/com.mojang'),
        // Linux mcpelauncher
        // TODO Windows 10
        new TPath('/opt/MC/bedrock'), // Linux BDS with MCscripts
    ]

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

    static List<Target> findCandidates() {
        DEFAULT_SEARCH_ROOTS.findAll { Files.isDirectory(it) }
        .collect { new Target(path:it)}
    }


}
