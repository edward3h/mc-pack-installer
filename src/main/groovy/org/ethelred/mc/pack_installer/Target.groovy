/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import groovy.transform.Canonical

import java.nio.file.Files
import java.nio.file.Path
/**
 * Target - place to install packs to i.e. client or server pack location
 */
@Canonical
class Target {
    static def DEFAULT_SEARCH_ROOTS = [
        Path.of(System.getProperty('user.home'), '/Library/Application Support/mcpelauncher/games/com.mojang'),
        // Mac OS mcpelauncher
        Path.of(System.getProperty('user.home'), '/.local/share/mcpelauncher/games/com.mojang'),
        // Linux mcpelauncher
        // TODO Windows 10
        Path.of('/opt/MC/bedrock'), // Linux BDS with MCscripts
    ]

    enum Type { LOCAL }

    Path path
    Type type

    static List<Target> findCandidates() {
        DEFAULT_SEARCH_ROOTS.findAll { Files.isDirectory(it) }
        .collect { new Target(path:it, type:Type.LOCAL)}
    }

}
