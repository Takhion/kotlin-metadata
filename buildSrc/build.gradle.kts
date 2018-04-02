plugins { `kotlin-dsl` }

repositories { jcenter() }

dependencies {
    compile(kotlin("gradle-plugin"))
    compile("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.0")
}
