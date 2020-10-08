/* (C) 2020 Edward Harman */
package org.ethelred.mc.text

import org.apache.commons.text.StringEscapeUtils
import org.ethelred.util.groovy.Table
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiRenderer.Code

import groovy.transform.Memoized

/**
 * wrapper for String that understands minecraft format codes
 * See https://minecraft.gamepedia.com/Formatting_codes
 *
 */
class MCText {
    static Table formatData = Table.of {
        name(String) | mcCode(Character) | jansiCode(Code) | jansiBright(Boolean) | htmlColor(HtmlColor) | htmlStyle(String)
        black        | '0'               | BLACK           | false                | '000000'             | null
        dark_blue    | '1'               | BLUE            | false                | '0000AA'             | null
        dark_green   | '2'               | GREEN           | false                | '00AA00'             | null
        dark_aqua    | '3'               | CYAN            | false                | '00AAAA'             | null
        dark_red     | '4'               | RED             | false                | 'AA0000'             | null
        dark_purple  | '5'               | MAGENTA         | false                | 'AA00AA'             | null
        gold         | '6'               | YELLOW          | false                | 'FFAA00'             | null
        gray         | '7'               | WHITE           | false                | 'AAAAAA'             | null
        dark_gray    | '8'               | BLACK           | true                 | '555555'             | null
        blue         | '9'               | BLUE            | true                 | '5555FF'             | null
        green        | 'a'               | GREEN           | true                 | '55FF55'             | null
        aqua         | 'b'               | CYAN            | true                 | '55FFFF'             | null
        red          | 'c'               | RED             | true                 | 'FF5555'             | null
        purple       | 'd'               | MAGENTA         | true                 | 'FF55FF'             | null
        yellow       | 'e'               | YELLOW          | true                 | 'FFFF55'             | null
        white        | 'f'               | WHITE           | true                 | 'FFFFFF'             | null
        minecoin     | 'g'               | YELLOW          | false                | 'DDD605'             | null
        obfuscated   | 'k'               | CONCEAL_ON      | false                | null                 | null
        bold         | 'l'               | BOLD            | false                | null                 | 'font-weight: bold;'
        italic       | 'o'               | ITALIC          | false                | null                 | 'font-style: italic;'
        reset        | 'r'               | RESET           | false                | null                 | null
    }

    static final String FORMAT_SYMBOL = '§'
    static final String OBFUSCATE = '▒'

    static MCText fromString(String s) {
        s == null ? null : new MCText(s)
    }

    String raw

    MCText(String original) {
        raw = original
    }

    /*
     Manual investigation by writing on signs in the game:
     Only one color can be in effect, so a color code should close a previous color.
     Strikethrough and underline do not work in Bedrock.
     Obfuscated, bold and italic can all be combined (and with the one color), so a code for one doesn't reset another.
     Reset resets all codes including color.
     */

    @Memoized
    private def getParsed() {
        List l = []
        def current = new StringBuilder()
        boolean awaitFormat = false
        def openFormatStack = []
        raw.each { c ->
            if (awaitFormat) {
                awaitFormat = false
                def format = formatData.findByMcCode(c)
                if (format) {
                    // insert current string
                    l << new Inner(s: current.toString())
                    current = new StringBuilder()

                    // close open format if necessary
                    // special case reset
                    if (format.name == 'reset') {
                        // close all
                        if (openFormatStack) {
                            l << new ResetFormat()
                        }
                        while (openFormatStack) {
                            def f = openFormatStack.pop()
                            l << new EndFormat(format: f)
                        }
                    } else if (format.jansiCode.isColor() && openFormatStack.any { it.jansiCode.isColor() }) {
                        // close everything because it's easier that way
                        // if other formats have to be closed, reopen them
                        def reopen = []
                        if (openFormatStack) {
                            l << new ResetFormat()
                        }
                        while (openFormatStack) {
                            def f = openFormatStack.pop()
                            l << new EndFormat(format: f)
                            if (!f.jansiCode.isColor()) {
                                reopen.push(f)
                            }
                        }
                        l << new ResetFormat()
                        while (reopen) {
                            def f = reopen.pop()
                            l << new StartFormat(format: f)
                            openFormatStack.push(f)
                        }

                    }

                    // open new format (unless it was reset)
                    if (format.name != 'reset') {
                        l << new StartFormat(format: format)
                        openFormatStack.push(format)
                    }
                } else {
                    log.warn("Illegal format code '$FORMAT_SYMBOL$c' in $raw")
                }
            } else if (c == FORMAT_SYMBOL) {
                awaitFormat = true
            } else if (openFormatStack.any { it.name == 'obfuscated' }) {
                // hack
                current << OBFUSCATE
            } else {
                current << c
            }
        }
        // at end write current and reset
        l << new Inner(s: current.toString())
        if (openFormatStack) {
            l << new ResetFormat()
        }
        while (openFormatStack) {
            def f = openFormatStack.pop()
            l << new EndFormat(format: f)
        }
        return l
    }

    String getStrip() {
        parsed*.strip.join()
    }

    String getAnsi() {
        Ansi a = Ansi.ansi()
        parsed*.ansi(a)
        a.toString()
    }

    String getHtml() {
        parsed*.html.join()
    }

    @Override
    String toString() {
        ansi
    }

}

class HtmlColor {
    String color

    HtmlColor(String value) {
        if (!value =~ /[0-9A-F]{6}/) {
            throw new IllegalArgumentException("Invalid color value $value")
        }
        this.color = "color: #${value};"
    }
}

class Base {
    String getStrip() {
        ""
    }

    void ansi(Ansi a) {
        // no-op
    }

    String getHtml() {
        ""
    }
}

class Inner extends Base {
    String s

    String getStrip() {
        s
    }

    void ansi(Ansi a) {
        a.a(s)
    }

    String getHtml() {
        StringEscapeUtils.escapeHtml4(s)
    }
}

class StartFormat extends Base {
    def format

    void ansi(Ansi a) {
        Code c = format.jansiCode
        if (c.isColor()) {
            a.fg(c.getColor())
            if (format.jansiBright) {
                a.bold()
            }
        } else {
            a.a(c.getAttribute())
        }
    }

    String getHtml() {
        def style = format.htmlColor ? format.htmlColor.color : format.htmlStyle;
        if (style) {
            """<span style="$style">"""
        } else {
            ""
        }
    }
}

class EndFormat extends Base {
    def format

    String getHtml() {
        def style = format.htmlColor ? format.htmlColor.color : format.htmlStyle;
        if (style) {
            """</span>"""
        } else {
            ""
        }
    }
}

class ResetFormat extends Base {
    void ansi(Ansi a) {
        a.reset()
    }
}