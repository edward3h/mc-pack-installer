package org.ethelred.mc.pack_installer

import net.java.truevfs.access.TArchiveDetector
import net.java.truevfs.access.TConfig
import net.java.truevfs.comp.zipdriver.ZipDriver

class Flow {
    UI ui

    void run() {
        init()

        // discover known targets
        def candidateTargets = Target.findCandidates()
        def targets = ui.confirmTarget(candidateTargets)

        // find sources
        def candidateSources = Source.findCandidates(targets)
        def sources = ui.confirmSource(candidateSources)

        def foundPacks = sources.collectMany { it.findPacks() }
        ui.listPacks(foundPacks)
    }

    static void init() {
        // override file extensions treated as zip
        TConfig.get().setArchiveDetector(
                new TArchiveDetector(
                        TArchiveDetector.NULL,
                        "zip|mcpack|mcaddon", new ZipDriver()))
    }
}