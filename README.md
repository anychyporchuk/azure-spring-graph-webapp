This test project is created based on this [tutorial](https://github.com/Azure-Samples/ms-identity-java-spring-tutorial/tree/main/2-Authorization-I/call-graph).

- [Setup](#setup)
  - [Clone or download this repository](#clone-or-download-this-repository)
  - [Register the webApp app (java-spring-webapp-call-graph)](#register-the-webapp-app-java-spring-webapp-call-graph)
- [Configure the webApp app (java-spring-webapp-call-graph) to use your app registration](#configure-the-webapp-app-java-spring-webapp-call-graph-to-use-your-app-registration)
- [Running the sample](#running-the-sample)

## Setup

### Clone or download this repository

From your shell or command line:

```console
    https://github.com/anychyporchuk/azure-spring-graph-webapp.git
    cd azure-spring-graph-webapp
```

or download and extract the repository .zip file.

### Choose the Azure AD tenant where you want to create your applications

As a first step you'll need to:

1. Sign in to the [Azure portal](https://portal.azure.com).
1. If your account is present in more than one Azure AD tenant, select your profile at the top right corner in the menu on top of the page, and then **switch directory** to change your portal session to the desired Azure AD tenant.

### Register the webApp app (java-spring-webapp-call-graph)

1. Navigate to the [Azure portal](https://portal.azure.com) and select the **Azure AD** service.
1. Select the **App Registrations** blade on the left, then select **New registration**.
1. In the **Register an application page** that appears, enter your application's registration information:
   - In the **Name** section, enter a meaningful application name that will be displayed to users of the app, for example `java-spring-webapp-call-graph`.
   - Under **Supported account types**, select **Accounts in this organizational directory only**.
   - In the **Redirect URI (optional)** section, select **Web** in the combo-box and enter the following redirect URI: `http://localhost:8080/login/oauth2/code/`.
1. Select **Register** to create the application.
1. In the app's registration screen, find and note the **Application (client) ID**. You use this value in your app's configuration file(s) later in your code.
1. Select **Save** to save your changes.
1. In the app's registration screen, select the **Certificates & secrets** blade in the left to open the page where we can generate secrets and upload certificates.
1. In the **Client secrets** section, select **New client secret**:
   - Type a key description (for instance `app secret`),
   - Select one of the available key durations (**In 1 year**, **In 2 years**, or **Never Expires**) as per your security posture.
   - The generated key value will be displayed when you select the **Add** button. Copy the generated value for use in the steps later.
   - You'll need this key later in your code's configuration files. This key value will not be displayed again, and is not retrievable by any other means, so make sure to note it from the Azure portal before navigating to any other screen or blade.
1. In the app's registration screen, click on the **API permissions** blade in the left to open the page where we add access to the Apis that your application needs.
   - Click the **Add permissions** button and then,
   - Ensure that the **Microsoft APIs** tab is selected.
   - In the *Commonly used Microsoft APIs* section, click on **Microsoft Graph**
   - In the **Delegated permissions** section, select the **User.Read** in the list. Use the search box if necessary.
   - Click on the **Add permissions** button in the bottom.
   - Repeat this operation for **Calendars.ReadWrite** and **MailboxSettings.Read** permissions.

#### Configure the webApp app (java-spring-webapp-call-graph) to use your app registration

Open the project in your IDE (Visual Studio Code or IntelliJ IDEA) to configure the code.

> In the steps below, "ClientID" is the same as "Application ID" or "AppId".

1. Open the `src\main\resources\application.yml` file.
1. Find the key `tenant-id` and replace the existing value with your Azure AD tenant ID.
1. Find the key `client-id` and replace the existing value with the application ID (clientId) of `java-spring-webapp-call-graph` app copied from the Azure portal.
1. Find the key `client-secret` and replace the existing value with the key you saved during the creation of `java-spring-webapp-call-graph` copied from the Azure portal.

## Running the sample

1. Open a terminal or the integrated VSCode terminal.
1. In the same directory as this readme file, run `mvn clean compile spring-boot:run`.
1. Open your browser and navigate to `http://localhost:8080`.

## Explore the sample

- Note the signed-in or signed-out status displayed.
- Click the context-sensitive button at the top right (it will read `Sign In` on first run)
- Follow the instructions on the next page to sign in with an account in the Azure AD tenant.
- On the consent screen, note the scopes that are being requested.
- Upon successful completion of the sign-in flow, you should be redirected to the home page.
- Note the context-sensitive button now says `Sign out` and displays your username to its left.
- If you are on the home page, you'll see an option to click **Events**.
- You will be redirected to the Events page where you can view list of default calendar events for the next week.
- You can add new event by filling **Create new event form** and pressing submit.
- You can update/delete from by searching required event via **Insert event id for update/delete:** and then change some fields via **Update/delete event** form and press **Update** or just remove event by pressing **Delete**
- You can also use the button on the top right to sign out. The status page will reflect this.
