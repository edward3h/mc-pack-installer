package org.ethelred.mc.gui

import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL

/**
 * TODO
 *
 * @author edward3h
 * @since 2020-10-14
 */
class App {
    static void main(String[] args) {
        def swing = new SwingBuilder()
        swing.edt {
            frame(title: "MC Pack Installer", size: [300, 300], show: true) {
                borderLayout()
                textLabel = label(text: "Hello world", constraints: BL.NORTH)
                button(text: "Click me")
            }
        }
    }
}
