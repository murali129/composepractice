package com.murali.composepractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.murali.composepractice.data.Quotation
import com.murali.composepractice.ui.screens.rowItem

import com.murali.composepractice.ui.theme.ComposepracticeTheme
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : ComponentActivity() {
    class Repo {
        val baseUrl = "https://quotable.io/"

        private fun getInstance(): Retrofit {
            return Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        interface ItemsApi {
            @GET("/quotes")
            suspend fun getItems() : Response<Quotation>
        }

        suspend fun getListItems() : Quotation? {
            val itemListsApi = getInstance().create(ItemsApi::class.java)
            return itemListsApi.getItems().body()
        }
    }

    class ItemsViewModel : ViewModel() {

        private val _itemList = MutableLiveData<List<com.murali.composepractice.data.Result>>()

        val itemList: LiveData<List<com.murali.composepractice.data.Result>> = _itemList

        fun updateItems() {
            viewModelScope.launch {
                val items = Repo().getListItems()
                _itemList.value = items?.results
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposepracticeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavComponent()
                }
            }
        }
    }

    @Composable
    fun NavComponent() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "profile") {
            composable("profile") {
                val viewModel: ItemsViewModel = viewModel()

                val items = viewModel.itemList.observeAsState()

                lifecycleScope.launchWhenResumed {
                    viewModel.updateItems()
                }

                items.value?.let {
                    LazyColumn(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        itemsIndexed(items = it) { _, item ->
                            rowItem(title = item.content, body = item.author, navController = navController)
                        }
                    }
                }
            }

            composable(
                "details/?header={header}",
                arguments = listOf(navArgument("header") { })
            ) {
                var show  by remember { mutableStateOf(true) }
                Text(text = it.arguments?.getString("header") ?: "default value", modifier = Modifier.padding(40.dp))
                if(show) {
                    AlertDialog(
                        onDismissRequest = { },
                        confirmButton = {
                            TextButton(onClick = {show = false})
                            { Text(text = "OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = {show = false})
                            { Text(text = "Cancel") }
                        },
                        title = { Text(text = "Please confirm") },
                        text = { Text(text = "Should I continue with the requested action?") }
                    )
                }
            }
        }
    }
}
