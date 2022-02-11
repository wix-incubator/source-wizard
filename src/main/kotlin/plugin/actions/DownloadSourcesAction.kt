package plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import files.SourcesFinderService
import files.SourcesResultHandler
import infra.InternalPluginException
import infra.PluginUtils.metadata
import plugin.ui.PluginNotifications

class DownloadSourcesAction : AnAction("Download Sources", "Download sources for your npm module", null) {

    override fun actionPerformed(e: AnActionEvent) {
        try {
            val (project, source, initialFile) = e.metadata()
            val sourcesFinderService = project.service<SourcesFinderService>()
            val sourceFiles = sourcesFinderService.downloadRepositoryAndFind(project, initialFile)
            SourcesResultHandler.processFoundSources(source, initialFile, sourceFiles, true)
            PluginNotifications.notifyAboutDownloadedRepository(project)
        } catch (error: InternalPluginException) {
            PluginNotifications.notifyAboutMissingRepository()
        } catch (error: Exception) {
            PluginNotifications.notifyAboutFailure()
        }
    }
}