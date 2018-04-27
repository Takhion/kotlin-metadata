import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins { kotlin("jvm") }

dependencies {
    compile(kotlin("stdlib"))
    compile("me.eugeniomarletti.kotlin.metadata:kotlin-compiler-lite:1.0.3-k-1.2.40")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-module-name", "$libGroupId.$libArtifactId")
    }
}

tasks.withType<Jar> { baseName = libArtifactId }

val upload = configurePublications()
