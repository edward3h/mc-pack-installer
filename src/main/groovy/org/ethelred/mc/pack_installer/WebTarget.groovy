/* (C) 2020 Edward Harman */
package org.ethelred.mc.pack_installer

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
@ToString
class WebTarget extends Target {

    @Override
    Path getPackRoot(type) {
        return path.resolve("packs")
    }

    Path getOutput() {
        Path.of(path.toString())
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
    void writePacks(List<List<Pack>> lists) {
        super.writePacks(lists)

        copyResources()

        output.resolve("index.html").withPrintWriter { writer ->
            def index = new MarkupBuilder(writer)
            index.html {
                head {
                    title("MC Pack Listing")
                    mkp.yieldUnescaped("<link href=\"https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap\" rel=\"stylesheet\">")
                    style('''
img { max-width: 256px; min-width: 128px; }
th, td { 
    padding: 4px; 
    background-size: cover;
}
th { background-image: url("sign_crimson.png"); color: white; }
td.icon { background-image: url("sign_darkoak.png"); color: white; }
td.pack { background-image: url("sign.png"); }
td.type { font-style: italic; background-image: url("sign_jungle.png"); }
td.description { background-image: url("sign_birch.png"); }
h1 { color: white; }
body { 
    font-family: 'Press Start 2P', monospace; 
    background-color: #222244; 
    color: black; 
    image-rendering: pixelated;
}

''')
                    meta("http-equiv":"content-type", content:"text/html; charset=UTF-8")
                }
                body {
                    h1("MC Pack Listing")
                    table {
                        thead {
                            tr {
                                "Icon Name Type Description".split().each {
                                    th(it)
                                }
                            }
                        }
                        tbody {
                            lists.each { group ->
                                group.each { pack ->
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
""")
                    }
                }
            }
        }
    }
}
