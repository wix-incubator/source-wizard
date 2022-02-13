import com.intellij.mock.MockProjectEx
import com.intellij.openapi.Disposable

/**
 * Utility object with allows creating [com.intellij.openapi.project.Project] instance with
 * provided mocked service dependencies.
 * Plugin services does not accept any constructor arguments other than single Project instance
 * therefore this class can be helpful to provide required dependencies as mocks to your service
 * instance during unit tests.
 */
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
