/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

import groovy.transform.EqualsAndHashCode
import org.ethelred.mc.pack.Pack

import groovy.transform.ToString
import groovy.xml.MarkupBuilder

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import net.java.truevfs.access.TPath

/**
 * instead of installing packs into a game directory, this creates a web listing of them
 */
@EqualsAndHashCode(callSuper = true)
class WebTarget extends Target {

    String remote

    @Override
    Path getPackRoot(type, ignore) {
        return path.resolve("packs")
    }

    Path getOutput() {
        Path.of(path.toString().takeAfter("file:"))
    }

    def extractIcon(pack) {
        def p = pack.iconPath
        if (p) {
            def iconRoot = output.resolve("icons")
            Files.createDirectories(iconRoot)
            def iconName = "${pack.uuid} ${pack.version}".replaceAll(/\W+/, '_') + '.png'
            def iconPath = iconRoot.resolve(iconName)
            if (!Files.exists(iconPath)) {
                Files.copy(p, iconPath)
            }
            return [src: "icons/$iconName", alt: pack.name.strip]
        }
        return null
    }

    def copyResources() {
        def resourceDir = Paths.get(getClass().getResource("/web").toURI())
        resourceDir.eachFileRecurse {
            def relative = resourceDir.relativize(it)
            def outpath = output.resolve(relative)
            if (!Files.exists(outpath)) {
                Files.copy(it, outpath)
            }
        }
    }

    @Override
    void writePacks(List<List<PackInstances>> lists) {
        log.warn "WebTarget writePacks $output"
        super.writePacks(lists)

        copyResources()

        def indexPath = output.resolve("index.html")
        log.warn "WebTarget write pack listing $indexPath"
        _withPrintWriter(indexPath) { writer ->
            def index = new MarkupBuilder(writer)
            index.html {
                head {
                    title("MC Pack Listing")
                    mkp.yieldUnescaped("<link href=\"https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap\" rel=\"stylesheet\">")
                    style('''
img { max-width: 256px; min-width: 128px; }
th, td, fieldset { 
    padding: 4px; 
    background-size: cover;
}
th { background-image: url("sign_crimson.png"); color: white; }
td.icon { background-image: url("sign_darkoak.png"); color: white; }
td.pack { background-image: url("sign.png"); }
td.type { font-style: italic; background-image: url("sign_jungle.png"); }
td.description, fieldset { background-image: url("sign_birch.png"); }
h1 { color: white; }
body { 
    font-family: 'Press Start 2P', monospace; 
    font-size: 12px;
    background-color: #222244; 
    color: black; 
    image-rendering: pixelated;
}
div.error { color: red; }
''')
                    meta("http-equiv":"content-type", content:"text/html; charset=UTF-8")
                }
                body {
                    h1("MC Pack Listing")
                    fieldset(class: "search") {
                        span("Search: ")
                        input(type: "text", id: "searchQuery", onkeyup: "doFilter()")
                        button(onclick: "doClearFilter()", "Clear")
                    }
                    table {
                        thead {
                            tr {
                                "Icon Name Type Description".split().each {
                                    th(it)
                                }
                            }
                        }
                        tbody(id: "mainTableBody") {
                            lists.each { group ->
                                group.each { pack ->
                                    if (matches(pack)) {
                                        tr {
                                            def icon = extractIcon(pack)
                                            if (icon) {
                                                td(class: "icon") {
                                                    img(icon)
                                                }
                                            } else {
                                                td(class: "icon") {
                                                    mkp.yieldUnescaped("&nbsp;")
                                                }
                                            }
                                            td(class: "pack") {
                                                if (pack.isDevelopment()) {
                                                    div(class: "error", "Development!")
                                                }
                                                div {
                                                    a(href: "packs/${pack.zipName}.mcpack") {
                                                        mkp.yieldUnescaped("${pack.name.html} ${pack.version}")
                                                    }
                                                }
                                                if (pack.metadata.authors) {
                                                    div {
                                                        mkp.yieldUnescaped('🧑‍💻 ' + pack.metadata.authors.join(', '))
                                                    }
                                                }
                                                if (pack.metadata.url) {
                                                    div {
                                                        a(href: pack.metadata.url, "🏠 Homepage")
                                                    }
                                                }
                                            }
                                            td(class: "type", pack.type)
                                            td(class: "description") {
                                                mkp.yieldUnescaped(pack.description.html)
                                            }
                                        }
                                    }
                                }
                                tr {
                                    td(colspan:4) {
                                        mkp.yieldUnescaped("&nbsp;")
                                    }
                                }
                            }
                        }
                    }
                    script {
                        mkp.yieldUnescaped("""
function doFilter() {
    var input, query, tbody;
    input = document.getElementById('searchQuery');
    query = input.value.toLowerCase();
    tbody = document.getElementById('mainTableBody');
    Array.from(tbody.getElementsByTagName('tr')).forEach(function (tr) {
        var rowText = tr.textContent || tr.innerText;
        rowText = rowText.toLowerCase();
        if (query == "" || rowText.indexOf(query) > -1) {
            tr.style.display = "";
        } else {
            tr.style.display = "none";
        }
    });
}

function doClearFilter() {
    var input;
    input = document.getElementById('searchQuery');
    input.value = "";
    doFilter();
}
""")
                    }
                }
            }
        }
    }

    @Override
    void finish() {
        _worldHackSorry()
        if (remote) {
            def cmd = [
                "rsync",
                "-av",
                path.toString().takeAfter("file:"),
                remote
            ]
            log.warn cmd.join(' ')
            cmd.execute().waitForProcessOutput(System.out, System.err)
        }
    }

    void _worldHackSorry() {
        def worlds = output.resolve("worlds")
        Files.createDirectories(worlds)
        def downloads = (System.getProperty("user.home") + "/Downloads") as File
        downloads.eachFileMatch(~/.*\.mcworld$/) { f ->
            Files.copy(f.toPath(), worlds.resolve(f.name), StandardCopyOption.REPLACE_EXISTING)
            def zipFileName = f.name.replaceAll(~/\.mcworld$/, '.zip')
            Files.copy(f.toPath(), worlds.resolve(zipFileName), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    // the groovy built in catches IOExceptions...
    static void _withPrintWriter(Path path, Closure block) {
        PrintWriter w = path.newPrintWriter()
        block.call(w)
    }

    @Override
    boolean exists() {
        try {
            Files.createDirectories(path)
            true
        } catch (e) {
            log.error "Failed to create directories $path", e
            false
        }
    }
}
