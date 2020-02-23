package adoption;

import petConst.PetCategory;
import util.RandomEnum;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author BuyWatermelon
 */
public class SocketClient {

    private static final RandomEnum<PetCategory> random = new RandomEnum<>(PetCategory.class);

    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(100);

        Runnable runnable = () -> {
            try {
                Socket client = new Socket("127.0.0.1", 8080);
                String request = "GET " + random.random();
                System.out.println(request);

                client.getOutputStream().write(request.getBytes("UTF-8"));
                client.shutdownOutput();

                InputStream inputStream = client.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                StringBuilder sb = new StringBuilder();
                while ((len = inputStream.read(bytes)) != -1) {
                    sb.append(new String(bytes, 0, len,"UTF-8"));
                }
                System.out.println(sb);

                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        executor.scheduleAtFixedRate(runnable, 0, 200, TimeUnit.MILLISECONDS);
    }
}
