package files.impl

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import files.FileFinderService
import files.SourcesFinderService
import infra.MissingRepositoryUrlException
import infra.PluginRepositoryManager
import npmmodules.NpmModuleFileExaminer

class IndexedSourcesFinderService(project: Project) : SourcesFinderService {

    private val pluginRepositoryManager = project.service<PluginRepositoryManager>()
    private val fileFinderService = project.service<FileFinderService>()
    private val npmModuleFileExaminer = project.service<NpmModuleFileExaminer>()


    override fun find(project: Project, file: VirtualFile): List<VirtualFile> {
        val npmModuleDirectory = npmModuleFileExaminer.extractNpmModuleDirectory(file)
        val sourceFiles = findSourceFiles(project, npmModuleDirectory, file)

        if (sourceFiles.isNotEmpty()) {
            val repositoryUrl = npmModuleFileExaminer.extractRepositoryUrl(file)
            repositoryUrl?.let {
                pluginRepositoryManager.pullRepositoryAsync(repositoryUrl)
            }
        }
        return sourceFiles
    }

    override fun downloadRepositoryAndFind(project: Project, file: VirtualFile): List<VirtualFile> {
        val repositoryUrl = npmModuleFileExaminer.extractRepositoryUrl(file) ?: throw MissingRepositoryUrlException()
        pluginRepositoryManager.cloneRepositorySync(repositoryUrl)

        val npmModule = npmModuleFileExaminer.extractNpmModuleDirectory(file)
        return findSourceFiles(project, npmModule, file)
    }

    private fun findSourceFiles(
        project: Project,
        module: VirtualFile,
        file: VirtualFile,
    ): List<VirtualFile> = fileFinderService.findFile(project, module.name, file.name, file.parent.name)

}