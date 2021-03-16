package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientCommandHandler extends SimpleChannelInboundHandler<String> {
    public static final String LS_COMMAND = "\tls                       view all files from current directory\n";
    public static final String MKDIR_COMMAND = "\tmkdir (dirname)          create directory\n";
    public static final String TOUCH_COMMAND = "\ttouch (filename)         create file\n";
    public static final String CD_COMMAND = "\tcd (path)                change directory\n";
    public static final String RM_COMMAND = "\trm (filename)            remove file\\directory nickname\n";
    public static final String COPY_COMMAND = "\tcopy (src, target)       change nickname\n";
    public static final String CAT_COMMAND = "\tcat (filename)           show file\n";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("Auth complete!\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Message from client: " + msg);

        String command = msg
                .replace("\r", "")
                .replace("\n", "");
        if (command.equals("ls")) {
            //TODO smt
        } else if (command.equals("exit")) {
            System.out.println("Chanel closed");
            ctx.channel().closeFuture();
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
    }
}
