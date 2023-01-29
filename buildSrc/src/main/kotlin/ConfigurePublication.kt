import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.register

fun Project.configurePublication() {
    val properties = gradleLocalProperties(rootDir)

    val isReleaseBuild = properties["release"]?.toString().toBoolean()

    val javadocJar = tasks.register("javadocJar", Jar::class) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {

        extensions.findByType<PublishingExtension>()?.apply {
            repositories {
                maven {
                    name = "sonatype"
                    url = uri(
                        if (isReleaseBuild) {
                            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                        } else {
                            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                        }
                    )
                    credentials {
                        username = requireNotNull(properties["sonatypeUsername"]?.toString())
                        password = requireNotNull(properties["sonatypePassword"]?.toString())
                    }
                }
            }

            publications.withType<MavenPublication>().configureEach {

                artifact(javadocJar)

                version = buildString {
                    append(Config.versionName)
                    if (!isReleaseBuild) append("-SNAPSHOT")
                }

                pom {
                    name.set(Config.artifact)
                    description.set("A collection of Kotlin Multiplatform essentials")
                    url.set("https://github.com/respawn-app/kmmutils")

                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("respawn-app")
                            name.set("Respawn")
                            email.set("hello@respawn.pro")
                            url.set("https://respawn.pro")
                            organization.set("Respawn")
                            organizationUrl.set(url)
                        }
                    }
                    scm {
                        url.set("https://github.com/respawn-app/kmmutils.git")
                    }
                }
            }
        }

        extensions.findByType<SigningExtension>()?.apply {
            val publishing = extensions.findByType<PublishingExtension>() ?: return@apply
            val keyId = requireNotNull(properties["signing.keyId"]?.toString())
            val password = requireNotNull(properties["signing.password"]?.toString())

            isRequired = isReleaseBuild
            useGpgCmd()
            useInMemoryPgpKeys(keyId, password)
            sign(publishing.publications)
        }
    }
}
