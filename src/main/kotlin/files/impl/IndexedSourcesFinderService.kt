package files.impl

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import files.FileFinderService
import files.SourcesFinderService
import sourcecache.SourceCacheRefresher
import npmmodules.NpmModuleFileExaminer

class IndexedSourcesFinderService(project: Project) : SourcesFinderService {

    private val sourceCacheRefresher = project.service<SourceCacheRefresher>()
    private val fileFinderService = project.service<FileFinderService>()
    private val npmModuleFileExaminer = project.service<NpmModuleFileExaminer>()


    override fun find(project: Project, file: VirtualFile): List<VirtualFile> {
        val npmModuleDirectory = npmModuleFileExaminer.extractNpmModuleDirectory(file)
        val sourceFiles = fileFinderService.findFile(project, npmModuleDirectory.name, file.name, file.parent.name)

        if (sourceFiles.isNotEmpty()) {
            val repositoryUrl = npmModuleFileExaminer.extractRepositoryUrl(file)
            repositoryUrl?.let {
                sourceCacheRefresher.refreshSourceCache(repositoryUrl)
            }
        }
        return sourceFiles
    }

}