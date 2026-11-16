plugins {
    id("io.papermc.paperweight.userdev")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

dependencies {
    compileOnly(project(":platforms:bukkit:common"))
    compileOnly(project(":platforms:bukkit:nms:common"))
    compileOnly("xyz.jpenilla", "reflection-remapper", Versions.Bukkit.reflectionRemapper)
    paperweight.paperDevBundle(Versions.Bukkit.paperDevBundle1_21_11)
}
