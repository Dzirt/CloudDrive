 package client;

 import common.Message;

 import javax.swing.*;
 import java.awt.*;
 import java.io.*;
 import java.net.Socket;

 import static java.nio.charset.StandardCharsets.*;

 public class Client {
     private final Socket socket;
     private final DataInputStream in;
     private final DataOutputStream out;

     public Client() throws Exception{
         socket = new Socket("localhost", 1234);
         in = new DataInputStream(socket.getInputStream());
         out = new DataOutputStream(socket.getOutputStream());

         runClient();
     }

        private void runClient() throws Exception {
            JFrame frame = new JFrame();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400,300);

            JTextArea ta = new JTextArea();

            DefaultListModel<String> model = new DefaultListModel<>();
            JList<String> list = new JList<>(model);
            list.setBackground(Color.GRAY);
            //UpdateFileList(model);

            JButton authButton = new JButton("Auth");
            authButton.addActionListener(e -> {
                try {
                    out.write("asd asd".getBytes(UTF_8));
                    //UpdateFileList(model);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

            JButton uploadButton = new JButton("Upload");
            uploadButton.addActionListener(e -> {
/*                try {
                    uploadFile("1.txt");
                    //UpdateFileList(model);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }*/
            });

            JButton downloadButton = new JButton("Download");
            downloadButton.addActionListener(e -> {
                try {
                    downloadFile("1.txt");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });

            JButton removeButton = new JButton("Remove");
            removeButton.addActionListener(e -> {
                //TODO remove
            });

            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(authButton);
            buttonPane.add(uploadButton);
            buttonPane.add(downloadButton);
            buttonPane.add(removeButton);

            frame.getContentPane().add(BorderLayout.PAGE_START, ta);
            frame.getContentPane().add(BorderLayout.CENTER, list);
            frame.getContentPane().add(BorderLayout.PAGE_END, buttonPane);

            frame.setVisible(true);

        }

        private String removeFile(String fileName) throws Exception{
            out.writeUTF("remove");
            out.writeUTF(fileName);
            return in.readUTF();
        }

        private String downloadFile(String fileName) throws Exception{
            out.write(("storage" + File.separator + fileName).getBytes(UTF_8));
            try {
                File file = new File("client" + File.separator + fileName);
                if (!file.exists()) {
                    out.write(("storage" + File.separator + fileName).getBytes(UTF_8));
                    file.createNewFile();

                    long sz = in.read(new byte[215]);
                    System.out.println(sz);
                    long size = in.readLong();

                    if (size == 0) {
                        return "File is not exist";
                    }

                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[255];
                    for (int i = 0; i < (size + 255) / 256; i++) {
                        int read = in.read(buffer);
                        fos.write(buffer, 0 , read);
                    }
                    fos.close();
                    return "File downloaded!";
                } else {
                    return "File is already exist";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Ups!";
        }

        private String uploadFile(String fileName) throws Exception{

            try {
                File file = new File("client" + File.separator + fileName);
                if (file.exists()) {
                    out.write("upload".getBytes(UTF_8));
                    out.flush();
                    out.write("upload".getBytes(UTF_8));
                    out.flush();

                    ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                    objectOut.writeObject(new Message("filename", fileName));
                    objectOut.flush();

                    objectOut.writeObject(new Message("filesize", file.length()));
                    objectOut.flush();

                    FileInputStream fis = new FileInputStream(file);
                    int read = 0;
                    byte[] buffer = new byte[255];
                    while ((read = fis.read(buffer)) != -1) {
                        objectOut.writeObject(new Message("data", buffer));
                        objectOut.flush();
                    }

                    objectOut.writeObject(new Message("end"));
                    objectOut.flush();

                } else {
                    return "File is not exist";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Some error!";
        }

        private void UpdateFileList(DefaultListModel<String> model) throws IOException{
            model.clear();

            String files = GetFilesList();

            if ("ERROR".equals(files)) {
                System.err.println("ERROR");
                return;
            }

            String[] fileArray = files.split(" ");
            for (int i = 0; i < fileArray.length; i++) {
                model.addElement(fileArray[i]);
            }
        }

        private String GetFilesList() {
            try {
                StringBuilder sb = new StringBuilder();
                out.write("getFileList".getBytes(UTF_8));
                while (true) {
                    byte[] buffer = new byte[512];
                    int size = in.read(buffer);
                    sb.append(new String(buffer, 0 ,size));
                    if (sb.toString().endsWith("end")) {
                        break;
                    }
                }
                return sb.substring(0, sb.toString().length() - 4);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "ERROR";
        }
        public static void main(String[] args) throws Exception {
            new Client();
        }
    }
