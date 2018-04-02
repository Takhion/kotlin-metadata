import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get

val publishing = configuration<PublishingExtension>()
val bintray = configuration<BintrayExtension>()

val Project.java by extension<JavaPluginConvention>()

val SourceSetContainer.main: SourceSet get() = this["main"]
