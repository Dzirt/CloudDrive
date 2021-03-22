package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.lang.reflect.Field;

public class ClientCommandHandler extends SimpleChannelInboundHandler<String> {
    public static final Command HELP_COMMAND = new Command( "help", "available commands");
    public static final Command LS_COMMAND = new Command("ls", "view all files from current directory");
    public static final Command MKDIR_COMMAND = new Command("mkdir", "create directory");
    public static final Command TOUCH_COMMAND = new Command("touch", "create file");
    public static final Command CD_COMMAND = new Command("cd", "change directory");
    public static final Command RM_COMMAND = new Command("rm", "remove file or directory");
    public static final Command COPY_COMMAND = new Command("copy", "copy file");
    public static final Command CAT_COMMAND = new Command("cat","show file");

    public static final Command UPLOAD_COMMAND = new Command( "upload", "upload file");
    public static final Command DOWNLOAD_COMMAND = new Command( "download", "upload file");


    public static final Command EXIT_COMMAND = new Command("exit","just exit");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.write("Auth complete!\n");
        ctx.write("type 'help' for available commands\n");
        ctx.write(":> ");
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Message from client: " + msg);

        String command = msg
                .replace("\r", "")
                .replace("\n", "");
        if (command.equals(HELP_COMMAND.get())) {
            sendCommands(ctx);
        } else if (command.equals(UPLOAD_COMMAND.get())){
            ctx.pipeline().addLast(new LineBasedFrameDecoder(8192));
            ctx.pipeline().addLast(new ChunkedWriteHandler());
            ctx.pipeline().addLast(new FileServerHandler());
        } else if (command.equals(DOWNLOAD_COMMAND.get())){

        } else if (command.equals(EXIT_COMMAND.get())) {
            System.out.println("Chanel closed");
            ctx.channel().closeFuture();
            ctx.channel().close();
        }
        ctx.writeAndFlush(":> ");
    }

    private void sendCommands(ChannelHandlerContext ctx) throws IllegalAccessException {
        Field[] declaredFields = ClientCommandHandler.class.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                String cmdName = ((Command)(field.get(null))).get();
                String cmdDsc = ((Command)(field.get(null))).getDescription();
                sb.append("\t"
                        + cmdName
                        + " ".repeat(25 - cmdName.length())
                        + cmdDsc
                        + "\n");
            }
        }

        ctx.writeAndFlush(sb.toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
    }
}
