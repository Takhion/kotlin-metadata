import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayExtension.MavenCentralSyncConfig
import com.jfrog.bintray.gradle.BintrayExtension.PackageConfig
import com.jfrog.bintray.gradle.BintrayExtension.VersionConfig
import com.jfrog.bintray.gradle.RecordingCopyTask
import org.gradle.api.publish.maven.MavenPom
import org.gradle.kotlin.dsl.closureOf
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

fun BintrayExtension.filesSpec(configure: RecordingCopyTask.() -> Unit): Any? = filesSpec(closureOf(configure))
fun BintrayExtension.pkg(configure: PackageConfig.() -> Unit): Any? = pkg(closureOf(configure))
fun PackageConfig.version(configure: VersionConfig.() -> Unit): Any? = version(closureOf(configure))
fun VersionConfig.mavenCentralSync(configure: MavenCentralSyncConfig.() -> Unit): Any? = mavenCentralSync(closureOf(configure))

inline fun MavenPom.buildXml(crossinline xml: NodeContext<Element>.() -> Unit) {
    withXml {
        val root = asElement()
        NodeContext(root, root.ownerDocument).xml()
    }
}

class NodeContext<out T : Node>(val node: T, val doc: Document) {

    inline operator fun String.invoke(nodeContent: NodeContext<Element>.() -> Unit): Element =
        doc.createElement(this)
            .also { NodeContext(it, doc).nodeContent() }
            .also { node.appendChild(it) }

    operator fun String.rangeTo(textContent: String) =
        invoke { node.textContent = textContent }
}
