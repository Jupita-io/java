[![](https://jitpack.io/v/Jupita-io/Jupita-Agent-Android.svg)](https://jitpack.io/#Jupita-io/Jupita-Agent-Android)

# Jupita Agent Java SDK

This SDK is developed for Android using Java and utilizes Google’s Volley library to create the API calls required. This library will allow you to make the required `dump` API calls with Jupita Agent. All API calls are made asynchronously, thus there are event listeners available to handle the API results.

## Overview
Jupita Agent is an API product that provides deep learning powered analysis of conversational data, via many mediums, between an agent and client. Within the SDK documentation, `message_type` will simply refer to who is speaking. `message_type` 0 = `agent`, and `message_type` 1 = `client`, although these labels are handled by the SDK.

The required parameters for the APIs include setting `message_type`, along with assigning an `agent_id` + `client_id` to be passed - how this is structured or deployed is dependent on your systems/platforms architecture, therefore it is completely flexible and customizable. Please note when assigning the `agent_id` that no data will be available for that particular agent from any of the APIs until the agent has sent at least 1 utterance via the `dump` API. 

## APIs
There are 3 APIs within the Jupita Agent product – `dump` `rating` & `feed`:

- `Dump` allows you to dump each communication utterance (required)
- `Rating` allows you to retrieve the agents rating (optional)
- `Feed` provides you with some basic ratings statistics (optional)

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
Add the dependency in app’s build.gradle file;

```
dependencies {
                implementation 'com.github.Jupita-io:Jupita-Agent-Android:1.1.0'
	    }
```

### Step 3
Build Jupita Agent - '2' has been used to represent the agent_id;

```
String token = “your-token”;
Agent agent = new Agent.Builder(getApplicationContext())
                                    .setToken(token)
                                    .setAgent_id("2")
                                    .build();
```

### Step 4
Call the dump API as a message from Agent by specifying the message and client_id – represented as '3' below;

```
agent.dump("hi", "3",  Agent.AGENT, new Agent.DumpListener() {
            @Override

            public void onSuccess(String msg, double utterance) {

                        // Do something

                        Log.d("TEST", String.valueOf(utterance));

            }



            @Override

            public void onError(String statusCode, JSONObject response) {

                        // Check the error

                        Log.d("TEST", response.toString());

            }

});
```


Similarly, call the dump API whenever client responds back to the same agent by specifying the message and ID of the client;

```
agent.dump("hi, how are you?", "3",  Agent.CLIENT, new Agent.DumpListener() {
            @Override

            public void onSuccess(String msg, double utterance) {

                        // Do something

                        Log.d("TEST", String.valueOf(utterance));

            }



            @Override

            public void onError(String statusCode, JSONObject response) {

                        // Check the error

                        Log.d("TEST", response.toString());

            }

});
```

### Step 5
Call the rating API;

```
agent.rating(new Agent.RatingListener() {

@Override

            public void onSuccess(double rating) {

                        Log.d("TEST", String.valueOf(rating));

            }



            @Override

            public void onError(String statusCode, JSONObject response) {

Log.d("TEST", response.toString());

            }

});
```


### Step 6
Call the feed API;

```
agent.feed(new Agent.FeedListener() {

@Override

            public void onSuccess(JSONObject week) {

                        Log.d("TEST", week.toString());

            }



            @Override

            public void onError(String statusCode, JSONObject response) {

                        Log.d("TEST", response.toString());

            }

});
```

## Error handling
The SDK throws 2 errors:
- JSONException which occurs if the user input is not json compatible. This can be incorrect usage of strings when passed on to the Agent methods.
- IllegalArgumentException: this arises if the `message_type` set in the dump method is not 1 or 0, or the model name in rating method is not ‘JupitaV1’.

## Error codes
Error codes thrown are 401 when the token is incorrect and 400 when there is an attempt to dump gibberish content to the server, although the model does have an inbuilt gibberish detector.

## Libraries
Use Step 1 and 2 so that the Jupita Agent Android SDK is available within the scope of the project.
Currently the Jupita Agent Android SDK is dependent on volley 1.2.0 and takes the permission of the internet as soon as the SDK is added as a dependency.

## Classes
The available product under the Android SDK is Jupita Agent.

Jupita Agent can be constructed directly using the public constructor but it is highly recommended to use the Agent.Builder class to build the product. This will ensure that mistakes are not made while building Jupita Agent.

```
String token = “your-token”;
Agent agent = new Agent.Builder(getApplicationContext())
                                    .setToken(token)
                                    .setAgent_id("2")
                                    .build();
```

The builder constructs with the context of the application. This is needed for building the volley request queue. Next the token and agent_id needs to be set.

The built agent can now be used to call dump, rating and feed methods asynchronously.

### `dump` method definitions

```
public void dump(@NonNull String text, @NonNull String client_id, int type, boolean isCall, DumpListener dumpListener)
public void dump(@NonNull String text, @NonNull String client_id, int type, DumpListener dumpListener)
public void dump(@NonNull String text, @NonNull String client_id, DumpListener dumpListener)
public void dump(@NonNull String text, @NonNull String client_id)
```

If the values of `type` and `isCall` are not provided by default the values are considered 0 and false. Thus `text` and the `client_id` are essential when creating a `dump` request. To avoid illegal argument error use `Agent.AGENT` or `Agent.CLIENT` for type.

`DumpListener` is an interface which needs to be implemented to listen to results of the dump call. The onSuccess event returns the success message as well as the utterance rating as double.

### `rating` method definitions

```
public void rating(RatingListener ratingListener)
public void rating(@NonNull String modelName, RatingListener ratingListener)
```

The second rating definition is created for future use when there will be multiple models to choose from. At the moment only 1 model (JupitaV1) is supported. To avoid illegal argument error use `Agent.JupitaV1` for modelName.

RatingListener is an interface which needs to be implemented to listen to results of the rating call. The onSuccess event returns the rating as a double.

### `feed` method definitions

```
public void feed(FeedListener feedListener)
```

FeedListener is an interface which needs to be implemented to listen to results of the feed call. The onSuccess event returns the feed for the whole week as a JSONObject.

