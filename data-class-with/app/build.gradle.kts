plugins {
    kotlin("jvm")
    kotlin("kapt")
}

apply { from("generated-kotlin-sources.gradle.kts") }

dependencies {
    compile(kotlin("stdlib"))
    compile(project(":data-class-with:api"))
    kapt(project(":data-class-with:processor"))
}
