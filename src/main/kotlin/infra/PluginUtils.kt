package infra

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Paths

object PluginUtils {

    fun AnActionEvent.metadata(): Triple<Project, FileEditorManager, VirtualFile> {
        val project = this.project!!
        val source = FileEditorManager.getInstance(project)
        val selectedFile = source.selectedEditor?.file!!

        return Triple(project, source, selectedFile)
    }

}
