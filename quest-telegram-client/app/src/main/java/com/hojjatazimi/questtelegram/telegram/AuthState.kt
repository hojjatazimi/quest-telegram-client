package com.hojjatazimi.questtelegram.telegram

sealed class AuthState {
    data object Uninitialized : AuthState()
    data object WaitingForPhoneNumber : AuthState()
    data object WaitingForCode : AuthState()
    data object WaitingForPassword : AuthState()
    data object Ready : AuthState()
    data object LoggingOut : AuthState()
    data object Closed : AuthState()
    data class Error(val message: String) : AuthState()
}
