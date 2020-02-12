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
        // 打开服务器套接字通道
        // 底层: 在linux上面开启socket服务，启动一个线程。绑定ip地址和端口号
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 服务器配置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 检索与此通道关联的服务器套接字
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 进行服务的绑定
        serverSocket.bind(new InetSocketAddress(port));
        // 通过open()方法找到Selector
        // 底层： 开启epoll，为当前socket服务创建epoll服务，epoll_create
        selector = Selector.open();
        // 注册到selector，等待连接
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
            // 选择一组键，并且相应的通道已经打开
            selector.select();
            // 返回此选择器的已选择键集。
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                // 获得当前epoll的rdlist复制到用户态，遍历，同事删除当前rdlist中的事件
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
        // 接受请求
        ServerSocketChannel server;
        SocketChannel client;
        int count=0;
        // 测试此键的通道是否已准备好接受新的套接字连接。
        if (selectionKey.isAcceptable()) {
            // 返回为之创建此键的通道。
            server = (ServerSocketChannel) selectionKey.channel();
            // 接受到此通道套接字的连接。
            // 此方法返回的套接字通道（如果有）将处于阻塞模式。
            client = server.accept();
            // 配置为非阻塞
            client.configureBlocking(false);
            // 注册到selector，等待连接
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            // 返回为之创建此键的通道。
            client = (SocketChannel) selectionKey.channel();
            //将缓冲区清空以备下次读取
            receiveBuffer.clear();
            //读取服务器发送来的数据到缓冲区中
            count = client.read(receiveBuffer);
            if (count > 0) {
                receiveText = new String( receiveBuffer.array(),0,count);
                sendText = switchReceiveText(receiveText);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (selectionKey.isWritable()) {
            //将缓冲区清空以备下次写入
            sendBuffer.clear();
            // 返回为之创建此键的通道。
            client = (SocketChannel) selectionKey.channel();
            //向缓冲区中输入数据
            sendBuffer.put(sendText.getBytes());
            //将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
            sendBuffer.flip();
            //输出到通道
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

