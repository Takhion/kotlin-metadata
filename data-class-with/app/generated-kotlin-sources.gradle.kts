import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.plugins.ide.idea.IdeaPlugin

apply { plugin(IdeaPlugin::class.java) }

configure<IdeaModel> {
    module {
        val dirs = listOf(
            "generated/source/kapt/main",
            "generated/source/kaptKotlin/main",
            "tmp/kapt/main/kotlinGenerated")
            .map(buildDir::resolve)

        listOf(sourceDirs, generatedSourceDirs).forEach { it.addAll(dirs) }
    }
}
