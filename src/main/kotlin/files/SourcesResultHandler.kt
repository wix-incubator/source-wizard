package files

import com.intellij.ide.DataManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import plugin.ui.PluginNotifications
import plugin.ui.FileListPopup

object SourcesResultHandler {

    fun processFoundSources(
        source: FileEditorManager,
        initialFile: VirtualFile,
        sourceFiles: List<VirtualFile>,
        notifyAboutFailure: Boolean = false
    ): Unit = when {
        sourceFiles.isEmpty() && notifyAboutFailure ->
            PluginNotifications.notifyAboutFailure()
        sourceFiles.isEmpty() -> {
        }
        sourceFiles.size == 1 -> {
            source.openFile(sourceFiles.first(), true)
            source.closeFile(initialFile)
        }
        else -> {
            val listPopup = JBPopupFactory.getInstance()
                .createListPopup(FileListPopup(source, initialFile, sourceFiles.toMutableList()))
            listPopup.showInBestPositionFor(
                DataManager.getInstance().getDataContext(source.selectedEditor?.component!!)
            )
        }
    }

}