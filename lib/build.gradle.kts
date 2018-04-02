import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val PUBLISH_GROUP_ID by extra("me.eugeniomarletti")
val PUBLISH_ARTIFACT_ID by extra("kotlin-metadata")
val PUBLISH_VERSION by extra("1.2.1")

plugins { kotlin("jvm") }

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("compiler-embeddable") as String) {
        isTransitive = true
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-module-name", "$PUBLISH_GROUP_ID.$PUBLISH_ARTIFACT_ID")
    }
}
