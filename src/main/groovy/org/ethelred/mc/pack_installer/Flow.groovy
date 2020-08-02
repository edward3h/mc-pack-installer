package org.ethelred.mc.pack_installer

class Flow {
    UI ui

    void run() {
        // discover known targets
        def candidateTargets = Target.findCandidates()
        def targets = ui.confirmTarget(candidateTargets)

        // find sources
        def candidateSources = Source.findCandidates()
        def sources = ui.confirmSource(candidateSources)

        def foundPacks = sources.collectMany { it.findPacks() }
        ui.listPacks(foundPacks)
    }
}