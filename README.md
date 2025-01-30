# Link Scraper Service

This is a **Spring Boot service** that asynchronously scrapes `<link>` tags from a given URL. The scraped links and their attributes are stored in an H2 database.

##  Requirements
Ensure you have the following installed:
- **Java 21** 
- **Gradle 8+** (or use the Gradle wrapper)

---

##  Running the Application via CLI

### **1. Build the Project**
Run the following command to build the project:
```sh
./gradlew build
```
or on Windows:
```sh
gradlew.bat build
```

### **2. Run the Application**
```sh
./gradlew bootRun
```
or
```sh
java -jar build/libs/link-scraper-service-0.0.1-SNAPSHOT.jar
```
By default, the service runs on **`http://localhost:8080`**.

---

## The Database
This project uses an **in-memory H2 database**, which is automatically started with the application.

If you would like to inspect the DB while the application is running
open **`http://localhost:8080/h2-console`** in your browser and enter:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: *(leave empty)*
---

## Running the Tests via CLI

### **Run All Tests**
```sh
./gradlew test
```
or on Windows:
```sh
gradlew.bat test
```
