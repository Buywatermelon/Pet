package nio;

import adopter.PetAdopter;
import observer.PetCounter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author BuyWatermelon
 */
public class Server {

    private int flag = 0;

    private int BLOCK = 4096;

    private ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK);

    private ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK);

    private Selector selector;

    /**
     * 服务端接收数据
     */
    private String receiveText = null;

    /**
     * 服务端发送数据
     */
    private String sendText = null;

    /**
     * 单例对象，宠物领养者
     */
    private PetAdopter petAdopter = PetAdopter.getInstance();

    /**
     * 单例对象，宠物计数器
     */
    private PetCounter petCounter = PetCounter.getInstance();

    /**
     * 缓存型线程池，根据jvm内存自动调整线程数量，通常用于执行一些生存期很短的异步型任务
     */
//    ExecutorService executor = Executors.newCachedThreadPool();
    ExecutorService executor = Executors.newFixedThreadPool(20);



    public Server(int port) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        ServerSocket serverSocket = serverSocketChannel.socket();

        serverSocket.bind(new InetSocketAddress(port));

        selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server Start----8888:");

        /**
         * 服务端初始化时，将宠物领养等事件注册到宠物计数器上
         */
        petCounter.setObservable(petAdopter);
    }

    /**
     * 监听
     *
     * @throws IOException
     */
    private void listen() throws IOException, ExecutionException, InterruptedException {
        while (true) {

            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                handleKey(selectionKey);
            }
        }
    }

    /**
     * 请求处理
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handleKey(SelectionKey selectionKey) throws IOException, ExecutionException, InterruptedException {

        ServerSocketChannel server;
        SocketChannel client;

        int count;

        if (selectionKey.isAcceptable()) {

            server = (ServerSocketChannel) selectionKey.channel();

            client = server.accept();

            client.configureBlocking(false);

            client.register(selector, SelectionKey.OP_READ);

        } else if (selectionKey.isReadable()) {

            client = (SocketChannel) selectionKey.channel();

            receiveBuffer.clear();

            count = client.read(receiveBuffer);

            if (count > 0) {
                receiveText = new String(receiveBuffer.array(), 0, count);
                sendText = setSendText(receiveText);
                client.register(selector, SelectionKey.OP_WRITE);
            }

        } else if (selectionKey.isWritable()) {

            sendBuffer.clear();

            client = (SocketChannel) selectionKey.channel();

            sendBuffer.put(sendText.getBytes());

            sendBuffer.flip();

            client.write(sendBuffer);

            client.register(selector, SelectionKey.OP_READ);
        }
    }

    private String setSendText(String receiveText) throws ExecutionException, InterruptedException {

        // 使用invokeAll是否可以1000次访问执行一次
        switch (receiveText) {
            case "A":
                petAdopter.setOperation("adoptDog");
                break;
            case "B":
                petAdopter.setOperation("adoptCat");
                break;
            case "C":
                petAdopter.setOperation("adoptParrot");
                break;
            case "D":
                petAdopter.setOperation("adoptChicken");
                break;
            case "E":
                petAdopter.setOperation("queryPopularity");
                break;
            default:
                return "您的输入有误,请输入正确的操作";
        }

        return executor.submit(petAdopter).get();
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        int port = 8888;
        Server server = new Server(port);
        server.listen();
    }
}

