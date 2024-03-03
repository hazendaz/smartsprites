## ChangeLog for Smartsprites Prior to 0.2.13 ##

Smartsprites uses github releases now.  See [releases](https://github.com/hazendaz/smartsprites/releases) for new changes.

Legacy release notes can be found here.  Eventually, I will try to properly tag all these and move this same data over to releases instead given its not a huge amount.

See [index](https://hazendaz.github.io/smartsprites/website/index.html) for more detailed information.

- v0.2.12, released: March 2016

    - Dependency updates.

- v0.2.11, released: May 2015

    - Guava updated to version 18.

- v0.2.10, released: June 2013

    - Sprite-alignment should support center option [SMARTSPRITES-36](https://issues.carrot2.org/browse/SMARTSPRITES-36), patch contributed by [Artur](https://github.com/Artur-)

- v0.2.9, released: April 2013

    - CSS background-size support for Retina/HiDPI displays [SMARTSPRITES-87](https://issues.carrot2.org/browse/SMARTSPRITES-87)
    - Google Collections upgraded to Guava [SMARTSPRITES-76](https://issues.carrot2.org/browse/SMARTSPRITES-76)
    - Bugfix: [SMARTSPRITES-82](https://issues.carrot2.org/browse/SMARTSPRITES-82)
    - Bugfix: [SMARTSPRITES-83](https://issues.carrot2.org/browse/SMARTSPRITES-83)

- v0.2.8, released: February 2011

    - Possibility to use SHA512 hash or time stamp anywhere in sprite image name and path [SMARTSPRITES-41](https://issues.carrot2.org/browse/SMARTSPRITES-41)
    - Releasing SmartSprites binaries into Maven central repository, switching to Maven-based build process [SMARTSPRITES-79](https://issues.carrot2.org/browse/SMARTSPRITES-79)
    - Bugfix: [SMARTSPRITES-78](https://issues.carrot2.org/browse/SMARTSPRITES-78)

- v0.2.7, released: January 2011

    - Bugfix: [SMARTSPRITES-74](https://issues.carrot2.org/browse/SMARTSPRITES-74)
    - Bugfix: [SMARTSPRITES-71](https://issues.carrot2.org/browse/SMARTSPRITES-71)
    - Bugfix: [SMARTSPRITES-69](https://issues.carrot2.org/browse/SMARTSPRITES-69)


- v0.2.6, released: December 2009

    - Bugfix: [SMARTSPRITES-53](https://issues.carrot2.org/browse/SMARTSPRITES-53)
    - Bugfix: [SMARTSPRITES-59](https://issues.carrot2.org/browse/SMARTSPRITES-59)
      

- v0.2.5, released: December 2009

    - SmartSprites JAR available as a Maven artifact [SMARTSPRITES-18](https://issues.carrot2.org/browse/SMARTSPRITES-49)
    - Bugfix: [SMARTSPRITES-54](https://issues.carrot2.org/browse/SMARTSPRITES-54)

- v0.2.4, released: September 2009

    - Removal of duplicate images from sprites [SMARTSPRITES-18](https://issues.carrot2.org/browse/SMARTSPRITES-18).
    - Support for specifying individual CSS files to process [SMARTSPRITES-37](https://issues.carrot2.org/browse/SMARTSPRITES-37)

        ```
        --css-files: paths of CSS files to process, required if root-dir-path not specified, default: not specified.

        SmartSprites will process all CSS files listed using this option. If css-files is to be used together with output-dir-path,
        root-dir-path must also be specified so that SmartSprites can preserve the directory structure found in root-dir-path in output-dir-path.
        If root-dir-path and output-dir-path are used, css-files outside of root-dir-path will be ignored.

        Relative CSS file paths provided using this option will be resolved against the current working directory.
        Please note that SmartSprites will not expand any wildcards (like style/*.css), it assumes the expansion
        is performed at the command line shell level.

        To specify the list of CSS files to process in the SmartSprites Ant task, use one or more nested fileset elements.
        Please see the build.xml file in the distribution archive for an example.
        ```

    - Bugfix: [SMARTSPRITES-42](https://issues.carrot2.org/browse/SMARTSPRITES-42)
    - Bugfix: [SMARTSPRITES-45](https://issues.carrot2.org/browse/SMARTSPRITES-45)
    - Google Collections JAR updated to version 1.0-rc2.

- v0.2.3, released: April 2009

    - Bugfix: [SMARTSPRITES-31](https://issues.carrot2.org/browse/SMARTSPRITES-31).
    - Bugfix: [SMARTSPRITES-32](https://issues.carrot2.org/browse/SMARTSPRITES-32).
    - CSS file encoding parameter added [SMARTSPRITES-33](https://issues.carrot2.org/browse/SMARTSPRITES-33).

        ```--css-file-encoding: The encoding to assume for input and output CSS files, default: UTF-8. For the list of allowed values, please see the list of encodings supported in Java.```

- v0.2.2, released: April 2009

    - Support for appending SHA512 hashes or timestamps to sprite image URLs [SMARTSPRITES-25](https://issues.carrot2.org/browse/SMARTSPRITES-25).

        ```
         sha512: SHA512 hash of the sprite image file created by SmartSprites. The hash will change only if the sprite image content or file format changes.

         date: timestamp taken at the time of sprite image generation. The timestamp will be different every time SmartSprites processing is run, even if the sprite images do not change 
         ```

    - Bugfix: [SMARTSPRITES-26](https://issues.carrot2.org/browse/SMARTSPRITES-26).


- v0.2.1, released: October 2008

    - Ant not needed anymore to run SmartSprites.
    - Partial support for the ```<tt>!important</tt>``` modifier in ```<tt>background-image</tt>``` declarations, see [SMARTSPRITES-23](https://issues.carrot2.org/browse/SMARTSPRITES-23?focusedCommentId=10914#action_10914) for a more in-depth discussion.

- v0.2, released: August 2008

    - full support for transparency in GIF, PNG8, PNG24.
    - simple color quantization algorithm for GIF and PNG8.
    - document-root-relative image paths support.
    - arbitrary output directory support.
    - SmartSprites Ant task.

- v0.1, released: February 2008

    - incubation release.
