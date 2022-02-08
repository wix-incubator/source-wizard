package files.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore
import files.FileFinderService
import infra.SourceCacheDirectory.getSourceCacheDirectoryFile


class FilenameIndexFileFinderService : FileFinderService {

    override fun findFile(
        project: Project,
        module: String,
        filename: String,
        fileDirectory: String
    ): List<VirtualFile> =
        getModuleSearchScope(project, module)
            ?.let { search(project, filename, fileDirectory, it) }
            ?: emptyList()


    private fun getModuleSearchScope(
        project: Project,
        module: String,
    ): GlobalSearchScope? {
        val root = getSourceCacheDirectoryFile()
        val rootDirectoriesSearchScope = GlobalSearchScopesCore.directoriesScope(project, true, root)
        return FilenameIndex.getFilesByName(project, module, rootDirectoriesSearchScope, true)
            .map { GlobalSearchScopesCore.directoriesScope(project, true, it.virtualFile) }
            .firstOrNull()
    }

    private fun search(
        project: Project,
        fileName: String,
        fileDirectory: String,
        searchScope: GlobalSearchScope
    ): List<VirtualFile> {
        val fileNameWithoutExt = fileName.split(".").first()
        val sources = getPossibleNames(fileName)
            .flatMap { name ->
                FilenameIndex.getFilesByName(project, name, searchScope).map { it.virtualFile }
            }

        val resultWithCorrectDirectory = sources
            .filter { it.path.contains("$fileDirectory/$fileNameWithoutExt") }
        return resultWithCorrectDirectory.ifEmpty { sources }
    }

    private fun getPossibleNames(fileName: String): List<String> =
        if (fileName.endsWith(".d.ts"))
            listOf(fileName.replace(".d.ts", ".ts"), fileName.replace(".d.ts", ".js"))
        else listOf(fileName)
}