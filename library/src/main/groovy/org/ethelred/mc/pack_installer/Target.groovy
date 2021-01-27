/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import groovy.transform.EqualsAndHashCode
import net.java.truevfs.access.TFile
import net.java.truevfs.access.TVFS
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
            writeResource(pack, getPackRoot(pack.type, pack.isDevelopment()))
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

    boolean exists() {
        Files.isDirectory(path)
    }

    static Set<Target> findCandidates(Set<Target> configTargets) {
        configTargets.findAll { it.exists() }
    }

    void writeWorlds(worlds) {
        // no-op for normal target
    }

    /**
     * implied interface of resource: zipName, path, toStringShort()
     * @param resource
     * @param targetPath
     */
    void writeResource(resource, TPath targetPath, String targetExt = ".mcpack") {
        try {
            if (!Files.exists(targetPath)) Files.createDirectories(targetPath)

//            ".zip .mcpack .mcworld".split().each { ext ->
//                _tryDelete(targetPath.resolve(resource.zipName + ext))
//            }

            TFile targetRoot = targetPath.resolve(resource.zipName + targetExt).toFile()
            TFile sourceRoot = resource.path.toFile()
            sourceRoot.cp_r(targetRoot)
            TVFS.sync(targetRoot)
            log.info "Added ${resource.toStringShort()} under $targetPath"
        } catch(e) {
            log.error($/Failed to write resource ${resource.toStringShort()}:
source: ${resource.path}
target: $targetPath
message: ${e.message}/$, e)
//            System.exit 1
        }
    }

     void _tryDelete(TPath tPath)  {
        try {
            if (Files.exists(tPath)) {
                TFile tFile = tPath.toFile()
                tFile.rm_r()
                TVFS.sync(tFile)
            }
        } catch(e) {
            log.error("Failed to delete path $tPath", e)
            System.exit 1
        }
    }
}
