[![Build Status](https://travis-ci.org/pt-osda/java-dependencies-analysis.svg?branch=master)](https://travis-ci.org/pt-osda/java-dependencies-analysis)
[![license](https://img.shields.io/github/license/pt-osda/java-dependencies-analysis.svg)](https://github.com/pt-osda/java-dependencies-analysis/blob/master/LICENSE)
[![Known Vulnerabilities](https://snyk.io/test/github/pt-osda/java-dependencies-analysis/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/pt-osda/java-dependencies-analysis?targetFile=build.gradle)

# java-dependencies-analysis
Analyse Open Source dependencies in projects development. Aplication to analyse security and license aspects in Open Source project's dependencies, applicable to Java platform.

# Using the plugin
To use this plugin all that is needed is to add the following script snippet, for [Gradle](https://gradle.org) 2.1 or later:

```
plugins {
  id "com.github.pt-osda.java-dependencies-analysis" version "1.0.0"
}
```

To use in older versions of Gradle or where dynamic configuration is required use the following script snippet:
```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.github.pt-osda.java-dependencies-analysis:java-dependencies-analysis:1.0.0"
  }
}

apply plugin: "com.github.pt-osda.java-dependencies-analysis"
```

# How it Works
This plugin will install in the project a task, which will be responsible for the validation of the dependencies of the project. And as such every time that it is needed to validate the dependencies of the project all that is required is to run the following command in the project directory:
```
./gradlew validateDependencies
```