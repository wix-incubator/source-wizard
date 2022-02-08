package plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import files.SourcesFinderService
import files.SourcesResultHandler
import plugin.ui.PluginNotifications
import infra.InternalPluginException

class DownloadSourcesAction : AnAction("Download Sources", "Download sources for your npm module", null) {

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val project = e.project!!
            val source = FileEditorManager.getInstance(project)
            val editor = source.selectedEditor
            val initialFile = editor?.file!!

            val sourcesFinderService = project.service<SourcesFinderService>()
            val sourceFiles = sourcesFinderService.downloadRepositoryAndFind(project, initialFile)
            SourcesResultHandler.processFoundSources(source, initialFile, sourceFiles, true)
        } catch (error: InternalPluginException) {
            PluginNotifications.notifyAboutMissingRepository()
        } catch (error: Exception) {
            PluginNotifications.notifyAboutFailure()
        }
    }
}