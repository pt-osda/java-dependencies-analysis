[![Build Status](https://travis-ci.org/pt-osda/java-dependencies-analysis.svg?branch=master)](https://travis-ci.org/pt-osda/java-dependencies-analysis)
[![license](https://img.shields.io/github/license/pt-osda/java-dependencies-analysis.svg)](https://github.com/pt-osda/java-dependencies-analysis/blob/master/LICENSE)

# java-dependencies-analysis
Analyse Open Source dependencies in projects development. Aplication to analyse security and license aspects in Open Source project's dependencies, applicable to Java platform.

# Using the plugin
To use this plugin all that is needed is to add the following script snippet, for Gradle 2.1 or later:

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
