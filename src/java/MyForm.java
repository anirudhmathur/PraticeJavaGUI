import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//import java.net.http.HttpHeaders;



public class MyForm extends JFrame{

    private JPanel mainPanel;
    private JButton button1;
    private JTextField textField1;
    private JTextField textField2;
    Map<String, String> map = new HashMap<String, String>();



    static void checkResponse(CacheResponseStatus e ) {
        switch (e) {
            case CACHE_HIT:
                System.out.println("A response was generated from the cache with no requests "
                        + "sent upstream");
                break;
            case CACHE_MODULE_RESPONSE:
                System.out.println("The response was generated directly by the caching module");
                break;
            case CACHE_MISS:
                System.out.println("The response came from an upstream server");
                break;
            case VALIDATED:
                System.out.println("The response was generated from the cache after validating "
                        + "the entry with the origin server");
                break;
        }
    };

    public MyForm(String title){
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        CacheEntry<Integer> cach1 = new CacheEntry<>("Anirudh",1);
        //String t = (String) cach1.getData();


        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CacheConfig cacheConfig = CacheConfig.custom()
                        .setMaxCacheEntries(3000)
                        .setMaxObjectSize(10240) // 10MB
                        .build();
                HttpHost proxy = new HttpHost("localhost", 8888);
              // CloseableHttpClient cachingClient = CachingHttpClients.custom().setCacheConfig(cacheConfig).setProxy(proxy).build();
                CloseableHttpClient cachingClient = CachingHttpClients.custom().setCacheConfig(cacheConfig).build();

                // httpClient = buildHttpClient(getHttpConnectionManager());
                System.setProperty("http.proxyHost", "localhost");
                System.setProperty("http.proxyPort", "8888");
                System.setProperty("https.proxyHost", "localhost");
                System.setProperty("https.proxyPort", "8888");
                String id = "1";
                if(textField2.getText() != null && textField2.getText().isEmpty() == false)
                {
                    id = textField2.getText();
                }
               // HttpRequest request = null;
                HttpGet httpget = null;

                if(map.get(id) == null)
                {
/*                     request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/users/"+id ))
                            .header("Cache-Control","max-age=0")
                            .header("User-Agent","Mozilla")
                            .build();
                    request.addHeader();*/
                    httpget = new HttpGet("http://localhost:8080/api/users/"+id);
                   // request.addHeader("Cache-Control", "public,max-age=1");

                }
                else
                {
                    String str = map.get(id);
                  /*  request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost.fiddler:8080/api/users/"+id ))
                            .header("If-Non-Match", str)
                            .header("Cache-Control","max-age=0")
                            .header("User-Agent","Mozilla")
                            .build();*/
                    httpget = new HttpGet("http://localhost:8080/api/users/"+id);
                    httpget.addHeader("If-None-Match",  str);
                    //request.addHeader("Cache-Control", "public,max-age=1");
                }


                String etag = "null";
                CloseableHttpResponse response = null;
                try {
                    HttpCacheContext context = HttpCacheContext.create();
                     response = cachingClient.execute(httpget, context);
//                    CacheResponseStatus responseStatus = (CacheResponseStatus) context.getAttribute(
//                            CachingHttpClient.CACHE_RESPONSE_STATUS);
                    CacheResponseStatus responseStatus = context.getCacheResponseStatus();
                    checkResponse(responseStatus);
                  // response = client.send(request, HttpResponse.BodyHandlers.ofString());
                   // httpClient.execute(, get, (response) -> processResponseWithBody(response, LINKED_ISSUE_COLLECTION_TYPE))
                    Header header = response.getFirstHeader("Etag");
                    etag = header.getValue();
                   // etag = etag.substring(1,etag.length() -1 );
                   map.put(id,etag);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                if(response != null)
                textField1.setText(String.valueOf(response.getStatusLine()) + "    " + etag);
                else
                    textField1.setText("null");

            }
        });
    }

    public static void main(String[] args){
        JFrame frame = new MyForm("My Form");
        frame.setVisible(true);
    }
}
