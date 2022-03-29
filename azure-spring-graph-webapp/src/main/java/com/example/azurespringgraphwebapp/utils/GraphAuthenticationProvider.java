package com.example.azurespringgraphwebapp.utils;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Sample GraphAuthenticationProvider class. An Authentication provider is required for setting up a
 * GraphServiceClient. This one extends BaseAuthenticationProvider which in turn implements IAuthenticationProvider.
 * This allows using an Access Token provided by Oauth2AuthorizationClient.
 */
public class GraphAuthenticationProvider
        extends BaseAuthenticationProvider {

    private OAuth2AuthorizedClient graphAuthorizedClient;

    /**
     * Set up the GraphAuthenticationProvider. Allows accessToken to be
     * used by GraphServiceClient through the interface IAuthenticationProvider
     *
     * @param graphAuthorizedClient OAuth2AuthorizedClient created by AAD Boot starter. Used to surface the access token.
     */
    public GraphAuthenticationProvider(@Nonnull OAuth2AuthorizedClient graphAuthorizedClient) {
        this.graphAuthorizedClient = graphAuthorizedClient;
    }

    /**
     * This implementation of the IAuthenticationProvider helps injects the Graph access
     * token into the headers of the request that GraphServiceClient makes.
     *
     * @param requestUrl the outgoing request URL
     * @return a future with the token
     */
    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL requestUrl){
        return CompletableFuture.completedFuture(graphAuthorizedClient.getAccessToken().getTokenValue());
    }
}
