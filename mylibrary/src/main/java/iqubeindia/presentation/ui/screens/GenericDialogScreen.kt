package com.tvsm.iqubeindia.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import com.tvsm.connect.R
import com.tvsm.iqubeindia.presentation.ui.utils.ErrorCode

@Composable
fun GenericDialogScreen(
    mErrorCode: ErrorCode?,
    title: String = stringResource(id = R.string.app_name),
    msg: String? = null,
    positiveButtonText : String = stringResource(id = R.string.btn_close),
    onPositiveClick: () -> Unit,
    onNegativeClick: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var mTitle = title
    var mMsg = msg

    when(mErrorCode) {
        ErrorCode.WATCH_NOT_CONNECTED_TO_MOBILE -> {
            mTitle = stringResource(id = R.string.title_watch_not_connected_to_mobile)
            mMsg = stringResource(id = R.string.msg_watch_not_connected_to_mobile)
        }
        ErrorCode.WATCH_CONNECTED_TO_MOBILE_APP_NOT_INSTALLED -> {
            mTitle = stringResource(id = R.string.title_watch_connected_to_mobile_app_not_installed)
            mMsg = stringResource(id = R.string.msg_watch_connected_to_mobile_app_not_installed)
        }
        ErrorCode.WATCH_NOT_CONNECTED_TO_MOBILE_USER_NOT_SIGNED -> {
            mTitle = stringResource(id = R.string.title_watch_not_connected_to_mobile_user_not_signed)
            mMsg = stringResource(id = R.string.msg_watch_not_connected_to_mobile_user_not_signed)
        }
        ErrorCode.WATCH_NOT_CONNECTED_TO_INTERNET -> {
            mTitle = stringResource(id = R.string.title_watch_not_connected_to_internet)
        }
        ErrorCode.USER_ACCESS_REVOKED -> {
            mTitle = stringResource(id = R.string.title_user_access_revoked)
        }
        ErrorCode.API_ERROR -> {
            mTitle = stringResource(id = R.string.title_api_error)
            mMsg = stringResource(id = R.string.msg_api_error)
        }
        ErrorCode.WATCH_NOT_CONNECTED_TO_MOBILE_VIA_BLE -> {
            mTitle = stringResource(id = R.string.title_watch_not_connected_to_mobile_via_ble)
            mMsg = stringResource(id = R.string.msg_watch_not_connected_to_mobile_via_ble)
        }
        ErrorCode.VEHICLE_NOT_CONNECTED_TO_MOBILE_VIA_BLE -> {
            mTitle = stringResource(id = R.string.title_vehicle_not_connected_to_mobile_via_ble)
            mMsg = stringResource(id = R.string.msg_vehicle_not_connected_to_mobile_via_ble)
        }
        ErrorCode.REMOTE_ACTION_FAILED -> {
            mTitle = stringResource(id = R.string.title_remote_action_failed)
            mMsg = stringResource(id = R.string.msg_remote_action_failed)
        }
        ErrorCode.VEHICLE_NOT_COMPATIBLE -> {
            mTitle = stringResource(id = R.string.title_vehicle_is_not_compatible)
            mMsg = stringResource(id = R.string.vehicle_is_not_compatible)
        }
        ErrorCode.NO_VEHICLE_FOUND -> {
            mTitle = stringResource(id = R.string.title_vehicle_is_not_found)
            mMsg = stringResource(id = R.string.vehicle_is_not_found)
        }
        else -> {}
    }
    Dialog(
        showDialog = true,
        onDismissRequest = onDismiss
    ) {
        Alert(
            icon = {},
            title = {
                Text(
                    text = mTitle,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onBackground
                )
            },
            negativeButton = {
                if (onNegativeClick != null) {
                    Button(
                        modifier = Modifier.padding(bottom = 15.dp, start = 8.dp),
                        onClick = onNegativeClick,
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "no"
                        )
                    }
                }
            },
            positiveButton = {
                if (onNegativeClick == null) {
                    Button(
                        // added padding to buttons so it doesn't go out screen if user increase the font size in device
                        modifier = Modifier.fillMaxSize().padding(end = 12.dp),
                        onClick = onPositiveClick,
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.alertButtonBackground))
                    ) {
                        Text(text = positiveButtonText,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onBackground)
                    }
                } else {
                    Button(
                        // added padding to buttons so it doesn't go out screen if user increase the font size in device
                        modifier = Modifier.padding(bottom = 15.dp, start = 8.dp),
                        shape = CircleShape,
                        onClick = onPositiveClick,
                        colors = ButtonDefaults.primaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "yes"
                        )
                    }
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                mMsg?.let {
                    Text(
                        text = mMsg,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun SaveResultPreview() {
    GenericDialogScreen(
        mErrorCode = null,
        onPositiveClick = { },
        onNegativeClick = { },
        title = "Test",
        msg = "Test",
        onDismiss = {},
    )
}