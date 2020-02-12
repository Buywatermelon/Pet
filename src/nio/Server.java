package nio;

import pets.Cat;
import pets.Chicken;
import pets.Dog;
import pets.Parrot;
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

/**
 *
 * @author BuyWatermelon
 */
public class Server {

    private  int flag = 0;

    private  int BLOCK = 4096;

    private ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK);

    private  ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK);

    private Selector selector;

    /**
     * 服务端接收数据
     */
    private String receiveText = null;

    /**
     * 服务端发送数据
     */
    private String sendText = null;

    public Server(int port) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        ServerSocket serverSocket = serverSocketChannel.socket();

        serverSocket.bind(new InetSocketAddress(port));

        selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server Start----8888:");
    }

    /**
     * 监听
     *
     * @throws IOException
     */
    private void listen() throws IOException {
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
    private void handleKey(SelectionKey selectionKey) throws IOException {

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
                receiveText = new String( receiveBuffer.array(),0,count);
                sendText = switchReceiveText(receiveText);
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

    private static String switchReceiveText(String receiveText){
        switch (receiveText) {
            case "A":
                Dog.dogCount += 1;
                System.out.println("dogCount: " + Dog.dogCount);
                break;
            case "B":
                Cat.catCount += 1;
                System.out.println("catCount: " + Cat.catCount);
                break;
            case "C":
                Parrot.parrotCount += 1;
                System.out.println("parrotCount: " + Parrot.parrotCount);
                break;
            case "D":
                Chicken.chickenCount += 1;
                System.out.println("chickenCount: " + Chicken.chickenCount);
                break;
            case "E":
                System.out.println("dog: " + Dog.dogCount + "\n" + "cat: " + Cat.catCount + "\n" + "parror: " + Parrot.parrotCount + "\n" + "chicken: " + Chicken.chickenCount);
                return  "dog: " + Dog.dogCount + "\n" + "cat: " + Cat.catCount + "\n" + "parror: " + Parrot.parrotCount + "\n" + "chicken: " + Chicken.chickenCount + "\n" +"ok";
            default:
                return "您的输入有误,请输入正确的操作";
        }
        return "ok";
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        int port = 8888;
        Server server = new Server(port);
        server.listen();
    }
}

