# AnColle CSS Selectors

This file documents the CSS selectors assigned to the GUI elements in this
application. Selectors are organised by the Java class to which they pertain,
and are structured hierarchically according to the organisation of the GUI.

- AnColle
    - VBox `#root`
        - MenuBar `.menu-bar`
            - Menu `#menu-file`
            - Menu `#menu-edit`
            - Menu `#menu-view`
                - CheckMenuItem `#menu-item-show-hidden-items`
            - Menu `#menu-tools`
            - Menu `#menu-help`
        - TabPane `.tab-pane`
            - Tab `#product-view-tab`
                - ScrollPane `.scroll-pane`
                    - ProductView extends TilePaneView `.product-view`
                        - ProductNode extends ItemNode `.product-node`
                        - ProductAdderNode extends ItemNode `.product-adder-node`
            - Tab `#album-view-tab`
                - ScrollPane `.scroll-pane`
                    - AlbumView extends TilePaneView `.album-view`
                        - AlbumNode extends ItemNode `.album-node`
            - [Tab ...] `.album-details-tab`
                - AlbumDetailsView `.album-details-view`
                    - VBox `.album-cover-container`
                        - ImageView `.album-cover`
                    - ScrollPane `.details-scrollpane`
                        - VBox `.details`
                            - Label `.title-en`
                            - Label `.title-ja`
                            - Label `.title-ja-latn`
                            - Label `.release-date`
                            - VBox `.track-list`
                                - Label `.track-list-header`
                                - [Label ...] `.track`
        - StatusBar `.status-bar`
            - Label `.status-label`

- ItemNode `.item-node`
    - ImageView `.image-view`
    - Label `.label1`
    - Label `.label2`

- TilePaneView `.tile-pane-view`
