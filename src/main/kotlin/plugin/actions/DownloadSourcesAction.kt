package plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import files.ProjectStateAwareFileOpener
import infra.InternalPluginException
import infra.MissingRepositoryUrlException
import infra.PluginUtils.executeBackgroundTask
import infra.PluginUtils.metadata
import npmmodules.NpmModuleFileExaminer
import plugin.ui.PluginNotifications
import repository.RepositoryService

class DownloadSourcesAction :
    AnAction("Download Sources", "Download sources for your npm module", null) {


    override fun actionPerformed(e: AnActionEvent) {
        val (project, source, initialFile) = e.metadata()

        val repositoryService = project.service<RepositoryService>()
        val projectStateAwareFileOpener = project.service<ProjectStateAwareFileOpener>()
        val npmModuleFileExaminer = project.service<NpmModuleFileExaminer>()

        executeBackgroundTask(project, "Cloning source repository") {
            withHandledErrors {
                val repositoryUrl =
                    npmModuleFileExaminer.extractRepositoryUrl(initialFile) ?: throw MissingRepositoryUrlException()

                repositoryService.cloneRepository(repositoryUrl)
                projectStateAwareFileOpener.findAndOpenSourceFile(project, source, initialFile)
            }
        }
    }

    private fun withHandledErrors(runnable: Runnable) {
        try {
            runnable.run()
        } catch (error: InternalPluginException) {
            PluginNotifications.notifyAboutMissingRepository()
        } catch (error: Exception) {
            PluginNotifications.notifyAboutFailure()
        }
    }
}