package ru.gorinih.familyshopper.domain.usecases

import familyshopper.shared.generated.resources.Res
import familyshopper.shared.generated.resources.error_network_state
import okio.IOException
import org.jetbrains.compose.resources.getString
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.PreferenceRepository
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 22.04.2026
 */

interface UpdateUserUseCase {
    suspend operator fun invoke(): Results
}

class UpdateUserUseCaseImpl(
    private val pref: PreferenceRepository,
    private val remote: RemoteRepository,
) : UpdateUserUseCase {
    override suspend fun invoke(): Results = try {
        remote.setUserName()
        Results(isError = false)
    } catch (_: IOException) {
        if (pref.getGroupUUID().isNotBlank())
            Results(
                isError = true,
                isNetworkError = true,
                textError = getString(Res.string.error_network_state)
            )
        else Results(isError = false)
    } catch (ex: Throwable) {
        Results(isError = true, textError = ex.localizedMessage ?: "")
    }
}
