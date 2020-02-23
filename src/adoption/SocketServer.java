package adoption;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author BuyWatermelon
 */
public class SocketServer {

    private static PetService petService = new PetService();

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        ServerSocket server = new ServerSocket(8080);
        while (true) {
            Socket client = server.accept();
            Runnable runnable = () -> {
                try {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String request = buf.readLine();

                    RequestMethod method = resolveMethod(request);
                    List<String> params = resolveParams(request);
                    String response = resolve(method, params);

                    OutputStream outputStream = client.getOutputStream();
                    outputStream.write(response.getBytes("UTF-8"));

                    buf.close();
                    outputStream.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            executor.submit(runnable);
        }
    }

    private static String resolve(RequestMethod method, List<String> params) {
        String result;
        switch (method) {
            case GET:
                result = petService.adoptPet(params.get(0));
                break;
            case LIST:
                result = petService.queryPopularity();
                break;
            default:
                return "错误的请求方式";
        }
        return result;
    }

    private static RequestMethod resolveMethod(String request) {
        if (request.isEmpty()) {
            return null;
        }
        String[] requestResolverList = request.split(" ");
        return RequestMethod.valueOf(requestResolverList[0]);
    }

    private static List<String> resolveParams(String request) {
        if (request.isEmpty()) {
            return null;
        }
        List<String> params = new ArrayList<>();
        String[] requestResolverList = request.split(" ");
        for (int i = 0; i < requestResolverList.length; i++) {
            if (i != 0) {
                params.add(requestResolverList[i]);
            }
        }
        return params;
    }
}
