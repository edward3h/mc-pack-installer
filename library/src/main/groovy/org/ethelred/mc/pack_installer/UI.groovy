/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import java.util.regex.Pattern

interface UI {
    Config getConfig()

    List<Target> confirmTarget(List<Target> targets)

    List<Source> confirmSource(List<Source> sources)

    void listPacks(library)
}

interface Config {
    List<Target> getTargets()

    List<Source> getSources()

    List<Pattern> getSkipPatterns()
}