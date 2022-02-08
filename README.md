# Source Wizard
### Introduction
**Source Wizard is a plugin to access sources of your `node_module` dependencies**

It caches sources of npm modules in your local directory `~/.npm-src` where they can be easily
accessed from your project. 

When opening a file from `node_modules`, if source file is cached, pop up appears
with the suggestion to open source file. When file is not found in the local cache, pop up to download
and cache sources to your machine appears.

Npm modules sources are resolved from `repository.json` field in its package json file:
```json
 "repository": {
    "url": "https://github.com/repository"
  },
```
Plugin will not be able to download sources for modules not containing repository info in their package json files. 

**At the moment cache sources are downloaded as the latest code in the repo.
Because of this code in the repository might differ than the code found in published npm module**

### Implementation
On opened file from `node_modules` directory, plugin performs search for actual source file in the cache directory
using Filename index search provided by Intellij. If the file is found, popup to open source file is displayed. 

If no sources are present, a popup is displayed with the ability to download
sources from the repository url found in `package.json` file. Downloaded sources
are indexed in Intellij filename index.

Source files are listed in *External Libraries* tab in project tree view where
you can deep dive into implementation details of your *node_module* dependencies.

If you already have source repository on your computer instead of downloading duplicate you can
create symbolic link to it in cache repository with provided user interface.