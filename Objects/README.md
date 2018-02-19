# Back In Stock - Shared Objects

This project contains shared Java dependencies for both the [Database REST API](RestApi) and the [Notification Service](NotificationService). We have put these classes and interfaces into this separate project for build convenience.

This project does not need to be modified, but you may want to add any custom API integrations into this package. 

## Deployment

If you modify or add to this project, recompile its contents using the command `./gradlew build`. Be sure to also refresh + recompile all other projects that depend on this project. 

## Author

Chris Leung - [chrislzm](https://github.com/chrislzm)
