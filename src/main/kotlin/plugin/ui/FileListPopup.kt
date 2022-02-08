package plugin.ui

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.popup.*
import com.intellij.openapi.vfs.VirtualFile
import infra.SourceCacheDirectory.sourceCacheDirectoryName
import javax.swing.Icon

class FileListPopup(
    private val source: FileEditorManager,
    private val initialFile: VirtualFile,
    private val values: List<VirtualFile>
) : ListPopupStep<VirtualFile> {
    override fun getTitle(): String {
        return "Ambiguous Sources. Select Correct File"
    }

    override fun onChosen(selectedValue: VirtualFile?, finalChoice: Boolean): PopupStep<*>? {
        source.openFile(selectedValue!!, true)
        source.closeFile(initialFile)
        return null
    }

    override fun hasSubstep(selectedValue: VirtualFile?): Boolean {
        return false
    }

    override fun canceled() {
    }

    override fun isMnemonicsNavigationEnabled(): Boolean {
        return false
    }

    override fun getMnemonicNavigationFilter(): MnemonicNavigationFilter<VirtualFile>? {
        return null
    }

    override fun isSpeedSearchEnabled(): Boolean {
        return false
    }

    override fun getSpeedSearchFilter(): SpeedSearchFilter<VirtualFile>? {
        return null
    }

    override fun isAutoSelectionEnabled(): Boolean {
        return false
    }

    override fun getFinalRunnable(): Runnable? {
        return null
    }

    override fun getValues(): MutableList<VirtualFile> {
        return values.toMutableList()
    }

    override fun isSelectable(value: VirtualFile?): Boolean {
        return true
    }

    override fun getIconFor(value: VirtualFile?): Icon? {
        return null
    }

    override fun getTextFor(value: VirtualFile?): String {
        return prettyName(value!!)
    }

    private fun prettyName(value: VirtualFile): String {
        if (value.parent.name == sourceCacheDirectoryName) {
            return value.name
        }
        return "${prettyName(value.parent)}/${value.name}"
    }

    override fun getSeparatorAbove(value: VirtualFile?): ListSeparator? {
        return null
    }

    override fun getDefaultOptionIndex(): Int {
        return 0
    }

}