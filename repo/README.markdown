[Adding Unmanaged Dependencies to a Maven Project](http://devcenter.heroku.com/articles/local-maven-dependencies)

```
yourproject
+- pom.xml
+- src
+- repo
   +- com
      +- example
         +- mylib
            +- 1.0
               +- mylib-1.0.jar
```

```xml
<repositories>
    <!--other repositories if any-->
    <repository>
        <id>project.local</id>
        <name>project</name>
        <url>file:${project.basedir}/repo</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>mylib</artifactId>
    <version>1.0</version>
</dependency>
```
