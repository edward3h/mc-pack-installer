/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import net.java.truevfs.access.TArchiveDetector
import net.java.truevfs.access.TConfig
import net.java.truevfs.access.TVFS
import net.java.truevfs.comp.zipdriver.AbstractZipDriverEntry
import net.java.truevfs.comp.zipdriver.ZipDriver

class Flow {
    UI ui

    void run() {
        init()
        def targets = []
        try {
            // discover known targets
            def candidateTargets = Target.findCandidates(ui.config.targets)
            targets = ui.confirmTarget(candidateTargets)

            // find sources
            def candidateSources = Source.findCandidates(ui.config.sources)
            def sources = ui.confirmSource(candidateSources)

            def library = new Library()
            [targets, sources].flatten().each { it.findPacks(library, ui.config.skipPatterns) }
            ui.listPacks(library)

            targets.each {
                it.writePacks(library.dependencyGroups)
                it.writeWorlds(library.worlds)
            }
        } catch(e) {
            log.error "Uncaught exception in Flow", e
        } finally {
            TVFS.umount()
        }
        targets.each { it.finish() }
    }

    static void init() {
        // override file extensions treated as zip
        TConfig.current().setArchiveDetector(
                new TArchiveDetector(
                TArchiveDetector.NULL,
                "zip|mcpack|mcaddon|mcworld", new MyZipDriver()))
    }
}

class MyZipDriver extends ZipDriver {
    @Override
    protected boolean rdc(AbstractZipDriverEntry input, AbstractZipDriverEntry output) {
        return false
    }
}