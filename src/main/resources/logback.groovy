appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%highlight(%-5level) %boldWhite(%logger{15}) - %boldYellow(%msg) %n"
    }
}
root(DEBUG, ["CONSOLE"])