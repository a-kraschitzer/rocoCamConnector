# ROCO Cam Connector

This JAVA Library is designed to make it easy for you to receive images from a 
Roco H0 locomotive and use them in whatever way you desire.

## Getting Started

Clone this repository, or download the /owner/name/releases/latest/download/roco_cam_connector.jar .jar
and add it as a library to your project.

- Implement the `CamConnector` wherever you want the images to end up
- Implement the `setImage(byte[] img)` method. (Here you can display the data in a JFrame/web page or write it to a file.)
- The last step is to create an instance of `ComController`
- And pass an instance of the class that implements the `CamConnector` with the ip of the locomotive.

        ComController comController = new ComController();
        comController.startLoco(imageFileWriter, "192.168.1.114");

### Prerequisites

- Run example applications:
  -  Java Runtime Environment
  
- Use the library in your own application:
  - Java Development Kit

## Authors

* **Andreas Kraschitzer** - *General Programming* - [github](https://github.com/a-kraschitzer)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* Thank you Norbert Stejskal who had the idea for this project and suggestions for improvements

