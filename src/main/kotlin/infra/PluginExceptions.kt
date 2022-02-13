package infra

sealed class InternalPluginException(message: String?) : RuntimeException(message)

class MissingRepositoryUrlException : InternalPluginException("Could not find repository url")

class FailedToFindSourceRepositoryException :
    InternalPluginException("Failed to find source repository for given npm module")

class MissingNpmModuleException :
    InternalPluginException("Failed to find directory with package.json file in given path")

class GitOperationException(message: String) : InternalPluginException(message)

