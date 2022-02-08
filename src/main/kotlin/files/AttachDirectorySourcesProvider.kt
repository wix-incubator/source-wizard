package files

import java.nio.file.Path

interface AttachDirectorySourcesProvider {

    fun attachSourceDirectory(sourcePath: Path, rootSourceDirectoryPath: Path)
}