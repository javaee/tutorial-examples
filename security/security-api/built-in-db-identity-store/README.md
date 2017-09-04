# Built-in Database Identity Store
JSR 375 mandates that a Java EE container MUST support built-in `IdentityStore` backed by a database.

To support this mandatory requirement, `DatabaseIdentityStore` comes bundled with GlassFish-RI.

This example demonstrates how you can configure a `DatabaseIdentityStore` to point to a backend database and then use it as an `IdentityStore`.

In this example, the following users are defined, along with the groups they are in.

|User|Password|Group|
|----|--------|----|
|Joe|secret1|foo,bar|
|Sam|secret2|foo,bar|
|Tom|secret2|foo|
|Sue|secret2|foo|

When a request is made to the application with certain credentials, the authentication mechanism bundled with this application comes into effect and authentication is performed against the `DatabaseIdentityStore` as defined in the application.

Post authentication, the application also verifies the roles the caller is in and sends the details as part of the response.

How to define credentials and the roles assigned to users is shown below:

```java

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.sql.DataSource;

@Singleton
@Startup
public class DatabaseSetup {

    // The default datasource that is bundled with GlassFish is used to store // credentials.
    @Resource(lookup="java:comp/DefaultDataSource")
    private DataSource dataSource;

    @PostConstruct
    public void init() {

        // ...      
        executeUpdate(dataSource, "INSERT INTO caller VALUES('Joe', '" + passwordHash.generate("secret1".toCharArray()) + "')");
        // ...        
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('Joe', 'foo')");
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('Joe', 'bar')");
        // ...        
    }

    @PreDestroy
    public void destroy() {
    	// ...
    }

    private void executeUpdate(DataSource dataSource, String query) {
        // ...
    }    
}

```
With `@Startup` annotation, this singleton enterprise bean is initialized during application startup and the credentials are set in the underlying database.

Built-in `DatabaseIdentityStore` gets mapped with the `DefaultDataSource` by defining the `ApplicationConfig` with the help of `@DatabaseIdentityStoreDefinition`.

```java
// Database Definition for built-in DatabaseIdentityStore
@DatabaseIdentityStoreDefinition(
    callerQuery = "#{'select password from caller where name = ?'}",
    groupsQuery = "select group_name from caller_groups where caller_name = ?",
    hashAlgorithm = Pbkdf2PasswordHash.class,
    priorityExpression = "#{100}",
    hashAlgorithmParameters = {
        "Pbkdf2PasswordHash.Iterations=3072",
        "${applicationConfig.dyna}"
    }
)

@ApplicationScoped
@Named
public class ApplicationConfig {

  public String[] getDyna() {
       return new String[]{"Pbkdf2PasswordHash.Algorithm=PBKDF2WithHmacSHA512", "Pbkdf2PasswordHash.SaltSizeBytes=64"};
   }

}
```
In this application, we are validating credentials using BASIC authentication mechanism. Following annotation in `ApplicationConfig` ensures that the `BasicAuthenticationMechanism` is used to perform credential validation.

```java
@BasicAuthenticationMechanismDefinition(
        realmName = "file"
)
```

Please note that in GlassFish, when BasicAuthenticationMechanism is used as the authentication mechanism, the `realmName` basically is presented to user ,as a hint, when wrong credentials are provided by the user.


```bash
curl -I -u Joe http://localhost:8080/built-in-db-identity-store/servlet
Enter host password for user 'Joe':
HTTP/1.1 401 Unauthorized
Server: GlassFish Server Open Source Edition  5.0
X-Powered-By: Servlet/3.1 JSP/2.3 (GlassFish Server Open Source Edition  5.0  Java/Oracle Corporation/1.8)
WWW-Authenticate: Basic realm="file"
Content-Length: 1090
Content-Language:
Content-Type: text/html
```

When a request is made to the application, the roles the user is in get returned as part of the repsonse.

```java
@WebServlet("/servlet")
@DeclareRoles({ "foo", "bar", "kaz" })
@ServletSecurity(@HttpConstraint(rolesAllowed = "foo"))
public class Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String webName = null;
        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }

        response.getWriter().write("web username: " + webName + "\n");

        response.getWriter().write("web user has role \"foo\": " + request.isUserInRole("foo") + "\n");
        response.getWriter().write("web user has role \"bar\": " + request.isUserInRole("bar") + "\n");
        response.getWriter().write("web user has role \"kaz\": " + request.isUserInRole("kaz") + "\n");
    }

}
```
Note that the container needs to be made aware of the supported roles, which is achieved with the help of `@DeclareRoles` annotation as shown above.

```java
@DeclareRoles({ "foo", "bar", "kaz" })
```
In GlassFish 5.0, role to group mapping is enabled by default. Therefore, you do not need to bundle `web.xml` with the application to provide mapping between roles and groups.

In this example, we are using the credentials of user `Joe` to make a request and validate the response according to the credentials/roles defined in `DatabaseSetup.java` above.

**Steps:**

* Since we are using the default datasource bundled with GlassFish for `DatabaseIdentityStore`, start the default database by running the following command:

`asadmin start-database`

* Start the domain

`asadmin start-domain`

* Deploy the application

`asadmin deploy <project>/target/built-in-db-identity-store.war`

After the application is deployed, we can make a request to the application using the URL shown below:

---

**Request URL:**

```bash
http://localhost:8080/built-in-db-identity-store/servlet
```

Since BASIC authentication is being used here, the container responds back prompting for username and password.

Once username and password are provided, the client presents the request to conainer with base64 encoded string and with `Authorization` header having value in the format expected for basic authentication.

With username and password available to the container, the validation is then done against `DatabaseIdentityStore`.

The corresponding `UsernamePasswordCredential` object is passed as parameter to `DatabaseIdentityStore#validate()` method.

Password is then fetched from database for user `Joe`. The password stored in database is hashed using `PBKDF2` algorithm. The password is then verified by built-in `Pbkdf2PasswordHash` implementation.

On successful verification, the request finally gets delegated to the servlet in question and response is returned to the end user.

**Response:**

```bash
web username: Joe
web user has role "foo": true
web user has role "bar": true
web user has role "kaz": false
```
---

**If invalid credentials are used:**

---

**Request URL:**

```bash
http://localhost:8080/built-in-db-identity-store/servlet
```

**Response:**


**`HTTP Status 401 - Unauthorized`**

**`type`** `Status report`

**`message`** `Unauthorized`

**`description`** `This request requires HTTP authentication.`

**`GlassFish Server Open Source Edition  5`**

---

In this application, we are using BasicAuthenticationMechanism.

When a request is made to the servlet in question,
container delegates the request to `org.glassfish.soteria.mechanisms.jaspic.HttpBridgeServerAuthModule`, which then invokes BasicAuthenticationMechanism#validateRequest method, and gets the credential from the request.
