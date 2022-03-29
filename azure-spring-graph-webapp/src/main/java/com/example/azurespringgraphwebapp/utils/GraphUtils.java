package com.example.azurespringgraphwebapp.utils;

import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import javax.annotation.Nonnull;

public final class GraphUtils {
    /**
     * getGraphServiceClient prepares and returns a graphServiceClient to make API calls to
     * Microsoft Graph. See docs for GraphServiceClient (GraphSDK for Java v3).
     *
     * Since the app handles token acquisition through AAD boot starter, we can give GraphServiceClient
     * the ability to use this access token when it requires it. In order to do this, we must create a
     * custom AuthenticationProvider (GraphAuthenticationProvider, see below).
     *
     *
     * @param graphAuthorizedClient OAuth2AuthorizedClient created by AAD Boot starter. Used to surface the access token.
     * @return GraphServiceClient GraphServiceClient
     */

    public static GraphServiceClient getGraphServiceClient(@Nonnull OAuth2AuthorizedClient graphAuthorizedClient) {
        return GraphServiceClient.builder().authenticationProvider(new GraphAuthenticationProvider(graphAuthorizedClient))
                .buildClient();
    }
}
