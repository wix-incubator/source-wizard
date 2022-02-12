package infra

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object PluginUtils {

    fun AnActionEvent.metadata(): Triple<Project, FileEditorManager, VirtualFile> {
        val project = this.project!!
        val source = FileEditorManager.getInstance(project)
        val selectedFile = source.selectedEditor?.file!!

        return Triple(project, source, selectedFile)
    }

    fun executeBackgroundTask(project: Project, title: String, runnable: Runnable) {
        val task = object : Task.Backgroundable(project, title) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = title
                runnable.run()
            }
        }
        ProgressManager.getInstance().run(task)
    }

}
