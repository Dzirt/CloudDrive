package server;

import common.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

public class FileServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public static final int BUFFER_SIZE = 512;
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Server start sending file");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buff) throws Exception {
        System.out.println("Reading channel");

        byte[] bytes = new byte[buff.readableBytes()];
        buff.readBytes(bytes);
        String msg = new String(bytes);

        System.out.println(msg);

        RandomAccessFile raf = null;

        long length = -1;
        try {
            raf = new RandomAccessFile(msg.toString(), "r");
            length = raf.length();
        } catch (Exception e) {
            ctx.writeAndFlush("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage() + '\n');
            return;
        } finally {
            if (length < 0 && raf != null) {
                raf.close();
            }
        }

        //Отправляем размер файла
        ctx.writeAndFlush(Unpooled.wrappedBuffer(ByteUtils.longToBytes(length)));

        byte[] buffer;
        if (length <= BUFFER_SIZE)
            buffer = new byte[(int) length];
        else
            buffer = new byte[BUFFER_SIZE];

        while (raf.read(buffer) != -1) {
            ctx.writeAndFlush(Unpooled.wrappedBuffer(buffer));
        }

        ctx.writeAndFlush("eof");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server finished sending file");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        if (ctx.channel().isActive()) {
            ctx.writeAndFlush("ERR: " +
                    cause.getClass().getSimpleName() + ": " +
                    cause.getMessage() + '\n').addListener(ChannelFutureListener.CLOSE);
        }
    }
}
