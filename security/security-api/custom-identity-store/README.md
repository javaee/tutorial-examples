# Custom Identity Store

An application can also provide its own IdentityStore. Bundled with the application, this custom identity store can then be used for authentication and authorization.

This example demonstrates how a custom identity store `TestIdentityStore` can be defined and provided as part of the application being deployed.

In this example, the following user is defined along with the group he is in.

|User|Password|Group|
|----|--------|----|
|Joe|secret1|foo,bar|

When a request is made to the application with certain credentials, the configured authentication mechanism comes into effect and authentication is performed against the `TestIdentityStore` as defined in the application.

Post authentication, the application also verifies the roles the caller is in and sends the details as part of the response.

How to define credentials and the roles assigned to users is shown below:

```java
if (usernamePasswordCredential.compareTo("Joe", "secret1")) {
    return new CredentialValidationResult("Joe", new HashSet<>(asList("foo", "bar")));
}

```

Authentication mechanism, which gets invoked when a request is made, is defined in `ApplicationConfig` as shown below:

```java
@BasicAuthenticationMechanismDefinition(
        realmName = "file"
)

@ApplicationScoped
@Named
public class ApplicationConfig {

}
```
Please note that in GlassFish, when BasicAuthenticationMechanism is used as the authentication mechanism, the `realmName` basically is presented to user ,as a hint, when wrong credentials are provided by the user.


```bash
curl -I -u Joe http://localhost:8080/custom-identity-store/servlet
Enter host password for user 'Joe':
HTTP/1.1 401 Unauthorized
Server: GlassFish Server Open Source Edition  5.0
X-Powered-By: Servlet/3.1 JSP/2.3 (GlassFish Server Open Source Edition  5.0  Java/Oracle Corporation/1.8)
WWW-Authenticate: Basic realm="file"
Content-Length: 1090
Content-Language:
Content-Type: text/html
```
In this example, the authentication mechanism is `BasicAuthenticationMechanism`, as is evident from the snippet above.


When a request is made to the application, the roles the user is in, gets returned as part of the repsonse.

```java
@DeclareRoles({ "foo", "bar", "kaz" })
@WebServlet("/servlet")
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
Note that the container needs to be made aware of the supported roles, which is achieved with the help of the `@Declareroles` annotation as shown above.

```java
@DeclareRoles({ "foo", "bar", "kaz" })
```
In GlassFish 5.0, role to group mapping is enabled by default. Therefore, you do not need to bundle `web.xml` with the application to provide mapping between roles and groups.

In this example, we are using the credentials of user `Joe` to make a request and to validate the response according to the credentials defined in `TestIdentityStore` above.

**Steps:**

* Start the domain

`asadmin start-domain`

* Deploy the application

`asadmin deploy <project>/target/custom-identity-store.war`

After the application is deployed, we can make a request to the application using the URL shown below:

---

**Request URL:**

```bash
http://localhost:8080/custom-identity-store/servlet?name=Joe&password=secret1
```
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
http://localhost:8080/custom-identity-store/servlet?name=Joe&password=secret3
```

**Response:**


**`HTTP Status 401 - Unauthorized`**

**`type`** `Status report`

**`message`** `Unauthorized`

**`description`** `This request requires HTTP authentication.`

**`GlassFish Server Open Source Edition  5`**

---
