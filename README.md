[![](https://jitpack.io/v/Jupita-io/Jupita-Java.svg)](https://jitpack.io/#Jupita-io/Jupita-Java)

# Jupita Java SDK

This SDK is developed for Android using Java and utilizes Google’s Volley library to create the API calls required. This library will allow you to make the required `dump` API calls with Jupita. All API calls are made asynchronously, thus there are event listeners available to handle the API results.

## Overview
Jupita is an API product that provides deep learning powered touchpoint analytics. Within the SDK documentation, `message_type` will simply refer to who is speaking. `message_type` 0 = `touchpoint`, and `message_type` 1 = `input`, although these labels are handled by the SDK.

The required parameters for the APIs include setting `message_type`, along with assigning an `touchpoint_id` + `input_id` to be passed - how this is structured or deployed is completely flexible and customizable. Please note when assigning the `touchpoint_id` that no data will be available for that particular touchpoint until the touchpoint has sent at least 1 utterance via the `dump` API. 

## APIs
There is one API within the Jupita product – `dump`:

- `Dump` allows you to dump each communication utterance.

## Quickstart
### Step 1
Add the following code under root build.gradle;

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2
Add the dependency in the applications build.gradle file;

```
dependencies {
                implementation 'com.github.Jupita-io:Jupita-Java:1.1.0'
	    }
```

### Step 3
Build Jupita - '2' has been used to represent the touchpoint_id;

```
String token = “your-token”;
Jupita touchpoint = new Jupita.Builder(getApplicationContext())
                                    .setToken(token)
                                    .setTouchpoint_id("2")
                                    .build();
```

### Step 4
Call the dump API as a message from touchpoint by specifying the message and input_id – represented as '3' below;

```
touchpoint.dump("Hello", "3", Jupita.TOUCHPOINT, new Jupita.DumpListener() {
            @Override

            public void onSuccess(String msg, double utterance) {

                        Log.d("TEST", String.valueOf(utterance));

            }



            @Override

            public void onError(String statusCode, JSONObject response) {

                        Log.d("TEST", response.toString());

            }

});
```


Similarly, call the dump API whenever input responds back to the same touchpoint by specifying the message and ID of the input;

```
touchpoint.dump("Hi, how are you?", "3",  Jupita.INPUT, new Jupita.DumpListener() {
            @Override

            public void onSuccess(String msg, double utterance) {

                        Log.d("TEST", String.valueOf(utterance));

            }



            @Override

            public void onError(String statusCode, JSONObject response) {

                        Log.d("TEST", response.toString());

            }

});
```

## Error handling
The SDK throws 2 errors:
- JSONException which occurs if the user input is not json compatible. This can be incorrect usage of strings when passed on to the Jupita methods.
- IllegalArgumentException: this arises if the `message_type` set in the dump method is not 1 or 0.

## Error codes
Error codes thrown are 401 when the token is incorrect.

## Libraries
Use Step 1 and 2 so that the Jupita Java SDK is available within the scope of the project.
Currently the Jupita Java SDK is dependent on volley 1.2.0 and takes the permission of the web as soon as the SDK is added as a dependency.

## Classes
The available product under the Java SDK is Jupita.

Jupita can be constructed directly using the public constructor but it is highly recommended to use the Jupita.Builder class to build the product. This will ensure that mistakes are not made while building Jupita.

```
String token = “your-token”;
Jupita touchpoint = new Jupita.Builder(getApplicationContext())
                                    .setToken(token)
                                    .setTouchpoint_id("2")
                                    .build();
```

The builder constructs with the context of the application. This is needed for building the volley request queue. Next the token and touchpoint_id needs to be set.

The built touchpoint can now be used to call dump method asynchronously.

### `dump` method definitions

```
public void dump(@NonNull String text, @NonNull String input_id, int type, boolean isCall, DumpListener dumpListener)
public void dump(@NonNull String text, @NonNull String input_id, int type, DumpListener dumpListener)
public void dump(@NonNull String text, @NonNull String input_id, DumpListener dumpListener)
public void dump(@NonNull String text, @NonNull String input_id)
```

If the values of `type` and `isCall` are not provided by default the values are considered 0 and false. Thus `text` and the `input_id` are essential when creating a `dump` request. To avoid illegal argument error use `Jupita.TOUCHPOINT` or `Jupita.INPUT` for type.

`DumpListener` is an interface which needs to be implemented to listen to results of the dump call. The onSuccess event returns the success message as well as the utterance rating as double.

If you require additional support just hit us up at support@jupita.io 
