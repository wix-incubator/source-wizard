package plugin.extensions

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile
import files.SourcesFinderService
import plugin.ui.PluginNotifications
import npmmodules.NpmModuleFileExaminer

class NpmModuleFileActionListener : FileEditorManagerListener {

    override fun fileOpenedSync(
        source: FileEditorManager,
        openedFile: VirtualFile,
        editors: Pair<Array<FileEditor>, Array<FileEditorProvider>>
    ) {
        val project = source.project
        val npmModuleFileExaminer = project.service<NpmModuleFileExaminer>()
        if (npmModuleFileExaminer.isFromNpmModule(openedFile)) {
            val sourcesFinderService = project.service<SourcesFinderService>()
            val sourceFiles = sourcesFinderService.find(project, openedFile)

            if (sourceFiles.isNotEmpty()) {
                PluginNotifications.notifyAboutAvailableSources(project, source, openedFile, sourceFiles)
            } else {
                PluginNotifications.notifyAboutSourcesAvailableToDownload(project, openedFile)
            }
        }
    }
}
