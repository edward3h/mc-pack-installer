package org.ethelred.mc.pack_installer

import dev.dirs.ProjectDirectories
import groovy.transform.ToString
import net.java.truevfs.access.TPath
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

    private Map<TPath,Location> _locations = new HashMap<>()
    Set<Pattern> skipPatterns = new HashSet<>()

    Set<Target> getTargets() {
        _locations.values().grep(Target) as Set
    }

    Set<Source> getSources() {
        _locations.values().grep(Source) as Set
    }

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

    Target target(String path, Class type = Target) {
        def k = Location.normalizePath(path)
        def v = _locations.get(k)
        switch (v) {
            case null:
                v = type == WebTarget ? new WebTarget(path: k) : new Target(path: k)
                _locations.put(k, v)
                break
            case Target:
                break
            default:
                throw new IllegalStateException("$path is already mapped to $v")
        }
        if (type == WebTarget && v.class != WebTarget) {
            v = new WebTarget(path: k)
            // TODO copy common properties, but there aren't any yet
            _locations.put(k, v)
        }
        v as Target
    }

    Source source(String path) {
        def k = Location.normalizePath(path)
        def v = _locations.get(k)
        switch (v) {
            case null:
                v = new Source(path: k)
                _locations.put(k, v)
                break
            case Source:
                break
            default:
                throw new IllegalStateException("$path is already mapped to $v")
        }
        v as Source
    }
}