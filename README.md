Java LAN File Sharer
====================

A robust desktop utility built in Java for reliable, peer-to-peer file sharing on a local network using direct TCP connections.

✨ Features
----------

*   **Direct TCP Connection:** Uses standard TCP sockets for a stable, high-speed connection between devices.
    
*   **Reliable File Transfer:** Handles large files efficiently using buffered streams, ensuring data integrity.
    
*   **Multithreaded Server:** The server can handle multiple client connections simultaneously without blocking.
    
*   **Simple GUI:** A clean, lightweight interface built with Java Swing.
    
*   **Cross-Platform:** Runs on any device with Java installed (Windows, macOS, Linux).
    

⚙️ How It Works
---------------

This application follows a classic Client-Server architecture:

1.  **Server (Sender):** The user selects a file to host. The application starts a TCP server on port 6789 and displays the computer's local IP address.
    
2.  **Client (Receiver):** The user enters the Server's IP address manually.
    
3.  **Transfer:** The client establishes a direct socket connection. The server streams the file data (metadata + content), and the client saves it to the local disk.
    

🚀 How to Run
-------------

### Prerequisites

*   **Java JDK (version 11 or newer)** installed on your machine.
    

### 1\. Compile the Code

Open your terminal/command prompt in the project folder:

`   javac LanFileSharer.java   `

### 2\. Run the Server (Sender)

1.  java LanFileSharer
    
2.  Select **"Send File (Server)"**.
    
3.  Choose the file you want to share.
    
4.  **Important:** The log will display your IP address (e.g., Waiting for connections on IP: 192.168.1.5). **Tell this IP to the receiver.**
    

### 3\. Run the Client (Receiver)

1.  java LanFileSharer
    
2.  Select **"Receive File (Client)"**.
    
3.  A dialog box will appear. **Enter the IP address** of the server (e.g., 192.168.1.5).
    
4.  Choose where to save the file (defaults to "Downloads").
    
5.  The transfer will begin immediately.
    

🛠️ Technologies Used
---------------------

*   **Java:** Core application logic.
    
*   **Java Networking (java.net):**
    
    *   ServerSocket & Socket for establishing TCP connections.
        
    *   InetAddress for retrieving local IP details.
        
*   **Java Swing (javax.swing):**
    
    *   JFrame, JOptionPane, JFileChooser for the user interface.
        
*   **Java I/O (java.io):**
    
    *   DataInputStream/DataOutputStream for sending file metadata (name, size).
        
    *   FileInputStream/FileOutputStream for reading and writing file content.
        
*   **Concurrency:**
    
    *   Thread and Runnable to manage client connections in the background.
        

📄 License
----------

This project is licensed under the MIT License.

