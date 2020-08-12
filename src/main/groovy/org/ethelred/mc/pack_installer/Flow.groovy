/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import net.java.truevfs.access.TArchiveDetector
import net.java.truevfs.access.TConfig
import net.java.truevfs.access.TVFS
import net.java.truevfs.comp.zipdriver.ZipDriver

class Flow {
    UI ui

    void run() {
        init()
        try {
            // discover known targets
            def candidateTargets = Target.findCandidates()
            def targets = ui.confirmTarget(candidateTargets)

            // find sources
            def candidateSources = Source.findCandidates(targets)
            def sources = ui.confirmSource(candidateSources)

            def library = new Library()
            sources.each { it.findPacks(library) }
            ui.listPacks(library)

            targets.each { it.writePacks(library.dependencyGroups) }
        } finally {
            TVFS.umount()
        }
    }

    static void init() {
        // override file extensions treated as zip
        TConfig.current().setArchiveDetector(
                new TArchiveDetector(
                TArchiveDetector.NULL,
                "zip|mcpack|mcaddon", new ZipDriver()))
    }
}