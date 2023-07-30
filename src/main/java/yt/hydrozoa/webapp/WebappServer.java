package yt.hydrozoa.webapp;

import org.conscrypt.OpenSSLProvider;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import yt.hydrozoa.webapp.handler.IndexHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.Properties;

/**
 * @author hydrozoa <https://www.youtube.com/hydrozoa>
 */
public class WebappServer implements Runnable, IService {

    /**
     * Configuration of server read from res/config.properties
     */
    private Properties properties;

    @Override
    public void run() {
        System.out.println("Initializing...");

        properties = new Properties();
        try {
            properties.load(Files.newInputStream(Path.of("res/config.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Security.insertProviderAt(new OpenSSLProvider(), 1);

        // Configure jetty
        Server server = new Server();
        setupServer(
                server,
                Integer.parseInt(properties.getProperty("app.secure_port")),
                properties.getProperty("keystore.path"),
                properties.getProperty("keystore.password")
        );

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Ready to serve");
    }

    /**
     * Setup jetty server with TLS and handlers.
     * @param server
     * @param port
     * @param keystorePath
     * @param keystorePass
     */
    private void setupServer(Server server, int port, String keystorePath, String keystorePass) {
        // The HTTP configuration object.
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecurePort(port);
        httpConfig.addCustomizer(new SecureRequestCustomizer()); // Add the SecureRequestCustomizer because we are using TLS.

        // The ConnectionFactory for HTTP/1.1.
        HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);

        // The ConnectionFactory for HTTP/2.
        HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpConfig);

        // The ALPN ConnectionFactory.
        ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        // The default protocol to use in case there is no negotiation.
        alpn.setDefaultProtocol(http11.getProtocol());

        // Configure the SslContextFactory with the keyStore information.
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(keystorePath);
        sslContextFactory.setKeyStorePassword(keystorePass);

        // The ConnectionFactory for TLS.
        SslConnectionFactory tls = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

        // The ServerConnector instance.
        ServerConnector secureConnector = new ServerConnector(server, tls, alpn, http11, h2);
        secureConnector.setPort(port);

        server.addConnector(secureConnector);

        // Calls all added handlers in list order
        HandlerCollection root = new HandlerCollection();
        server.setHandler(root);

        // Create a ContextHandlerCollection to hold contexts
        ContextHandlerCollection ctxCol = new ContextHandlerCollection();
        root.addHandler(ctxCol);

        addHandler(ctxCol, "/", new IndexHandler(this));

        // support for serving /.well-known/acme-challenges/ in order to be issued certs
        ResourceHandler acmeHandler = new ResourceHandler();
        try {
            acmeHandler.setBaseResource(Resource.newResource(".well-known/acme-challenge/"));
            acmeHandler.setDirectoriesListed(true);
            acmeHandler.setDirAllowed(true);
            addHandler(ctxCol,"/.well-known/acme-challenge", acmeHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convenience-method that adds an AbstractHandler to a ContextHandlerCollection, at the specified path
     *
     * @param handlerCollection     Handler collection to add handler to
     * @param contextPath           Path for context (e.g. "files/")
     * @param handler               Handler to be added at specified path
     */
    private void addHandler(ContextHandlerCollection handlerCollection, String contextPath, AbstractHandler handler) {
        ContextHandler newContext = new ContextHandler(contextPath);
        newContext.setHandler(handler);
        handlerCollection.addHandler(newContext);
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
