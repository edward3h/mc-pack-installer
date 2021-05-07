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

    // TODO refactor
    def extractWorldIcon(world) {
        def p = world.iconPath
        if (p) {
            def iconRoot = output.resolve("icons")
            Files.createDirectories(iconRoot)
            def iconName = "${world.name.strip}".replaceAll(/\W+/, '_') + '.jpeg'
            def iconPath = iconRoot.resolve(iconName)
            if (!Files.exists(iconPath)) {
                Files.copy(p, iconPath)
            }
            return [src: "icons/$iconName", alt: world.name.strip]
        }
        return null
    }

    def _isNewer(Path input, Path output) {
        def inputTime = Files.getLastModifiedTime(input)
        def outputTime = Files.getLastModifiedTime(output)
        log.warn "_isNewer ${input.fileName} $inputTime $outputTime"
        inputTime > outputTime
    }

    def copyResources() {
        def resourceDir = Paths.get(getClass().getResource("/web").toURI())
        log.warn "copy resources $resourceDir"
        resourceDir.eachFileRecurse {
            def relative = resourceDir.relativize(it)
            def outpath = output.resolve(relative)
            log.warn "copy resource $it $outpath"
            if (!Files.exists(outpath) || _isNewer(it, outpath)) {
                Files.copy(it, outpath, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    static def PAGES = [[href: "index.html", title: "Packs"], [href: "worlds.html", title: "Worlds"]]

    void navigation(filename, page) {
        page.div(class: "navigation") {
            PAGES.each {p ->
                def current = p.href == "${filename}.html"
                div(class: "tab ${current ? 'current' : ''}") {
                    if (current) {
                        span(p.title)
                    } else {
                        a(href: p.href, p.title)
                    }
                }
            }
        }
    }

    void writeTablePage(filename, atitle, columnNames, bodyContent) {
        def pagePath = output.resolve("${filename}.html")
        log.warn "WebTarget write page $pagePath"
        _withPrintWriter(pagePath) { writer ->
            def page = new MarkupBuilder(writer)
            page.html {
                head {
                    title(atitle)
                    mkp.yieldUnescaped("<link href=\"https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap\" rel=\"stylesheet\">")
                    mkp.yieldUnescaped("<link href=\"style.css\" rel=\"stylesheet\">")

                    meta("http-equiv":"content-type", content:"text/html; charset=UTF-8")
                }
                body {
                    h1(atitle)
                    navigation(filename, page)
                    fieldset(class: "search") {
                        span("Search: ")
                        input(type: "text", id: "searchQuery", onkeyup: "doFilter()")
                        button(onclick: "doClearFilter()", "Clear")
                    }
                    table {
                        thead {
                            tr {
                                columnNames.each {
                                    th(it)
                                }
                            }
                        }
                        tbody(id: "mainTableBody") {
                            bodyContent(page)
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
    void writePacks(List<List<PackInstances>> lists) {
        log.warn "WebTarget writePacks $output"
        super.writePacks(lists)

        copyResources()

        writeTablePage("index", "MC Pack Listing", "Icon Name Type Description".split()) { markup ->
            lists.each { group ->
                group.each { pack ->
                    if (matches(pack)) {
                        markup.tr {
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
                markup.tr {
                    td(colspan:4) {
                        mkp.yieldUnescaped("&nbsp;")
                    }
                }
            }
        }
    }


    void writeWorlds(worlds) {
        def worldRoot = path.resolve("worlds")
        worlds.each {
            writeResource(it, worldRoot, ".mcworld")
        }
        writeTablePage("worlds", "MC World Listing", "Icon Name".split()) { markup ->
            worlds.each { w ->
                markup.tr {
                    def icon = extractWorldIcon(w)
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
                        div {
                            a(href: "worlds/${w.zipName}.mcworld") {
                                mkp.yieldUnescaped("${w.name.html}")
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    void finish() {
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

    // the groovy built in catches IOExceptions, I wanted them to be reported
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
