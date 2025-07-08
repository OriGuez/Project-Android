# Project-Android

The repository includes both the server and React app. Please refer to its README file for detailed instructions on running the server. You can fill the server with data by following instructions there.

## Server connectivity
- The server's path is located in res/values/strings.xml and called "BaseUrl".
- currently the setup is to the local machine (10.0.2.2) to 8080 port with http.
- please make sure the server listen to the same port.
- server's timeout time is set to 3 seconds for connect,read and write. you can change it in the api/RetrofitClient class.

## Starting the app
- Please clone the project to Android Studio.
- make sure gradle dependencies update when you open the project.

project was tested on Android Studio on android API's 30 and 34.
was tested on local machine on the emulator.
