plugins {
	id("java")
}

group = "org.togetherjava"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("net.dv8tion:JDA:5.0.0-beta.12") {
		exclude(module = "opus-java")
	}
	// https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-toml
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2")

	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
	useJUnitPlatform()
}
