repositories {

}
tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

dependencies {
    shadedApi(project(":common:implementation:base"))

    compileOnly("io.papermc.paper", "paper-api", Versions.Bukkit.paper)
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.25.2")

    compileOnly("org.mvplugins.multiverse.core", "multiverse-core", Versions.Bukkit.multiverse)

    shadedApi("io.papermc", "paperlib", Versions.Bukkit.paperLib)

    shadedApi("com.google.guava", "guava", Versions.Libraries.Internal.guava)

    shadedApi("org.incendo", "cloud-paper", Versions.Bukkit.cloud)
}
