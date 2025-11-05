# Java LAN File Sharer

A desktop utility built in Java for simple peer-to-peer file sharing on a local network.

## Features

* **Zero‑Configuration Discovery** automatically finds servers on the local network using UDP broadcasting.
* **Reliable File Transfer** using TCP sockets ensures files are delivered without corruption.
* **Multithreaded Server** supports multiple clients at once.
* **Simple GUI** designed with Java Swing.
* **Cross‑Platform** runs on Windows, macOS, and Linux.

## How It Works

The system uses a hybrid protocol to separate service discovery from actual file transfer.

### Discovery Phase (UDP)

* Client broadcasts `LFS_DISCOVER_REQUEST` on port `6790`.
* Server listens on UDP port `6790` and responds with metadata like `LFS_DISCOVER_RESPONSE|MyPC|file.pdf|2048KB`.
* Client collects responses for three seconds and lists available hosts.

### Transfer Phase (TCP)

* Client connects to the server on port `6789`.
* Server accepts the connection and starts a dedicated handler thread.
* File streams through TCP directly to disk, avoiding full memory loading.

## Run Instructions

### Requirements

Java JDK 11 or newer.

### Compile

```bash
javac LanFileSharer.java
```

### Start Server (Sender)

```bash
java LanFileSharer
```

Choose **Send File (Server)** and select a file.

### Start Client (Receiver)

```bash
java LanFileSharer
```

Choose **Receive File (Client)**. Use **Refresh** to detect servers, then connect and save the file.

## Technologies

* Java (core logic)
* Java Sockets: `Socket`, `ServerSocket` for TCP, `DatagramSocket`, `DatagramPacket` for UDP
* Java Swing for the UI
* Java Threads and `SwingUtilities.invokeLater`
* `FileInputStream` and `DataOutputStream` for streaming

## License

Licensed under the MIT License. Refer to the LICENSE file for details.
