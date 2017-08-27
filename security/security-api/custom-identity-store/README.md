# Custom Identity Store

An application also can provide its own IdentityStore. Bundled with the application, this is the identity store, which is then used for authentication, authorization.

This example demonstrates how a custom identity store `TestIdentityStore` can be defined and provided as part of the application being deployed.

In this example, following user is defined along with the roles they are in.

|User|Password|Role|
|----|--------|----|
|reza|secret1|foo,bar|

When a request is made to the application with certain credentials, the authentication mechanism bundled with this application comes into affect and an authentication is done against the `TestIdentityStore` as defined in the application.

Post authentication, the application will also verify the roles the caller is in and will send the details as part of the response.

Defining credentials and the roles assigned to the users is done as shown below in `TestIdentityStore`:

```java
if (usernamePasswordCredential.compareTo("reza", "secret1")) {
    return new CredentialValidationResult("reza", new HashSet<>(asList("foo", "bar")));
}

```

With application, a custom `HttpAuthenticationMechanism` too is bundled which gets called during authentication process.

```java
@RequestScoped
public class TestAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {

        if (request.getParameter("name") != null && request.getParameter("password") != null) {

            // Get the (caller) name and password from the request
            // NOTE: This is for the smallest possible example only. In practice
            // putting the password in a request query parameter is highly
            // insecure
            String name = request.getParameter("name");
            Password password = new Password(request.getParameter("password"));

            // Delegate the {credentials in -> identity data out} function to
            // the Identity Store
            // Password internally gets verified using the PasswordHash specified while defining DatabaseIdentityStore.
            CredentialValidationResult result = identityStoreHandler.validate(
                new UsernamePasswordCredential(name, password));

            if (result.getStatus() == VALID) {
                // Communicate the details of the authenticated user to the
                // container. In many cases the underlying handler will just store the details
                // and the container will actually handle the login after we return from
                // this method.
                return httpMessageContext.notifyContainerAboutLogin(
                    result.getCallerPrincipal(), result.getCallerGroups());
            } else {
                return httpMessageContext.responseUnauthorized();
            }
        }

        return httpMessageContext.doNothing();
    }

}
```
Following snippet from `TestAuthenticationMechanism` delegates the validation task to underlying `DefaultIdentityStoreHandler`, which in turn validates the credentials against `TestIdentityStore` as defined in this application.

```java
CredentialValidationResult result = identityStoreHandler.validate(
    new UsernamePasswordCredential(name, password));
```

On successful authorization, container gets notified with caller details.

```java
return httpMessageContext.notifyContainerAboutLogin(
    result.getCallerPrincipal(), result.getCallerGroups());
```
If credentials are not valid, an unauthorized response is sent back to the caller with status `401` indicating that authentication has failed with provided credentails.
```java
return httpMessageContext.responseUnauthorized();
```

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
Notice that, the container itself needs to be made aware of the supported roles and it is done with the help of `@DeclareRoles` annotation as shown above.

```java
@DeclareRoles({ "foo", "bar", "kaz" })
```
In Glassfish 5.0, role to group mapping is enabled by default. Hence there is no need to bundle `web.xml` with the application to provide mapping between roles and the groups in Glassfish.

In this example, we would be using credentials of user `reza` to make a request and see if response is according to credentails as specified in `TestIdentityStore` above.

**Steps:**

* Start the domain

`asadmin start-domain`

* Deploy the application

`asadmin deploy <project>/target/custom-identity-store.war`

Post which, a request can be made to the application using the URL shown below:

---

**Request URL:**

```bash
http://localhost:8080/custom-identity-store/servlet?name=reza&password=secret1
```
**Response:**

```bash
web username: reza
web user has role "foo": true
web user has role "bar": true
web user has role "kaz": false
```
---

**If invalid credentials are used:**

---

**Request URL:**

```bash
http://localhost:8080/custom-identity-store/servlet?name=reza&password=secret3
```

**Response:**


**`HTTP Status 401 - Unauthorized`**

**`type`** `Status report`

**`message`** `Unauthorized`

**`description`** `This request requires HTTP authentication.`

**`GlassFish Server Open Source Edition  5`**

---
