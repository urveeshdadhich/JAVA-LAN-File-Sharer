import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class LanFileSharer {

    private JFrame mainFrame;
    private JTextArea logArea;
    private static final int PORT = 6789;

    public static void main(String[] args) {
        // Ensure we run this on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LanFileSharer sharer = new LanFileSharer();

            // --- MODIFICATION ---
            // Ask user if they want to send or receive
            String[] options = {"Send File (Server)", "Receive File (Client)"};
            int choice = JOptionPane.showOptionDialog(
                    null, // parent component
                    "What would you like to do?", // message
                    "Lan File Sharer", // title
                    JOptionPane.DEFAULT_OPTION, // option type
                    JOptionPane.QUESTION_MESSAGE, // message type
                    null, // icon
                    options, // options
                    options[0] // default option
            );

            if (choice == 0) {
                // User chose "Send File"
                sharer.createServerUI();
                // Prompt for file *after* UI is visible
                sharer.promptForFileAndStartServer();
            } else if (choice == 1) {
                // User chose "Receive File"
                sharer.createClientUI();
                sharer.startClient();
            } else {
                // User closed the dialog
                System.exit(0);
            }
            // --- END MODIFICATION ---
        });
    }

    /**
     * Creates the main UI window.
     */
    private void createUI(String title) {
        mainFrame = new JFrame(title);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 300);
        mainFrame.setLocationRelativeTo(null); // Center screen

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);

        mainFrame.add(scrollPane, BorderLayout.CENTER);
        mainFrame.setVisible(true);
        log("Application started.\n");
    }

    private void createServerUI() {
        createUI("LAN File Sharer (Server)");
    }

    private void createClientUI() {
        createUI("LAN File Sharer (Client)");
    }

    /**
     * Appends a message to the text area.
     * Ensures it's thread-safe by using SwingUtilities.
     */
    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
        System.out.print(message + "\n");
    }

    // ========================================================================
    // SERVER LOGIC (Sender)
    // ========================================================================

    /**
     * --- NEW METHOD ---
     * Prompts the user to select a file and then starts the server.
     */
    private void promptForFileAndStartServer() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to send");

        // Set the default directory to user's home
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File fileToSend = fileChooser.getSelectedFile();
            // Start the server with the selected file
            startServer(fileToSend.getAbsolutePath());
        } else {
            log("No file selected. Server not started.");
            mainFrame.dispose(); // Close the app if they cancel
        }
    }

    /**
     * Starts the server to host a single file.
     * This method is now called by promptForFileAndStartServer.
     */
    private void startServer(String filePath) {
        File fileToSend = new File(filePath);
        if (!fileToSend.exists() || fileToSend.isDirectory()) {
            log("Error: File not found or is a directory: " + filePath);
            return;
        }

        // Start the server socket in a new thread to keep the UI responsive
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                log("Server started. Hosting: " + fileToSend.getName());

                String ip = InetAddress.getLocalHost().getHostAddress();
                log("Waiting for a client on IP " + ip + " (Port " + PORT + ")...");

                while (true) { // Keep server running to accept multiple clients
                    Socket clientSocket = serverSocket.accept();
                    log("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                    // Handle each client in its own thread
                    new Thread(new ClientHandler(clientSocket, fileToSend)).start();
                }

            } catch (IOException e) {
                log("Server Error: " + e.getMessage()); // <-- This line is fixed
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Thread to handle sending the file to a connected client.
     */
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private File fileToSend;

        public ClientHandler(Socket socket, File file) {
            this.clientSocket = socket;
            this.fileToSend = file;
        }

        @Override
        public void run() {
            try (DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                 FileInputStream fis = new FileInputStream(fileToSend)) {

                log("Sending file details...");
                dos.writeUTF(fileToSend.getName());
                dos.writeLong(fileToSend.length());

                log("Sending file data (" + fileToSend.length() + " bytes)...");
                byte[] buffer = new byte[8192]; // 8KB buffer
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                dos.flush();
                log("File sent successfully.");

            } catch (SocketException e) {
                log("Error sending file: " + e.getMessage());
            } catch (IOException e) {
                log("File transfer error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    // Ignore
                }
                log("Client disconnected.");
            }
        }
    }


    // ========================================================================
    // CLIENT LOGIC (Receiver)
    // ========================================================================

    /**
     * Starts the client to receive a file.
     */
    private void startClient() {
        String serverIp = JOptionPane.showInputDialog(
                mainFrame,
                "Enter Server IP Address:",
                "127.0.0.1"
        );

        if (serverIp == null || serverIp.trim().isEmpty()) {
            log("Client cancelled.");
            mainFrame.dispose();
            return;
        }

        // Start the client in a new thread to keep the UI responsive
        new Thread(() -> {
            try (Socket socket = new Socket(serverIp, PORT);
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                log("Connected to server: " + serverIp);

                // 1. Read file details
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                log("Receiving file: " + fileName + " (" + fileSize + " bytes)");

                // 2. Ask user where to save
                JFileChooser fileChooser = new JFileChooser();

                // --- MODIFICATION ---
                // Get user's home directory and append "Downloads"
                String homeDir = System.getProperty("user.home");
                File downloadsDir = new File(homeDir + File.separator + "Downloads");

                // Set the default directory for the file chooser
                if (downloadsDir.exists() && downloadsDir.isDirectory()) {
                    fileChooser.setCurrentDirectory(downloadsDir);
                } else {
                    // Fallback to home directory if Downloads doesn't exist
                    fileChooser.setCurrentDirectory(new File(homeDir));
                }
                // --- END MODIFICATION ---

                fileChooser.setSelectedFile(new File(fileName)); // Pre-populate the name

                if (fileChooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {

                    File fileToSave = fileChooser.getSelectedFile();

                    // The fix for your original bug is still here:
                    // We use fileToSave directly, not append the name again.

                    try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                        log("Saving file to: " + fileToSave.getAbsolutePath());

                        // 3. Read file data and write to disk
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        long totalRead = 0;

                        while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }

                        fos.flush();
                        log("File received successfully.");
                    }

                } else {
                    log("User cancelled save operation.");
                }

            } catch (FileNotFoundException e) {
                log("Error receiving file: " + e.getMessage());
                e.printStackTrace();
            }
            catch (UnknownHostException e) {
                log("Error: Unknown host " + serverIp);
            } catch (IOException e) {
                log("Client Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                log("Disconnected from server.");
            }
        }).start();
    }
}