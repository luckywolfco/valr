package co.luckywolf.valr.api

import arrow.core.*
import co.luckywolf.valr.protocol.ApiTypes

object ApiFunctions {

  val matchPathToStartsWithMatchingRule: (String, String) -> Boolean =
    { path, matchingRule -> path.startsWith(matchingRule, ignoreCase = true) }

  val matchPathToRegexMatchingRule: (String, String) -> Boolean =
    { path, matchingRule -> matchingRule.toRegex(RegexOption.IGNORE_CASE) matches path }

  fun matchPathToPermissionRule(
    path: String,
    rule: ApiTypes.ApiPermissionRule,
    matchingStrategy: (String, String) -> Boolean
  ): Option<ApiTypes.ApiPermission> {
    return when {
      rule.pathMatchingRules.firstOrNull {
        matchingStrategy(
          path,
          it
        )
      } != null -> Option(rule.apiPermission)
      else -> none()
    }
  }

  fun matchPathToPermissionRules(
    path: String,
    matchingStrategy: (String, String) -> Boolean
  ): Option<ApiTypes.ApiPermission> {

    return ApiTypes.ApiPermissionRule.values().firstOrNull {
      matchPathToPermissionRule(path, it, matchingStrategy).isNotEmpty()
    }.toOption().map {
      Option(it.apiPermission)
    }.getOrElse {
      none()
    }
  }

  fun verifyUserPermissionsFor(userPermissions: List<ApiTypes.ApiPermission>, permission: ApiTypes.ApiPermission):
    Either<ApiTypes.Validators.ApiError, Boolean> {

    return if (userPermissions.contains(permission)) {
      Either.Right(true)
    } else {
      Either.Left(ApiTypes.Validators.ApiError.InsufficientPermissions(permission.permission))
    }
  }
}
