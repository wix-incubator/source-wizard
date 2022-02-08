import com.intellij.mock.MockProjectEx
import com.intellij.openapi.Disposable

object MockProject {

    fun mockProject(): MockProjectEx = MockProjectEx(TestDisposable())

    fun <T : Any> mockProjectWithService(serviceInterface: Class<T>, serviceImplementation: T): MockProjectEx {
        val project = MockProjectEx(TestDisposable())
        project.registerService(serviceInterface, serviceImplementation)

        return project
    }

    fun <T : Any> MockProjectEx.withService(
        serviceInterface: Class<T>,
        serviceImplementation: T
    ): MockProjectEx {
        this.registerService(serviceInterface, serviceImplementation)

        return this
    }

    private class TestDisposable : Disposable {
        @Volatile
        var isDisposed = false
            private set

        override fun dispose() {
            isDisposed = true
        }

    }
}
