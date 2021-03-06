/* (C) 2020 Edward Harman */
package org.ethelred.mc


import org.ethelred.mc.pack_installer.DefaultConfig
import org.ethelred.mc.pack_installer.Config
import org.ethelred.mc.pack_installer.Flow
import org.ethelred.mc.pack_installer.Source
import org.ethelred.mc.pack_installer.Target
import org.ethelred.mc.pack_installer.UI
import picocli.CommandLine


import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.regex.Pattern

@CommandLine.Command(name = "mc-pack-installer", mixinStandardHelpOptions = true, version = "1.0")
class App implements UI, Callable<Integer> {

    static void main(String... args) {
        new CommandLine(new App()).execute(args)
    }

    @CommandLine.Option(names = ["--config", "-c"], description = "Use config at this path instead of finding it in user directory")
    Path configOverride

    Config config = new DefaultConfig()

    @CommandLine.Option(names = ["--target", "-t"], description = "Add a target path")
    def addTarget(String path) {
        config.targets << new Target(path: path)
    }

    @CommandLine.Option(names = ["--source", "-s"], description = "Add a source path")
    def addSource(String path) {
        config.sources << new Source(path: path)
    }

    @CommandLine.Option(names = ["--skip", "-k"], description = "Add a skip pattern (regexp)")
    def addSkipPattern(Pattern p) {
        config.skipPatterns << p
    }

    def loadConfig() {
        config.loadDefault()

        // include override path, or user config path
        if (!configOverride) {
            config.loadUser()
        } else {
            log.info "Trying to load config from $configOverride"
            config.load(configOverride)
        }
        println("Read config $config")
    }

    @Override
    Integer call() throws Exception {
        loadConfig()
        def flow = new Flow(ui: this)
        flow.run()
        0
    }

    @Override
    Set<Target> confirmTarget(Set<Target> targets) {
        targets.each { println it }
    }

    @Override
    Set<Source> confirmSource(Set<Source> sources) {
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



