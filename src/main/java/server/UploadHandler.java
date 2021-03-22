package server;

import common.Message;

import java.io.DataInputStream;
import java.io.File;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UploadHandler {
    private File file;
    private String filename;
    private long filesize;

    public UploadHandler(DataInputStream in) throws Exception {
        System.out.println("uploading");
        Message message = new Message("end");
        String msg = in.readUTF();

        if (message.getName().equals("filename"))
            filename = message.getData().toString();
        else if (message.getName().equals("filesize")) {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.put(message.getData());
            buffer.flip();//need flip
            filesize = buffer.getLong();
        } else if (message.getName().equals("data")) {
            file = new File("storage" + File.separator + filename);

            if (file.exists()) {
                Files.write(
                        Paths.get(filename),
                        message.getData(),
                        StandardOpenOption.APPEND);
            } else {
                Files.write(
                        Paths.get(filename),
                        message.getData(),
                        StandardOpenOption.CREATE);
            }
        } else if (message.getName().equals("end")) {
            if (file.length() == filesize)
                System.out.println("upload complete");
            else
                System.out.println("some data was missed");
        }
    }
}
