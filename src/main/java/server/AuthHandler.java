package server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Promise;

public class AuthHandler extends ChannelDuplexHandler {
    private static String LOGIN;
    private static String PASSWORD;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("auth channel active");
        ctx.writeAndFlush("Welcome!\n");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (auth()) {
            ctx.pipeline().addLast(new ClientCommandHandler());
        }

        System.out.println("auth channel inactive");
        ctx.pipeline().remove(this);
        //ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String clientMSG = msg.toString();
        clientMSG = clientMSG.replace("\r", "")
                .replace("\n", "");
        if (clientMSG.split(" ").length == 2) {
            setLOGIN(clientMSG.split(" ")[0]);
            setPASSWORD(clientMSG.split(" ")[1]);
        }

        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("auth channel inactive");
        ctx.pipeline().remove(this);
    }

    public static void setLOGIN(String LOGIN) {
        AuthHandler.LOGIN = LOGIN;
    }

    public static void setPASSWORD(String PASSWORD) {
        AuthHandler.PASSWORD = PASSWORD;
    }


    // Returns true if auth was successfully
    private boolean auth() {
        return !LOGIN.equals("") && !PASSWORD.equals("");
    }
}
