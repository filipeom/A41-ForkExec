# Restaurant Web Service client

## Authors

Group A41

### Lead developer 

* **Paulo Dias** - *86492* - [PauloACDias](https://github.com/PauloACDias)

### Contributors

* **Filipe Marques** - *86411* - [filipeom](https://github.com/filipeom)
* **Jorge Martins** - *86456* - [Jorgecmartins](https://github.com/Jorgecmartins)

## About

This is a Java Web Service client

The client uses the wsimport tool (included with the JDK since version 6)
to generate classes that can invoke the web service and
perform the Java to XML data conversion.

The client needs access to the WSDL file,
either using HTTP or using the local file system.


## Instructions for using Maven

You must start jUDDI and server first.

The default WSDL file location is ${basedir}/src/wsdl .
The WSDL URL location can be specified in pom.xml
/project/build/plugins/plugin[artifactId="jaxws-maven-plugin"]/configuration/wsdlUrls

The jaxws-maven-plugin is run at the "generate-sources" Maven phase (which is before the compile phase).

To generate stubs using wsimport:

```
mvn generate-sources
```

To compile:

```
mvn compile
```

To run using exec plugin:

```
mvn exec:java
```

## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.


----
