package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author BuyWatermelon
 */
public class Client {

    private static int flag = 0;

    private static int BLOCK = 4096;

    private static ByteBuffer sendbuffer = ByteBuffer.allocate(BLOCK);

    private static ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);

    private final static InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(
            "localhost", 8888);

    public static void main(String[] args) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);

        Selector selector = Selector.open();

        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        socketChannel.connect(SERVER_ADDRESS);


        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> iterator;
        SelectionKey selectionKey;
        SocketChannel client;

        String receiveText;
        String sendText;

        int count;

        while (true) {

            selector.select();

            selectionKeys = selector.selectedKeys();
            iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {

                selectionKey = iterator.next();

                if (selectionKey.isConnectable()) {
                    System.out.println("client connect");
                    client = (SocketChannel) selectionKey.channel();

                    // 此通道是否正在进行连接操作。
                    if (client.isConnectionPending()) {
                        client.finishConnect();
                        System.out.println("完成连接!");
                        sendbuffer.clear();
                        sendbuffer.put("Hello,Server".getBytes());
                        sendbuffer.flip();
                        client.write(sendbuffer);
                    }

                    client.register(selector, SelectionKey.OP_READ);

                } else if (selectionKey.isReadable()) {

                    client = (SocketChannel) selectionKey.channel();

                    receivebuffer.clear();

                    count=client.read(receivebuffer);

                    if(count>0){
                        receiveText = new String( receivebuffer.array(),0,count);
                        System.out.println(receiveText);
                        client.register(selector, SelectionKey.OP_WRITE);
                    }

                } else if (selectionKey.isWritable()) {

                    sendbuffer.clear();

                    client = (SocketChannel) selectionKey.channel();

                    sendText = clientOperation();

                    sendbuffer.put(sendText.getBytes());

                    sendbuffer.flip();

                    client.write(sendbuffer);

                    client.register(selector, SelectionKey.OP_READ);
                } 
            }
            selectionKeys.clear();
        }
    }

    /**
     * 客户端操作
     * @return
     */
    private static String clientOperation(){
        System.out.println("请输入您的操作：A；领养小狗  B：领养小猫  C：领养鹦鹉  D：领养小鸡  E:查看宠物受欢迎程度");

        String chars = "ABCDE";

        String randomOperation = String.valueOf(chars.charAt((int) (Math.random() * 5)));
        System.out.println(randomOperation);

        return randomOperation;

        /*//控制台输入
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();*/
    }
}
