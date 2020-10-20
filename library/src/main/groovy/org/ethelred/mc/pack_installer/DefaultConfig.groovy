package org.ethelred.mc.pack_installer

import dev.dirs.ProjectDirectories
import groovy.transform.ToString
import org.codehaus.groovy.control.CompilerConfiguration

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

class DefaultConfig implements Config {
    GroovyClassLoader classLoader

    DefaultConfig() {
        this.classLoader = new GroovyClassLoader(this.getClass().getClassLoader(),
                new CompilerConfiguration(scriptBaseClass: ConfigScript.name))
    }

    Set<Target> targets = new HashSet<>()
    Set<Source> sources = new HashSet<>()
    Set<Pattern> skipPatterns = new HashSet<>()

    def loadDefault() {
        def defaultFile = Paths.get(getClass().getResource("/default_config.groovy").toURI())
        load(defaultFile)
    }

    def loadUser() {
        def userFile = FileSystems.getDefault()
                .getPath(ProjectDirectories.from("org", "ethelred", "mc-pack-installer").configDir, "config.groovy")
        load(userFile)
    }

    def load(file) {
        log.debug "$file"
        if (file && Files.isReadable(file)) {
            log.debug "${file.text}"
            //noinspection GrDeprecatedAPIUsage
            def script = classLoader.parseClass(file.text).newInstance()
            script.binding = new Binding(
                    mcpelauncher: ProjectDirectories.from("", "", "mcpelauncher").dataDir,
                    HOME: System.getProperty("user.home")
            )
            log.debug "${this}"
            script.setAppConfig(this)
            log.debug "${script.dump()}"
            script.run()
        } else {
        log.info "No config file with path $configOverride"
    }
    }


    @Override
    public String toString() {
        return """\
DefaultConfig{
    targets=$targets, 
    sources=$sources, 
    skipPatterns=$skipPatterns
}"""
    }
}