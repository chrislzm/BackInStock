# Back In Stock - Shared Objects

This project contains shared Java dependencies for both the [Database REST API](RestApi) and the [Notification Service](NotificationService). The classes and interfaces in this project have been put here for build convenience.

This project does not need to be modified, but you may want to add any custom API integrations into this package.

## Deployment

If you modify or add to this project, recompile its contents using the command `./gradlew build`. Be sure to also refresh + recompile all other projects that depend on this project.

## Developer Reference

This project can be easily edited in [Eclipse for Java](http://www.eclipse.org/downloads/eclipse-packages/):
1. Ensure both Gradle and Eclipse are installed
2. Download this repository to your computer
3. In Eclipse, open **File** then **Import...**
4. Under **Gradle**, select **Existing Gradle Project** and click **Next** 
5. On the **Import Gradle Project** click **Browse** and open the root directory of the project
6. Click **Finish**

## License

Copyright (c) 2018 [Chris Leung](https://github.com/chrislzm)

Licensed under the MIT License. You may obtain a copy of the License in the [`LICENSE`](LICENSE) file included with this project.
