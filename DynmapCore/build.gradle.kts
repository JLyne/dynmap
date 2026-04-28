plugins {
    alias(libs.plugins.shadow)
    id("dynmap.java-conventions")
}

description = "DynmapCore"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    implementation(project(":DynmapCoreAPI"))
    implementation(libs.jsonSimple)
    implementation(libs.snakeYaml)
    implementation(libs.owaspHtmlSanitizer)
    implementation(libs.gson)
}

tasks {
    processResources {
        // replace stuff in mcmod.info, nothing else
        filesMatching(
            listOf(
            "core.yml",
            "lightings.txt",
            "perspectives.txt",
            "shaders.txt",
            "extracted/web/version.js",
            "extracted/web/index.html",
                )) {
            // replace version and mcversion
            expand(
                    "buildnumber" to project.parent!!.ext.get("buildNumber").toString(),
                    "version" to project.version
            )
        }
    }

    jar {
       archiveClassifier = "unshaded"
    }

    shadowJar {
        dependencies {
            include(dependency(libs.jsonSimple))
            include(dependency(libs.snakeYaml))
            include(dependency(libs.owaspHtmlSanitizer))
            include(dependency(":DynmapCoreAPI"))
            exclude("META-INF/maven/**")
            exclude("META-INF/services/**")
        }
        relocate("org.json.simple", "org.dynmap.json.simple")
        relocate("org.yaml.snakeyaml", "org.dynmap.snakeyaml")
        relocate("org.owasp.html", "org.dynmap.org.owasp.html")

        destinationDirectory = file("../target")
        archiveClassifier = ""
    }

    artifacts {
        archives(shadowJar)
    }
}

