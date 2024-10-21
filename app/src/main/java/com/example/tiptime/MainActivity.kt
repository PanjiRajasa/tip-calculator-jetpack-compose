/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tiptime

//import
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusDirection
import kotlin.math.ceil
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size

//function utama
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            //manggilnya disini
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

//fungsi untuk textfield
//yang dalam () isinya parameter, {} isinya isi fungsinya
@Composable
fun EditNumberField(
    @StringRes label: Int, //ni parameternya ngambil agar
    // nilainya cuman bisa pakai apa yang ada di strings.xml
    //parameter value
    value: String,
    //parameter onValueChanged
    onValueChanged: (String) -> Unit,
    //parameter modifier
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions

) {
    //fungsi ini manggil TextField
    TextField(
        //nilai property TextField setara/ = dengan parameter fungsi
        value = value,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        modifier = modifier,
        //nyetel keyboard
        keyboardOptions = keyboardOptions, // Gunakan keyboardOptions di sini
        keyboardActions = keyboardActions // Masukkan keyboardActions di sini
    )
}

//fungsi pembulatan, jika user mau membulatkan tip
@Composable
fun RoundTip(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.round_tip))
    }
}

//fungsi untuk menampilkan teks, dalamnya dipanggil fungsi TextField
@Composable
fun TipTimeLayout() {

    //variable buat state agar nilai TextField bisa berubah-ubah dan terus terupdate
    //dikasih 'by remember {mutableStateOf()}' biar nilainya bisa berubah"
    var amountInput by remember { mutableStateOf("") }
    var tipInput by remember { mutableStateOf("") }

    //variable buat ngitung tip
    //disini makai elvis operator gini cara kerjanya: misalkan
    //var amount = amountInput.toDoubleOrNull(): 0,0

    //itu amount adalah namanya, amountInput ngambil dari variabel lain
    // .toDoubleOrNull bakal ngubah ke Double, tapi kalau misal datanya engga double
    // kayak misal "abc" maka akan jadi Null, dan disebelahnya ada elvis/ ?:
    // ya kalau semial hasil amountInput.toDoubleOrNull nya null maka yang disebelah kanan
    // bakal jadi nilai defaultnya yakni 0,0

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0

    val tip = calculateTip(amount, tipPercent) // ini buat ngitung tip
    // FocusManager untuk menangani peralihan fokus
    val focusManager = LocalFocusManager.current

    //maggil Column(parameter) {isinya}
    Column(

        //dibawah ini biar ketengah pas
        modifier = Modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(alignment = Alignment.Start)
        )

        //buat TextField pakai fungsi EditNumberField
        // onValueChanged = { variable = it} tujuannya supaya nilai input disimpan dalam variablenya "it" itu gunanya menetapkan nilai variable ngikutin nilai terbaru TextField

        EditNumberField(
            label = R.string.bill_amount,
            value = amountInput,
            onValueChanged = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            // Action untuk pindah fokus ke TextField berikutnya
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        EditNumberField(
            label = R.string.how_was_the_service,
            value = tipInput,
            onValueChanged = { tipInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            // Action untuk menutup keyboard ketika "Done" ditekan
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )

        )
        Text(
            text = stringResource(R.string.tip_amount, tip),
            //style tu untuk menerapkan gaya ke text nya
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}

//fungsi ngitung tip
private fun calculateTip(amount: Double, tipPercent: Double = 15.0): String {
    val tip = tipPercent / 100 * amount
    return NumberFormat.getCurrencyInstance().format(tip) // ini buat format mata uang 🤑💲💸💰
}

//fungsi buat preview doang
@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
