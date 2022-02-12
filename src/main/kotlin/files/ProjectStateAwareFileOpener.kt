package files

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import plugin.ui.PluginNotifications
import java.nio.file.Paths

class ProjectStateAwareFileOpener(project: Project) {

    private val sourcesFinderService = project.service<SourcesFinderService>()

    fun findAndOpenSourceFile(project: Project, source: FileEditorManager, openedFile: VirtualFile) =
        executeAfterRefresh {
            val sourceFiles = sourcesFinderService.find(project, openedFile)
            if (sourceFiles.isEmpty()) {
                PluginNotifications.notifyAboutAddedSources(project)
                DumbService.getInstance(project).runWhenSmart {
                    SourcesResultHandler.processFoundSources(
                        source,
                        openedFile,
                        sourcesFinderService.find(project, openedFile),
                        true
                    )
                }
            } else {
                SourcesResultHandler.processFoundSources(source, openedFile, sourceFiles, true)
            }
        }

    private fun executeAfterRefresh(runnable: Runnable) {
        ReadAction.run<Throwable> {
            val homePath = Paths.get(FileUtil.expandUserHome("~/"))
            VfsUtil.findFile(homePath, false)?.refresh(true, true, runnable)
        }
    }
}