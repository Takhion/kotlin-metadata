@file:Suppress("MayBeConstant")

import org.gradle.api.Project

val codeVersion = "1.2.1"

val gitHubUser = "Takhion"
val gitHubRepo = "kotlin-metadata"
val gitHubRepoDomain = "github.com/$gitHubUser/$gitHubRepo"

val gitTag = "v$codeVersion"
val gitRepo = "$gitHubRepoDomain.git"

val mainRepoUrl = "https://$gitHubRepoDomain"
val taggedRepoUrl = "$mainRepoUrl/tree/$gitTag"

val libName = "kotlin-metadata"
val libDescription = "Analysis of Kotlin type metadata during annotation processing."
val libUrl = mainRepoUrl

val libGroupId = "me.eugeniomarletti.kotlin.metadata"
val libArtifactId = "kotlin-metadata"
val libVersion = codeVersion

val publicationName = libArtifactId.split("-").joinToString("") { it.capitalize() }.decapitalize()
val publicationTaskName = "publish${publicationName.capitalize()}PublicationToMavenRepository"

val authorName = "Eugenio Marletti"

val licenseName = "MIT"
val Project.licenseFile get() = rootDir.resolve("LICENSE")
val Project.licenseUrl get() = "$mainRepoUrl/blob/$gitTag/${licenseFile.toRelativeString(rootDir)}"

val issuesSystem = "GitHub"
val issuesUrl = "$mainRepoUrl/issues"

val bintrayRepo = "kotlin-metadata"
val bintrayTags = arrayOf("kotlin", "kotlin-metadata")

val Project.bintrayPublish by extraOrDefault(true)
val Project.bintrayOverride by extraOrDefault(false)
val Project.bintrayDryRun by extraOrDefault(false)
val Project.bintrayGpgSign by extraOrDefault(true)
val Project.bintrayMavenCentralSync by extraOrDefault(true)
val Project.bintrayMavenCentralClose by extraOrDefault(true)

val Project.bintrayUser by extraOrEnv("BINTRAY_USER")
val Project.bintrayKey by extraOrEnv("BINTRAY_KEY")

val Project.sonatypeUser by extraOrEnv("SONATYPE_USER")
val Project.sonatypePassword by extraOrEnv("SONATYPE_PASSWORD")

val Project.outputDir get() = buildDir.resolve("out")
