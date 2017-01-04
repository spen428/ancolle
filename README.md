AnColle
=======

AnColle is a GUI tool for keeping track of your anime and video game music
collection. It allows you to track any products or franchises found at
[VGMdb](http://vgmdb.net/). It uses the web API found at http://vgmdb.info/ to
interface with VGMdb.

## Controls

- Navigate with the mouse
- Add a product from the Product Tracker tab by clicking the "+" node, or by
  pressing `A`
- Remove a product from the Product Tracker by right-clicking it and selecting
  `Remove Product`
- On album view:
    - Left-click an album to toggle *collected* status
    - Middle-click an album to open album details
    - Right-click an album and click `Hide` to toggle *hidden* status
- Hidden items can be shown by selecting `View -> Show hidden items`

## Screenshots

![Product tracker](doc/product-view.png)

![Album tracker](doc/album-view.png)

![Album tracker](doc/album-details.png)

## Known bugs and limitations

- Updating details about an album or product requires the user to delete the
  cache
- Background tasks (such as the fetching of album covers) run on a single thread
  rather than a thread pool, and so execute quite slowly
- Selecting `View -> Show hidden items` while albums are still loading can halt
  the loading process
- Only one product tab may be open at a time

## Build

This project requires JDK 8 and JavaFX, and is built using the Ant build tool.
From the project directory, running `ant` should build the project to an
executable JAR which will be placed in the `dist/` directory.

Zipped release builds can be found in the `builds/` directory.

## Common errors

`Error: Could not find or load main class` occurs when your Java does not have
JavaFX bundled with it (which is the case for OpenJDK8). Install the JavaFX
package for your platform to fix this issue.
