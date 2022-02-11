package infra

import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project

import plugin.ui.PluginNotifications
import repository.RepositoryService

class PluginRepositoryManager(val project: Project) {

    private val repositoryService: RepositoryService = project.service()

    fun pullRepositoryAsync(repositoryUrl: String) {
        val pullTask = object : Task.Backgroundable(project, "Refreshing source cache") {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Refreshing source cache"
                repositoryService.pullRepository(repositoryUrl)
            }
        }
        ProgressManager.getInstance().run(pullTask)
    }

    fun cloneRepositorySync(repositoryUrl: String) {
        ProgressManager.getInstance()
            .runProcessWithProgressSynchronously<String, InternalPluginException>({
                val repositoryPath = repositoryService.cloneRepository(repositoryUrl)
                val root = SourceCacheDirectory.getSourceCacheDirectoryFile()
                root.refresh(false, true)
                repositoryPath
            }, "Downloading sources", false, project)
    }
}