package plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import files.AttachDirectorySourcesProvider
import files.SourcesFinderService
import files.SourcesResultHandler
import infra.PluginUtils.metadata
import infra.SourceCacheDirectory
import infra.SourceCacheDirectory.getPathToSourceCacheDirectory
import java.nio.file.Path
import java.nio.file.Paths


class SymlinkExistingSourcesAction :
    AnAction("Attach Sources Directory", "Attach directory with the sources from your computer", null) {
    override fun actionPerformed(e: AnActionEvent) {
        val (project, source, openedFile) = e.metadata()

        val sourcePathDirectory = chooseDirectoryToLink(project)
        createLink(project, sourcePathDirectory)


        val source = FileEditorManager.getInstance(project)
        val editor = source.selectedEditor
        val openedFile = editor?.file!!
        val sourcesFinderService = project.service<SourcesFinderService>()

        val sourceFiles = sourcesFinderService.find(project, openedFile)
        SourcesResultHandler.processFoundSources(source, openedFile, sourceFiles, true)
    }

    private fun chooseDirectoryToLink(project: Project): Path {
        val homePath = Paths.get(FileUtil.expandUserHome("~/"))
        val homeDirectoryToSelect = VfsUtil.findFile(homePath, false)
        val sourcePath = FileChooser.chooseFile(
            FileChooserDescriptorFactory.createSingleFolderDescriptor(),
            project,
            homeDirectoryToSelect
        )!!
        return sourcePath.toNioPath()
    }

    private fun createLink(project: Project, sourcePath: Path) {
        val linkToExistingSourcesCreator = project.getService(AttachDirectorySourcesProvider::class.java)

        ProgressManager.getInstance().runProcessWithProgressSynchronously({
            linkToExistingSourcesCreator.attachSourceDirectory(sourcePath, getPathToSourceCacheDirectory())
        }, "Creating a symbolic link to sources directory", false, project)
    }

}