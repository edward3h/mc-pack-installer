/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

interface UI {
    Config getConfig()

    List<Target> confirmTarget(List<Target> targets)

    List<Source> confirmSource(List<Source> sources)

    void listPacks(library)
}

interface Config {
    List<Target> getTargets()

    List<Source> getSources()
}