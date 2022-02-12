package plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import files.AttachDirectorySourcesProvider
import files.ProjectStateAwareFileOpener
import infra.PluginUtils.executeBackgroundTask
import infra.PluginUtils.metadata
import infra.SourceCacheDirectory.getPathToSourceCacheDirectory
import java.nio.file.Path
import java.nio.file.Paths


class SymlinkExistingSourcesAction :
    AnAction("Attach Sources Directory", "Attach directory with the sources from your computer", null) {

    override fun actionPerformed(e: AnActionEvent) {
        val (project, source, openedFile) = e.metadata()
        val fileOpener = project.service<ProjectStateAwareFileOpener>()
        val linkToExistingSourcesCreator = project.service<AttachDirectorySourcesProvider>()

        chooseDirectoryToLink(project)?.let { sourcePathDirectory ->
            executeBackgroundTask(project, "Creating a symbolic link to sources directory") {
                linkToExistingSourcesCreator.attachSourceDirectory(
                    sourcePathDirectory,
                    getPathToSourceCacheDirectory()
                )
                fileOpener.findAndOpenSourceFile(project, source, openedFile)
            }
        }
    }

    private fun chooseDirectoryToLink(project: Project): Path? {
        val homePath = Paths.get(FileUtil.expandUserHome("~/"))
        val homeDirectoryToSelect = VfsUtil.findFile(homePath, false)
        val sourcePath = FileChooser.chooseFile(
            FileChooserDescriptorFactory.createSingleFolderDescriptor(),
            project,
            homeDirectoryToSelect
        )
        return sourcePath?.toNioPath()
    }
}