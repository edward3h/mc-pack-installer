/* (C) 2020 Edward Harman */
package org.ethelred.mc

import org.ethelred.mc.pack_installer.Flow
import org.ethelred.mc.pack_installer.Source
import org.ethelred.mc.pack_installer.Target
import org.ethelred.mc.pack_installer.UI
import org.ethelred.mc.pack_installer.WebTarget
import org.slf4j.LoggerFactory

import ch.qos.logback.core.util.StatusPrinter

class App implements UI {

    static void main(String... args) {
        StatusPrinter.print(LoggerFactory.ILoggerFactory)
        def app = new App(args)
        def flow = new Flow(ui: app)
        flow.run()
    }

    App(String... args) {
    }

    @Override
    List<Target> confirmTarget(List<Target> targets) {
        targets << new WebTarget(System.getProperty('user.home'), "sites", "mcpacks")
        targets.each { println it }
    }

    @Override
    List<Source> confirmSource(List<Source> sources) {
        sources.each { println it }
    }

    @Override
    void listPacks(library) {
        //        library.dependencyGroups.each { g ->
        //            g.each { println it.toStringShort() }
        //            println '---'
        //        }
    }
}
