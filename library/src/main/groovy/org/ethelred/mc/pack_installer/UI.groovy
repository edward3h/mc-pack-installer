/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import java.util.regex.Pattern

interface UI {
    Config getConfig()

    Set<Target> confirmTarget(Set<Target> targets)

    Set<Source> confirmSource(Set<Source> sources)

    void listPacks(library)
}

interface Config {
    Set<Target> getTargets()

    Set<Source> getSources()

    Set<Pattern> getSkipPatterns()
}