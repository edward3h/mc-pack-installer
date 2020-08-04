/* (C) 2020 Edward Harman */
package org.ethelred.mc

import org.ethelred.mc.pack.Pack
import org.ethelred.mc.pack_installer.*

class App implements UI {

    static void main(String... args) {
        def app = new App(args)
        def flow = new Flow(ui: app)
        flow.run()
    }

    App(String... args) {
    }

    @Override
    List<Target> confirmTarget(List<Target> targets) {
        targets.each { println it }
    }

    @Override
    List<Source> confirmSource(List<Source> sources) {
        sources.each { println it }
    }

    @Override
    List<Pack> listPacks(List<Pack> packs) {
        packs.each {println it}
    }
}
