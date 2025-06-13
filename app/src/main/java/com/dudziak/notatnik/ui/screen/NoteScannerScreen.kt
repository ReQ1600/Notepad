package com.dudziak.notatnik.ui.screen

import android.net.Uri
import android.widget.Toast

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dudziak.notatnik.R
import com.dudziak.notatnik.viewModel.NoteScannerViewModel
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteScannerScreen(navController: NavController, viewModel: NoteScannerViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val content by viewModel.content

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_scan)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("menu") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                                R.string.close)
                        )
                    }
                }
            )
        }) { padding ->

        //prompting user to allow camera usage
        LaunchedEffect(Unit) {
            if (!cameraPermissionState.status.isGranted) {
                cameraPermissionState.launchPermissionRequest()
            }
        }

        if (cameraPermissionState.status.isGranted) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                AndroidView(factory = { previewView }, modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp))

                LaunchedEffect(previewView) {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        surfaceProvider = previewView.surfaceProvider
                    }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        viewModel.imageCapture
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.takeAndProcessPhoto() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.scan))
                }

                Button(
                    onClick = {
                        content?.let { txt ->
                            //navController.navigate("addReceipt")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = content != null
                ) {
                    Text(stringResource(R.string.scan_add))
                }


                Spacer(modifier = Modifier.height(16.dp))


                OutlinedTextField(
                    value = content.toString(),
                    onValueChange = {  },
                    label = { Text(stringResource(R.string.note_contents)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .verticalScroll(scrollState),
                    maxLines = Int.MAX_VALUE,
                    singleLine = false
                )
                //Text("${stringResource(R.string.rec_amnt)}: ${amount ?: stringResource(R.string.rec_not_found)}", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {
                Text(
                    text = stringResource(R.string.scan_no_access),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddReceiptScreen(navController: NavController, viewModel: ReceiptViewModel = viewModel(), transactionViewModel: TransactionViewModel, amnt: String) {
//    var amount by remember { mutableStateOf(amnt) }
//    var description by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val rec_added = stringResource(R.string.rec_added)
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(stringResource(R.string.add_trans)) },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        navController.popBackStack("scanner", inclusive = false)
//                    }) {
//                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.trans_screen_close))
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(modifier = Modifier
//            .padding(padding)
//            .padding(16.dp)) {
//            OutlinedTextField(
//                value = amount,
//                onValueChange = { amount = it },
//                label = { Text(stringResource(R.string.trans_amnt)) },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = description,
//                onValueChange = { description = it },
//                label = { Text(stringResource(R.string.trans_desc)) },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row {
//                Button(onClick = {
//                    transactionViewModel.addTransaction(
//                        amount = amount.toDoubleOrNull() ?: 0.0,
//                        type = TransactionType.EXPENSE,
//                        description = description.ifBlank { null }
//                    )
//                    Toast.makeText(context, rec_added, Toast.LENGTH_SHORT).show()
//                    navController.popBackStack("scanner", inclusive = false)
//                },
//                    modifier = Modifier.fillMaxWidth()) {
//                    Text(stringResource(R.string.add_expense))
//                }
//            }
//        }
//    }
//}