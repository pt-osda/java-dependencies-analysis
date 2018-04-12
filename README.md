[![Build Status](https://travis-ci.org/pt-osda/java-dependencies-analysis.svg?branch=master)](https://travis-ci.org/pt-osda/java-dependencies-analysis)
[![license](https://img.shields.io/github/license/pt-osda/java-dependencies-analysis.svg)](https://github.com/pt-osda/java-dependencies-analysis/blob/master/LICENSE)
[ ![Download](https://api.bintray.com/packages/ruidtlima/pt-osda/java-dependencies-analysis/images/download.svg) ](https://bintray.com/ruidtlima/pt-osda/java-dependencies-analysis/_latestVersion)

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

If you are using [Maven](https://maven.apache.org) the following snippet must be added to the pom file:
```
<dependency>
  <groupId>com.github.pt-osda.java-dependencies-analysis</groupId>
  <artifactId>java-dependencies-analysis</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

To use with [Ivy](http://ant.apache.org/ivy/) use this snippet:
```
<dependency org='com.github.pt-osda.java-dependencies-analysis' name='java-dependencies-analysis' rev='1.0.0'>
  <artifact name='java-dependencies-analysis' ext='pom' ></artifact>
</dependency>
```
