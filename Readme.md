# SwipeAPI

## Android project Folder Structure:
This structure of the project is given below use it for reference:
```
├─app
│  ├─-manifests
│  │    └--AndroidManifest.xml
│  ├──java
│  │    └--com.siddarth.swipeapi
│  │         ├──ui.theme
│  │         │  ├──Color.kt
│  │         │  ├──Theme.kt
│  │         │  └──Type.kt
│  │         ├── ApiInterface.kt
│  │         ├── MainActivity.kt
│  │         ├── Product.kt
│  │         ├── RetrofitInstance.kt
│  │         └─── Screen.kt
│  └-res
│     ├── drawable
│     │   ├── error.xml
│     │   ├── ic_launcher_background.xml
│     │   └── placeholder.xml
│     ├── drawable-v24
│     │   └── ic_launcher_foreground.xml
│     ├── mipmap-anydpi-v26
│     │   ├── ic_launcher.xml
│     │   └── ic_launcher_round.xml
│     ├── mipmap-anydpi-v33
│     │   └── ic_launcher.xml
│     ├── mipmap-hdpi
│     │   ├── ic_launcher.webp
│     │   └── ic_launcher_round.webp
│     ├── mipmap-mdpi
│     │   ├── ic_launcher.webp
│     │   └── ic_launcher_round.webp
│     ├── mipmap-xhdpi
│     │   ├── ic_launcher.webp
│     │   └── ic_launcher_round.webp
│     ├── mipmap-xxhdpi
│     │   ├── ic_launcher.webp
│     │   └── ic_launcher_round.webp
│     ├── mipmap-xxxhdpi
│     │   ├── ic_launcher.webp
│     │   └── ic_launcher_round.webp
│     ├── values
│     │   ├── colors.xml
│     │   ├── strings.xml
│     │   └── themes.xml
│     └── xml
│        ├── backup_rules.xml
│        └── data_extraction_rules.xml
│
└─Gradle Scripts
    ├──build.gradle(Project:Swipe:API)
    └--build.gradle(Module:app)

```

## Short Description:
```
The application appears is an API client for accessing and interacting with a remote server. It uses Retrofit, a popular library for making HTTP requests and handling API integrations in Android applications.
```

## Dependencies:
```
1. Retrofit
2. Gson Converter
3. Kotlin
4. Jetpack Compose
```
## Code Documentation:

There  are 4 important files that contains the major code of the application
1. MainActivity.kt : 
    1. Important Functions
        1. `onCreate(savedInstanceState: Bundle?)`: Overrides the onCreate method of the ComponentActivity class. It is called when the activity is starting. It sets the content view of the activity and initializes the navigation graph and other components.
        2. `AddProductScreen(onAddProduct: (Product) -> Unit)`: A composable function that represents the screen for adding a product. It takes a lambda function onAddProduct as a parameter, which will be called when a product is added. It displays input fields for product name, product type, price, and tax. It also allows attaching an image and handles the addition of the product.
        3. `getPathFromUri(contentResolver: ContentResolver, uri: Uri): String?`: A utility function that takes a ContentResolver and a Uri and returns the file path corresponding to the URI. It queries the content resolver to retrieve the file path from the URI.
        4. `uploadImage(uri: Uri?)`: This function is called when an image is selected for uploading. It takes a Uri parameter representing the selected image. It converts the Uri to a file path, creates a temporary file, and copies the contents of the selected image to the temporary file. Then, it executes a curl command to upload the image.
        5. `executeCurlCommand(curlCommand: String)`: This function is called to execute a curl command. It takes a curl command as a parameter, executes the command using Runtime.getRuntime().exec(), and captures the output of the command.
        6. `ProductList(products: List<Product>)`: A composable function that displays a list of products. It takes a list of products as a parameter and displays a search field and the list of products in a grid layout.
        7. `ProductItem(product: Product)`: A composable function that represents an item in the product list. It takes a product object as a parameter and displays the product image, name, price, product type, and tax.
    2. Import Variables
        1. `productListState`: A mutable state list of products. It holds the current state of the product list.
        2. `products`: A nullable list of products. It is initially set to null and later updated with the retrieved products.
        3. `searchQueryState`: A mutable state of the search query. It holds the current value of the search query.
2. RetrofitInstance.kt:
    1. `retrofit (private val)`: This is a lazily instantiated Retrofit object. It is created using the `Retrofit.Builder` class and configured with a base URL of `"https://app.getswipe.in/api/"` and a Gson converter factory created by `GsonConverterFactory.create()`. The `by lazy` delegate ensures that the object is instantiated only when accessed for the first time.
    2. `apiInterface (public val)`: This is a lazily instantiated property that provides an instance of the `ApiInterface` interface. It is created using the `retrofit` object's `create` method and passing `ApiInterface::class.java` as the service interface type. The `by lazy` delegate ensures that the instance is created only when accessed for the first time.
    * These components allow you to use the Retrofit library to interact with an API by accessing the singleton `RetrofitInstance.apiInterface` property, which provides an implementation of the `ApiInterface` for making network requests.
3. ApiInterface.kt
    1. `getData` function: This function is annotated with `@GET` and specifies the endpoint path as `"public/get"`. It returns a `Call` object that represents the asynchronous network request for retrieving data from the specified endpoint. The response type is `List<Product>`, indicating that a list of `Product` objects is expected in the response.

    2. `createProduct` function: This function is annotated with `@POST` and specifies the endpoint path as `"public/add"`. It accepts a `product` object as the request body, annotated with `@Body`. It returns a `Call` object that represents the asynchronous network request for creating a new `product`. The response type is `Product`, indicating that a single Product object is expected in the response.

    3. `GET`, `POST` annotations: These annotations specify the HTTP method to be used for the corresponding API endpoint. `@GET` is used for retrieving data, and `@POST` is used for creating data.

    4. `@Body` annotation: This annotation is used to indicate that the annotated parameter should be included as the request body in the network request. In the `createProduct` function, the `product` parameter is annotated with `@Body`, indicating that the `Product` object should be serialized and sent as the request body when making the POST request.

    * By using this ApiInterface, you can define the API endpoints and their corresponding HTTP methods for making network requests in your application.
4. Product.kt:
    1. `image`: A property of type String that represents the URL or path to the image of the product.

    2. `price`: A property of type Double that represents the price of the product.

    3. `product_name`: A property of type String that represents the name or title of the product.

    4. `product_type`: A property of type String that represents the type or category of the product.

    5. `tax`: A property of type Double that represents the tax applied to the product.

