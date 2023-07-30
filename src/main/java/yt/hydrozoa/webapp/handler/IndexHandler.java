package yt.hydrozoa.webapp.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import yt.hydrozoa.webapp.IService;
import yt.hydrozoa.webapp.WebappServer;

import java.io.IOException;

/**
 * @author hydrozoa <https://www.youtube.com/hydrozoa>
 */
public class IndexHandler extends AbstractHandler {

    private IService service;

    private final String PAYLOAD = """
            <html>
                <head>
                    <title>Hello world</title>
                </head>
                <body>
                    <h1>Hello world</h1>
                </body>
            </html>
            """;

    public IndexHandler(IService service) {
        this.service = service;
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        sendMessage(200, PAYLOAD, httpServletResponse);
        request.setHandled(true);
    }

    /**
     * Sends a UTF-8 encoded message
     */
    protected void sendMessage(int status, String message, HttpServletResponse response) {
        try {
            // Declare response encoding and types
            response.setContentType("text/html; charset=utf-8");

            // Declare response status code
            response.setStatus(status);

            // Write back response
            response.getWriter().println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}