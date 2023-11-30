package com.blueduck.dajumgum.ui.bottombar.inspection

 import android.widget.Toast
 import androidx.compose.foundation.background
 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Spacer
 import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.layout.height
 import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.lazy.LazyColumn
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.foundation.text.KeyboardOptions
 import androidx.compose.material3.ButtonDefaults
 import androidx.compose.material3.CircularProgressIndicator
 import androidx.compose.material3.ElevatedButton
 import androidx.compose.material3.OutlinedTextField
 import androidx.compose.material3.Scaffold
 import androidx.compose.material3.Text
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.LaunchedEffect
 import androidx.compose.runtime.collectAsState
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.mutableStateOf
 import androidx.compose.runtime.remember
 import androidx.compose.runtime.setValue
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.platform.LocalContext
 import androidx.compose.ui.res.colorResource
 import androidx.compose.ui.text.input.ImeAction
 import androidx.compose.ui.unit.dp
 import androidx.constraintlayout.compose.ConstraintLayout
 import androidx.navigation.NavHostController
 import com.blueduck.dajumgum.R
 import com.blueduck.dajumgum.ui.common.BackButtonAppBar
 import com.blueduck.dajumgum.enums.DefectType
 import com.blueduck.dajumgum.model.ACDefect
 import com.blueduck.dajumgum.preferences.DataStoreManager
 import com.blueduck.dajumgum.ui.bottombar.HomeViewModel
 import com.blueduck.dajumgum.ui.common.CustomSpinner
 import java.util.UUID

@Composable
fun CreateAirConditionerDefectScreen(navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val customer = viewModel.currentCustomer
    val user = DataStoreManager.getUser(context = context).collectAsState(initial = null)

    var isLoading by remember { mutableStateOf(false) }


    var hcho by remember { mutableStateOf("") }
    var tvoc by remember { mutableStateOf("") }
    var radon by remember { mutableStateOf("") }

    var selectedCategoryTags by remember { mutableStateOf(listOf<String>()) }
    var selectedStatusTags by remember { mutableStateOf(listOf<String>()) }

    if (viewModel.category  != null){
        if (!selectedCategoryTags.contains(viewModel.category)) {
            selectedCategoryTags = selectedCategoryTags + viewModel.category!!
        }
    }

    LaunchedEffect(key1 = user.value, block = {
        viewModel.getCategoryTags(context = context)
        viewModel.getStatusTags(context = context)
    })

    val categoryTags = viewModel.categoryTags
    val statusTags = viewModel.statusTags


    LaunchedEffect(key1 = viewModel.acDefectToEdit, block = {
        viewModel.acDefectToEdit?.let { defect ->
            selectedCategoryTags = selectedCategoryTags + defect.category
            hcho = defect.hcho
            tvoc = defect.tvoc
            radon = defect.radon
            selectedStatusTags = selectedStatusTags + defect.status
        }
    })

    fun isInputValid(): Boolean {

        // Check if the category is not empty
        if (selectedCategoryTags.isEmpty()) {
            Toast.makeText(
                context,
                "카테고리를 입력해주세요",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the HCHO is not empty
        if (hcho.isEmpty()) {
            Toast.makeText(
                context,
                "HCHO를 입력하십시오.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the TVOC is valid
        if (tvoc.isEmpty()) {
            Toast.makeText(
                context,
                "TVOC를 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the RADON is valid
        if (radon.isEmpty()) {
            Toast.makeText(
                context,
                "라돈을 입력해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Check if the status is not empty
        if (selectedStatusTags.isEmpty()) {
            Toast.makeText(
                context,
                "상태를 선택하세요.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // All validations passed, return true
        return true
    }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BackButtonAppBar {
                navController.popBackStack()
            }
        },
    ) { innerPadding ->
        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val (mainContent, progressBar) = createRefs()

            LazyColumn(
                content = {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {

                            Text(
                                text = "Category", modifier = Modifier
                                    .fillMaxWidth().padding(vertical = 8.dp)
                            )
                            CustomSpinner(categoryTags, selectedCategoryTags, true, onTagSelection = { tag ->
                                selectedCategoryTags = selectedCategoryTags + tag
                            }, onTagRemove = {  } )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "HCHO*", modifier = Modifier
                                .fillMaxWidth().padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = hcho,
                                onValueChange = { hcho = it },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 55.dp),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "TVOC", modifier = Modifier
                                .fillMaxWidth().padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = tvoc,
                                onValueChange = { tvoc = it },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 55.dp),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "RADON", modifier = Modifier
                                .fillMaxWidth().padding(vertical = 8.dp))
                            OutlinedTextField(
                                value = radon,
                                onValueChange = { radon = it },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 55.dp),
                                shape = RoundedCornerShape(
                                    topStart = 10.dp,
                                    topEnd = 10.dp,
                                    bottomStart = 10.dp,
                                    bottomEnd = 10.dp
                                ),
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Status", modifier = Modifier
                                    .fillMaxWidth().padding(vertical = 8.dp)
                            )
                            CustomSpinner(statusTags, selectedStatusTags,
                                onTagSelection = { tag ->
                                    selectedStatusTags = listOf(tag)
                                }, onTagRemove = { tag ->
                                    selectedStatusTags = listOf(tag)
                                }
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            ElevatedButton(
                                onClick = {
                                    if (isInputValid()){
                                        val defect = ACDefect(
                                            id = viewModel.acDefectToEdit?.id ?: UUID.randomUUID().toString(),
                                            customerId = customer.id,
                                            category = selectedCategoryTags.joinToString(separator = ""),
                                            defectType = DefectType.AC.value,
                                            hcho = hcho,
                                            tvoc = tvoc,
                                            radon = radon,
                                            status = selectedStatusTags[0],
                                            createdAt = (System.currentTimeMillis() / 1000),
                                            updatedAt = (System.currentTimeMillis() / 1000)
                                        )
                                        isLoading = true
                                        viewModel.createACDefect(defect, user.value!!.id,
                                            onSuccess = {
                                                isLoading = false
                                                navController.popBackStack()
                                            }, onFailure = {
                                                isLoading = false
                                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                            })
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.orange)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = "저장")
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                        }

                    }
                })


            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(color = Color.Transparent)
                        .constrainAs(progressBar) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        },
                    color = colorResource(id = R.color.orange)
                )
            }
        }
     }

}