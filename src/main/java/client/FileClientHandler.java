package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private long filesize = -1;
    private long pos = -1;
    private int BUFFER_SIZE = -1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Channel active");
        //ctx.writeAndFlush(Unpooled.wrappedBuffer("storage/1.txt".getBytes()));
        ctx.writeAndFlush(Unpooled.wrappedBuffer("storage/100MB.bin".getBytes()));
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buff) throws Exception {
        String fileName = "client" + File.separator + "asd.txt";
        File file = new File(fileName);

        if (filesize == -1) {
            filesize = buff.readLong();
        } else {
            if (!file.exists()) {
                file.createNewFile();
            } else if (file.length() == filesize) {
                System.out.println("file already downloaded");
                ctx.fireChannelReadComplete();
            }

            byte[] bytes = new byte[buff.readableBytes()];
            buff.readBytes(bytes);

            Files.write(
                    Paths.get(fileName),
                    bytes,
                    StandardOpenOption.APPEND);
            System.out.println("saved " + bytes.length + " bytes");
        }

        if (file.length() >= filesize) {
            System.out.println("File downloaded!");
            ctx.fireChannelReadComplete();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

    }
}
