package pl.jsubczynski.sejmierz.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.jsubczynski.sejmierz.domain.error.DomainError
import pl.jsubczynski.sejmierz.ui.R

@Composable
fun DomainError.toMessage(): String = when (this) {
    is DomainError.Network -> stringResource(R.string.error_network)
    is DomainError.Http -> stringResource(R.string.error_http, code)
    is DomainError.Serialization -> stringResource(R.string.error_serialization)
    DomainError.NotCached -> stringResource(R.string.error_not_cached)
    DomainError.Empty -> stringResource(R.string.error_empty)
    is DomainError.Unknown -> stringResource(R.string.error_unknown)
}
