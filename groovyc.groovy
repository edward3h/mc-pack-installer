import groovy.util.logging.Slf4j
import org.codehaus.groovy.ast.ClassNode

withConfig(configuration) {
    source(classValidator: { ClassNode cn -> !cn.isInterface()}) {
        ast(Slf4j)
    }
}