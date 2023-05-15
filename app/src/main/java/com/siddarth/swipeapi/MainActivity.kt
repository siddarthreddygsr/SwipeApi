package com.siddarth.swipeapi

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
//import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import okhttp3.RequestBody.Companion.asRequestBody
//import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.siddarth.swipeapi.ui.theme.SwipeAPITheme
import android.content.ContentResolver
import android.content.res.Configuration
import android.database.Cursor
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.time.format.TextStyle


class MainActivity : ComponentActivity() {
    private val productListState = mutableStateListOf<Product>()
    private var products: List<Product>? = null
    private val searchQueryState = mutableStateOf("")
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SwipeAPITheme {
                val navController = rememberNavController()

                getData { retrievedProducts ->
                    products = retrievedProducts
                    productListState.addAll(retrievedProducts.orEmpty())
                }

                println("Hello")
                println(productListState)

                NavHost(navController, startDestination = "productList") {
                    composable("productList") {
                        Scaffold(topBar = { /* Add top app bar if needed */ }, floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate("addProduct")
                                },
                                modifier = Modifier.padding(16.dp),
                                containerColor = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = "Add"
                                )
                            }
                        }, content = {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.background
                                ) {
                                    ProductList(products = productListState)
                                }
                        }, bottomBar = {
                        }
                        )
                    }
                    composable("addProduct") {
                        AddProductScreen(
                            onAddProduct = { product ->
                                println(product)

                                // Handle adding the product here
                                // For example, you can add the product to the list and navigate back to the product list screen
                                productListState.add(product)
                                navController.navigateUp()
                            }
                        )
                    }
                }
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    ProductList(products = productListState)
//                    FloatingActionButton(
//                        onClick = {  },
//                        modifier = Modifier
//                            .padding(16.dp),
//                        containerColor = MaterialTheme.colorScheme.secondary,
//                        shape = RoundedCornerShape(16.dp),
//                    ) {
//                        Icon(
//                            Icons.Filled.Add,
//                            tint = MaterialTheme.colorScheme.onPrimary,
//                            modifier = Modifier.size(24.dp),
//                            contentDescription = "Add"
//                        )
//                    }
//                }
//                productListState.forEach { product ->
//                    println("Product Name: ${product.product_name}")
//                    println("Product Price: ${product.price}")
//                    // Print other product details
//                }
            }
        }
    }
    @Composable
    fun AddProductScreen(
        onAddProduct: (Product) -> Unit
    ) {
        var productName by remember { mutableStateOf("") }
        var productType by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var tax by remember { mutableStateOf("") }
        val context = LocalContext.current
        val imageUriState = remember { mutableStateOf<Uri?>(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            imageUriState.value = uri
            println("Image URI: $uri")
            uploadImage(uri)
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Add Product",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
//                style = MaterialTheme.typography.h5,
//                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = {
                        Text("Product Name*", color = MaterialTheme.colorScheme.onSecondaryContainer)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = productName.isEmpty()
                )

                OutlinedTextField(
                    value = productType,
                    onValueChange = { productType = it },
                    label = { Text("Product Type*", color = MaterialTheme.colorScheme.onSecondaryContainer) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = productType.isEmpty()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price*", color = MaterialTheme.colorScheme.onSecondaryContainer) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = price.isEmpty(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

                    )

                OutlinedTextField(
                    value = tax,
                    onValueChange = { tax = it },
                    label = { Text("Tax*", color = MaterialTheme.colorScheme.onSecondaryContainer) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = tax.isEmpty(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Button(
                    onClick = {
                        launcher.launch("image/*")

                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Attach Image")

                }

                Button(
                    onClick = {
                        Toast.makeText(this@MainActivity, "Added product successfully!!", Toast.LENGTH_SHORT).show()
                        if (productName.isNotEmpty() && productType.isNotEmpty() && price.isNotEmpty() && tax.isNotEmpty()) {
                            val product = Product(
                                product_name = productName,
                                product_type = productType,
                                price = try {
                                    price.toDouble()
                                } catch (e: NumberFormatException) {
                                    0.0
                                },
                                tax = try {
                                    tax.toDouble()
                                } catch (e: NumberFormatException) {
                                    0.0
                                },
                                image = ""
                            )
                            onAddProduct(product)
                            RetrofitInstance.apiInterface.createProduct(product = product).enqueue(object: Callback<Product>
                            {
                                override fun onResponse(call: Call<Product>, response: Response<Product>) {

                                    if (response.isSuccessful) {
                                        val createdProduct = response.body()
                                        if (createdProduct != null) {
                                            // Product created successfully
                                            onAddProduct(createdProduct)
                                        } else {
                                            // Handle the case when the response body is null
                                        }
                                    } else {
                                        // Handle the case when the response is not successful
                                    }
                                }

                                override fun onFailure(call: Call<Product>, t: Throwable) {
                                    // Handle the failure case
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                        .height(48.dp),
                    enabled = productName.isNotEmpty() && productType.isNotEmpty() && price.isNotEmpty() && tax.isNotEmpty()
                ) {
                    Text("Add Product")
                }
            }
        }
    }
    fun getPathFromUri(contentResolver: ContentResolver, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    return it.getString(columnIndex)
                }
            }
        } catch (e: Exception) {
            Log.e("GetPathFromUri", "Error retrieving file path: $e")
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun uploadImage(uri: Uri?) {
//        val filePath = getPathFromUri(contentResolver, uri)
//        val inputStream = contentResolver.openInputStream(uri)
        var inputStream = uri?.let { contentResolver.openInputStream(it) }
//        if(inputStream == null){ inputStream = "" }
        val tempFile = createTempFile("temp", null)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        val filePath = tempFile.absolutePath
//        val filePath = uri?.let { getPathFromUri(contentResolver, it) }
        if (filePath != null) {
            val file = File(filePath)
            if (file.exists()) {
                val curlCommand = "curl -F \"file=@$filePath\" 0x0.st"
                // Execute the curl command here
//                Log.d("CurlCommand", curlCommand)
                executeCurlCommand(curlCommand = curlCommand)
            } else {
                Log.e("UploadImage", "File does not exist: $filePath")
            }
        } else {
            Log.e("UploadImage", "Failed to get file path from URI: $uri")
        }
    }
    fun executeCurlCommand(curlCommand: String) {
        try {
            val process = Runtime.getRuntime().exec(curlCommand)
            val inputStream = process.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val output = StringBuilder()

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                output.append(line)
            }

            bufferedReader.close()
            inputStream.close()

            val curlOutput = output.toString()
            Log.d("CurlCommand", curlCommand)
            Log.d("CurlOutput", curlOutput)
            println(output)
        } catch (e: Exception) {
            Log.e("CurlOutput", "Error executing curl command: ${e.message}")
        }
    }


    @Composable
    fun ProductList(products: List<Product>) {
        Column {
            TextField(
                value = searchQueryState.value,
                onValueChange = { searchQueryState.value = it },
                label = { Text("Search", color = MaterialTheme.colorScheme.onSecondaryContainer) },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            val filteredProducts = products.filter { product ->
                product.product_name.contains(searchQueryState.value, ignoreCase = true)
            }
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            LazyVerticalGrid(
                columns =if (isLandscape) GridCells.Fixed(4) else GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(filteredProducts) { product ->
                    ProductItem(product)
                }
            }
        }
    }

    @Composable
    fun ProductItem(product: Product) {
        Surface(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(0.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                val image = if (product.image=="") {
                    "https://nayemdevs.com/wp-content/uploads/2020/03/default-product-image.png"
                } else{
                    product.image
                }
                ImageFromUrl(url = image)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
//                        text = product.product_name.capitalize(),
                        text = if (product.product_name.capitalize().length > 15) {
                            "${product.product_name.capitalize().substring(0, 15)}..."
                        } else {
                            product.product_name.capitalize()
                        },
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    Text(text= product.price.toString(),
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = product.product_type, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(text = product.tax.toString(), color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
    }

    private fun getData(callback: (List<Product>?) -> Unit) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelMessage("The data is being fetched")
        progressDialog.show()

        RetrofitInstance.apiInterface.getData().enqueue(object : Callback<List<Product>?> {
            override fun onResponse(call: Call<List<Product>?>, response: Response<List<Product>?>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val products = response.body()
                    callback(products) // Invoke the callback with the list of products
                    Toast.makeText(this@MainActivity, "Data received", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    callback(null) // Invoke the callback with null to indicate failure
                }
            }

            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                callback(null) // Invoke the callback with null to indicate failure
            }
        })

    }

}

private fun ProgressDialog.setCancelMessage(s: String) {

}

@Composable
fun ImageFromUrl(url: String) {
    Box(modifier = Modifier
        .height(220.dp)
        .width(220.dp),
        contentAlignment = Alignment.Center
    ) {
        val painter = rememberImagePainter(
            data = url,
            builder = {
                // You can customize the image loading options here if needed
            }
        )

        Image(
            painter = painter,
            contentDescription = null, // Provide a content description if necessary
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.Center)
        )
    }
}
