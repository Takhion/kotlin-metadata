import com.jfrog.bintray.gradle.Artifact
import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.task

fun Project.configurePublications(uploadTaskName: String = "upload"): Task {

    applyPlugin<BintrayPlugin>()
    applyPlugin<JavaLibraryPlugin>()
    applyPlugin<MavenPublishPlugin>()

    val sourcesJar = task<Jar>("sourcesJar") {
        from(java.sourceSets.main.allSource)
        classifier = "sources"
    }

    val javadocJar = task<Jar>("javadocJar") {
        classifier = "javadoc"
    }

    publishing {
        repositories.maven { url = uri(outputDir) }
        (publications) {
            publicationName(MavenPublication::class) {
                from(components["java"])
                artifact(sourcesJar)
                artifact(javadocJar)

                groupId = libGroupId
                artifactId = libArtifactId
                version = libVersion
                pom.buildXml {
                    "name"..libName
                    "description"..libDescription
                    "url"..libUrl
                    "licenses" {
                        "license" {
                            "name"..licenseName
                            "url"..licenseUrl
                        }
                    }
                    "issueManagement" {
                        "system"..issuesSystem
                        "url"..issuesUrl
                    }
                    "developers" {
                        "developer" {
                            "name"..authorName
                        }
                    }
                    "scm" {
                        "connection".."scm:git:git://$gitRepo"
                        "developerConnection".."scm:git:ssh://$gitRepo"
                        "tag"..gitTag
                        "url"..taggedRepoUrl
                    }
                }
            }
        }
    }

    bintray {
        publish = bintrayPublish
        override = bintrayOverride
        dryRun = bintrayDryRun
        user = bintrayUser
        key = bintrayKey
        filesSpec {
            fileUploads = fileTree(outputDir).map {
                Artifact().apply {
                    file = it
                    setPath(it.toRelativeString(outputDir))
                }
            }
        }
        pkg {
            repo = bintrayRepo
            name = libName
            desc = libDescription
            websiteUrl = libUrl
            issueTrackerUrl = issuesUrl
            githubRepo = "$gitHubUser/$gitHubRepo"
            vcsUrl = "https://$gitRepo"
            setLabels(*bintrayTags)
            setLicenses(licenseName)
            version {
                name = libVersion
                vcsTag = gitTag
                gpg.sign = bintrayGpgSign
                mavenCentralSync {
                    sync = bintrayMavenCentralSync
                    close = if (bintrayMavenCentralClose) "1" else "0"
                    user = sonatypeUser
                    password = sonatypePassword
                }
            }
        }
    }

    val uploadTask = task(uploadTaskName)
    val bintrayUploadTask = tasks["bintrayUpload"]

    uploadTask.dependsOn(bintrayUploadTask)
    bintrayUploadTask.dependsOn(publicationTaskName)

    return uploadTask
}
