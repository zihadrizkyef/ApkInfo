package com.zhd.apkinfo

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.zhd.apkinfo.ui.theme.ApkInfoTheme


class MainActivity : ComponentActivity() {
    private val listApp = arrayListOf<Data>()
    private val filteredApp = arrayListOf<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        listApp.addAll(packageManager.getInstalledApplications(PackageManager.GET_META_DATA).map {
            Data(
                packageManager.getApplicationIcon(it).toBitmap(),
                packageManager.getApplicationLabel(it).toString(),
                it.packageName,
            )
        }.sortedBy { it.name })
        filteredApp.addAll(listApp)

        setContent {
            ApkInfoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    var text by remember { mutableStateOf("") }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            val focusManager = LocalFocusManager.current
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(onSearch = {
                                    focusManager.clearFocus()
                                }),
                                trailingIcon = {
                                    if (text.isNotEmpty()) {
                                        IconButton(onClick = {
                                            text = ""
                                            filteredApp.clear()
                                            filteredApp.addAll(listApp.filter {
                                                it.name.contains(text, true) || it.packageName.contains(text, true)
                                            })
                                            focusManager.clearFocus()
                                        }) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_clear), contentDescription = "Clear", tint = Color.Gray
                                            )
                                        }
                                    }
                                },
                                onValueChange = { value ->
                                    text = value
                                    filteredApp.clear()
                                    filteredApp.addAll(listApp.filter {
                                        it.name.contains(text, true) || it.packageName.contains(text, true)
                                    })
                                },
                                label = {
                                    Text("Search name...")
                                },
                                value = text,
                            )
                        }
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            items(filteredApp) {
                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent()
                                        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        intent.data = Uri.fromParts("package", it.packageName, null)

                                        if (intent.resolveActivity(packageManager) != null) {
                                            startActivity(intent)
                                        }
                                    }) {
                                    Row(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .width(40.dp)
                                                .height(40.dp)
                                                .padding(8.dp),
                                            bitmap = it.image.asImageBitmap(),
                                            contentDescription = "",
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(text = it.name)
                                            Text(text = it.packageName)
                                        }
                                    }
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp, 0.dp)
                                            .height(1.dp)
                                            .background(Color.LightGray)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    data class Data(
        val image: Bitmap,
        val name: String,
        val packageName: String,
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ApkInfoTheme {}
}