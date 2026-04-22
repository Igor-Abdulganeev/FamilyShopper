package ru.gorinih.familyshopper.domain.usecases

import okio.IOException
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 22.04.2026
 */

interface UpdateUserUseCase {
    suspend operator fun invoke(): Results
}

class UpdateUserUseCaseImpl(
    private val pref: StorageRepository,
    private val remote: RemoteRepository
) : UpdateUserUseCase {
    override suspend fun invoke(): Results = try {
        remote.setUserName()
        Results(isError = false)
    } catch (_: IOException) {
        if (pref.getGroupUUID().isNotBlank())
            Results(isError = true, textErrorResource = R.string.error_network_state)
        else Results(isError = false)
    } catch (ex: Throwable) {
        Results(isError = true, textError = ex.localizedMessage ?: "")
    }
}
