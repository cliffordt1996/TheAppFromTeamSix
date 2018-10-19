# TheAppFromTeamSix
A simple program designed for developers to collaborate on issues to find solutions.

This application was created in a few months time during the Spring semester of 2018
by a team of four developers: Thomas Clifford, Nate Irwin, Troy Nance, and Dylan Webb.
The application began as a project for a Human Computer Interaction and Design course.
Driven by a team inspired to go beyond the minimum requirement of a simple wireframe, 
the application includes support for most actions such as issue creation, editing, 
prioritization, archiving, code uploading, commenting, and action shortcuts.

The project was originally a usability experiment, so not all features
were implemented due to time constraints; therefore, it is not recommended to use
this application for any professional use. Think of this application like a front end
for Stack Overflow, but for teams (yes, Stack Overflow for Teams exists now and is likely better). 
For those who are interested, feel free to expand on this application and modify it to suit your needs.

Regarding the name of the application "The App from Team Six"
    
    - The team simply couldn't think of a better name, so we used our assigned team number. 
      Positive thoughts for a new name are welcome.

A makefile for this project will be provided soon for those weary of jars and unwilling to compile it.

To use the application:

    1. Run the server application.
  
      a. Skip this step if you just want to run the client and server on your machine.
         If you intend to connect to the server remotely using a public IP, 
         you may need to port forward port 60100. The port is currently hard-coded (sorry)..
 
    2. Run the client application.
      
      a. Enter the host address (either IP, host address, or localhost)
      b. Enter a custom username (ex. AwesomeChuck)
      c. Enter a password
      
            - The password is simply a placeholder at this time. You may enter anything you like
              but a password you use regularly- that is not a good idea!

            - ABSOLUTELY DO NOT SEND PERSONAL/PRIVATE INFORMATION USING THIS APPLICATION.
              This application provides no guarentee that your data is safe from the bad guys!
      
      d. Have fun using the application! Try the following activities:

            - Connect multiple users on the same machine or other machines if you have port forwarded.

            - Try creating a new issue. Notice there are keyboard shortcuts for many actions.
                - Notice that you can upload a source code file. There is a sample C++ file
                  provided to you in this repo containing an error. See if you can solve it!
          
            - After creating a few issues, feel free to get others to collaborate on them!
            
            - Try archiving an issue, notice how it can no longer be modified!
            
            - Try modifying an issue. If you're the author of the issue, you can make changes!

            - Notice how the server indicates when an issue has been updated.
