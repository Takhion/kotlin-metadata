import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins { kotlin("jvm") }

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("compiler-embeddable") as String) {
        isTransitive = true
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-module-name", "$libGroupId.$libArtifactId")
    }
}

tasks.withType<Jar> { baseName = libArtifactId }

val upload = configurePublications()
