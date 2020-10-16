/* (C) 2020 Edward Harman */
package org.ethelred.mc

import static org.ethelred.mc.text.MCText.fromString as t

import spock.lang.Specification

/**
 * TODO
 *
 */
class MCTextTest extends Specification {
    def "testcase #testString"() {
        when:
        def t = t(testString)

        then:
        t.raw == testString
        t.strip == expectStrip
        t.ansi == expectAnsi
        t.html == expectHtml

        where:
        testString | expectStrip | expectAnsi | expectHtml
        'Just some plain text.' | 'Just some plain text.' | 'Just some plain text.' | 'Just some plain text.'
        '§lbold §6gold' | 'bold gold' | '\u001b[1mbold \u001b[33mgold\u001b[m' | '<span style="font-weight: bold;">bold <span style="color: #FFAA00;">gold</span></span>'
        'a §4b §lc §od §be §rf' | 'a b c d e f' | 'a \u001b[31mb \u001b[1mc \u001b[3md \u001b[0;0;1;3;36;1me \u001b[mf' | 'a <span style="color: #AA0000;">b <span style="font-weight: bold;">c <span style="font-style: italic;">d </span></span></span><span style="font-weight: bold;"><span style="font-style: italic;"><span style="color: #55FFFF;">e </span></span></span>f'
        'off §kon §roff' | 'off ▒▒▒off' | 'off \u001B[8m▒▒▒\u001B[moff' | 'off ▒▒▒off'
    }
}
