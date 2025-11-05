JAVA LAN File Sharer

A desktop utility built in Java for simple, peer-to-peer file sharing on a local network.

✨ Features

Zero-Configuration Discovery: Automatically finds servers on the network using UDP broadcasting. No more IP-hunting!\
Reliable File Transfer: Uses TCP sockets to ensure every file is transferred completely and without corruption.\
Concurrent Server: The server is multithreaded and can handle multiple client connections simultaneously.\
Simple GUI: A clean, responsive graphical user interface built with Java Swing.\
Cross-Platform: Built with pure Java, so it can run on Windows, macOS, and Linux.

⚙️ How It Works: The Hybrid Protocol

The application uses a hybrid protocol model, separating the "discovery" phase from the "transfer" phase.

1\. Discovery (using UDP)

This phase is like "shouting" in a room to see who is present.\
Client: When a user clicks "Refresh," the client broadcasts a single UDP packet (message: LFS_DISCOVER_REQUEST) to the entire network on port 6790.\
Server: The server constantly listens on UDP port 6790. When it hears a request, it replies directly to that client's IP with a UDP packet containing its metadata (e.g., LFS_DISCOVER_RESPONSE|MyPC|report.pdf|2048KB).\
Client: The client collects all responses for 3 seconds, parses the metadata, and populates the list of available servers in the GUI.

2\. Transfer (using TCP)

This phase is like having a private, reliable phone call.\
Client: Once a user selects a server from the list, the client initiates a standard TCP connection to the server's IP on port 6789.\
Server: The server, which is also listening on TCP port 6789, accepts this connection. It then spins off a new ClientHandler thread to manage this specific user.\
Transfer: The server streams the file data over the reliable TCP connection, and the client writes it to the local disk. This process is efficient as the file is never fully loaded into memory.

🚀 How to Run

You will need the Java JDK (version 11 or newer) installed on your machine.

1\. Compile the Code

Open your terminal, navigate to the project directory, and run:\
Bash\
javac LanFileSharer.java

2\. Run the Server (Sender)

On the machine you want to send from, run the application:\
Bash\
java LanFileSharer

At the first prompt, select "Send File (Server)".\
Choose the file you want to share.\
The server is now active, hosting the file, and listening for discovery requests.

3\. Run the Client (Receiver)

On another computer on the same network, run the application:\
Bash\
java LanFileSharer

At the first prompt, select "Receive File (Client)".\
The discovery dialog will open. Click "Refresh".\
Servers on the network will appear in the list.\
Select a server and click "Connect".\
You will be prompted to choose a save location (defaults to "Downloads").

🛠️ Technologies Used

Java: Core application logic.\
Java Networking (Sockets):\
java.net.Socket & java.net.ServerSocket (for TCP file transfer).\
java.net.DatagramSocket & java.net.DatagramPacket (for UDP discovery).\
Java Swing:\
JFrame, JDialog, JList, JButton, etc., for the graphical user interface.\
Java Concurrency (Multithreading):\
java.lang.Thread to handle multiple clients and keep the GUI responsive.\
SwingUtilities.invokeLater to ensure thread safety when updating the GUI.\
Java I/O:\
FileInputStream and DataOutputStream for streaming file data efficiently.

📄 License

This project is licensed under the MIT License. See the LICENSE file for details.
