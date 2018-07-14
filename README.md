[![Build Status](https://travis-ci.org/pt-osda/java-dependencies-analysis.svg?branch=master)](https://travis-ci.org/pt-osda/java-dependencies-analysis)
[![license](https://img.shields.io/github/license/pt-osda/java-dependencies-analysis.svg)](https://github.com/pt-osda/java-dependencies-analysis/blob/master/LICENSE)
[![Known Vulnerabilities](https://snyk.io/test/github/pt-osda/java-dependencies-analysis/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/pt-osda/java-dependencies-analysis?targetFile=build.gradle)

# java-dependencies-analysis
Analyse Open Source dependencies in projects development. Aplication to analyse security and license aspects in Open Source project's dependencies, applicable to Java platform.

# Using the plugin
To use this plugin it is required to add the following script snippet, for [Gradle](https://gradle.org) 2.1 or later:

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

Once that is done the project must include a policy file named osda, which indicates certains attributes of the project and how the plugin must react to certain events in its execution. The file has the following schema:

<details><summary>Policy file structure</summary>
<p>
  
```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "title": "Project Policy",
  "description": "A policy with a project related configurations and \tinformation",
  "type": "object",
  "properties": {
    "project_id": {
      "description": "Id of the project to present in the report",
      "type": "string"
    },
    "project_name": {
      "description": "Name of the project to present in the report",
      "type": "string"
    },
    "project_version": {
      "description": "Version of the project to present in the report",
      "type": "string"
    },
    "project_description": {
      "description": "Description of the project to present in the report",
      "type": "string"
    },
    "organization": {
      "description": "The organization the project belongs to",
      "type": "string"
    },
    "repo": {
      "description": "The repository in github the project belongs to",
      "type": "string"
    },
    "repo_owner": {
      "description": "The owner of the repository the project belongs to",
      "type": "string"
    },
    "admin": {
      "description": "The username of the administrator of the project (Only used in project first report)",
      "type": "string"
    },
    "invalid_licenses": {
      "description": "The names of all invalid licenses. Default value is an empty collection",
      "type": "array"
    },
    "fail": {
      "description": "Indicates if the build should fail in case a vulnerability is found. Default value is false",
      "type": "boolean"
    },
    "api_cache_time": {
      "description": "Indicates, in seconds, the amount of time the cached results should be considered valid. If 0 (which is the default value), there are no restrictions on the lifetime of cached results",
      "type": "number"
    }
  },
  "required": ["project_id", "project_name", "admin"]
}
```
  
</p>
</details>



To have an admin the user of the plugin must create and account in the following link: http://35.234.147.77. There it is required to register an account and the username must be the one to be added to the admin field in the poliy file. Once that is done it is required to generate a token in the link: http://35.234.147.77/user. The generated token must be added as a environment variable with the name 
**CENTRAL_SERVER_TOKEN**.

# How it Works
This plugin will install in the project a task, which will be responsible for the validation of the dependencies of the project. And as such every time that it is needed to validate the dependencies of the project all that is required is to run the following command in the project directory:
```
./gradlew validateDependencies
```

Once the plugin finishes its execution a report will be produced and stored in a server. The server is acessible in the following link: http://35.234.147.77/
