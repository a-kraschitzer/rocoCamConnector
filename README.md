# ROCO Cam Connector

This JAVA Library is designed to make it easy for you to receive images from a 
Roco H0 locomotive and use them in whatever way you desire.

## Getting Started
Clone this repository, or download the [Latest Release](https://github.com/a-kraschitzer/rocoCamConnector/releases/latest) .jar


### Try it out
After you downloaded the .jar, you can run it on your local system to test the functionality of this library.

- Open a command line window and navigate to the downloaded .jar file
- Execute `java -jar rocoCamConnector.jar`
- The application will run you through the possible options.

### Use the library
Following you can find a short explanation on how to use the library in your code.
For more details, please have a look at the [examples](https://github.com/a-kraschitzer/rocoCamConnector/tree/master/src/net/kraschitzer/roco/examples) folder.
- Add the repository or the .jar as a library to your project.
- Implement the `CamConnector` wherever you want the images to end up
- Implement the `setImage(byte[] img)` method. (Here you can display the data in a JFrame/web page or write it to a file.)
- The last step is to create an instance of `ComController`
- And pass an instance of the class that implements the `CamConnector` with the ip of the locomotive.

        ComController comController = new ComController();
        comController.connectLoco(imageFileWriter, "192.168.1.114");

### Prerequisites

- Run example applications:
  -  Java Runtime Environment
  
- Use the library in your own application:
  - Java Development Kit

## Authors

* **Andreas Kraschitzer** - [github](https://github.com/a-kraschitzer)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* Thank you Norbert Stejskal who had the idea for this project and suggestions for improvements

