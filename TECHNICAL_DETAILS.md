# ⛏️🗺️ Minecraft 1.8.8 ([Protocol Version 47](https://minecraft.wiki/w/Protocol?oldid=2772100)) :

## Summary:

- 📋 Login State
- 📦 Packet Reader 
- 🔗 [VarInt explanation](VARINT.md)

---

## 📲 Login State:

### ➢ Packet Order for connection to server:

| Packet Name             | Client → Server  | Server → Client   |
|-------------------------|------------------|-------------------|
| `Login Start¹`          | `0x00`           | —                 |
| `Encryption Request²`   | —                | `0x01`            |
| `Encryption Response²`  | `0x01`           | —                 |
| `Set Compression³`      | —                | `0x03`            |
| `Login Success`         | —                | `0x02`            |

- *¹ : Handshake packet with status `2`.*<br>
- *² : Only send if `online-mode=true` in `server.properties`.*
- *³ : If compression is enabled in `server.properties` → `network-compression-threshold=<value>`*

---
### 👥 ClientBound:
#### ➢ `Login Start` packet structure:

| Field Name           | Type                | Description                                   |
|----------------------|---------------------|-----------------------------------------------|
| **Packet ID**        | `VarInt`            | Always `0x00` for handshake                   |
| **Protocol version** | `VarInt`            | `47` for minecraft 1.8.8                      |
| **Server Address**   | `String`            | The hostname or IP of the server              |
| **Server Port**      | `Unsigned Short`    | The port number or the server (e.g., '25565') |
| **Next State**       | `VarInt`            | `1` for status, `2` for login                 |
---
### 🌐 ServerBound:
#### ➢ `Disconnected` packet Structure:
| Field Name    | Type                            | Description                                                                    |
|---------------|---------------------------------|--------------------------------------------------------------------------------|
| **Packet ID** | `VarInt`                        | `0x00` - This is the packet ID that identifies this as the Disconnect packet.  |
| **reason**    | `String (Chat Component JSON)`  | The **reason** for disconnecting (UTF-8 encoded string).                       |

#### Example :
- If the reason for disconnect is `"Server is full"`, the packet could look like this in hexadecimal:

```
0D 00 12 7B 22 74 65 78 74 22 3A 22 4B 69 63 6B 65 64 20 66 6F 72 20 66 6C 79 69 6E 67 22 7D
```

#### 💡 Explanation:
- `0D`: Total **Packet Length** (13 bytes in this cases).
- `00`: The **Packet ID** for **Disconnect** (`0x00`).
- `12`: **Length of String** (18 characters).
- `7B 22 74 65 78 74 22 3A 22 4B 69 63 6B 65 64 20 66 6F 72 20 66 6C 79 69 6E 67 22 7D`: 
  - This is the **UTF-8 encoded JSON string**: `{"text":"Kicked for flying"}`.

#### 📝 Key Notes:
- **Reason**: JSON-encoded **kick message** (`String`, VarInt length-prefixed).
- **Used in**: **Login** State, sent from **Server** → **Client**
- **Sending it**: Send through a **TCP socket** to the minecraft client.  
---
#### ➢ `Login Success` packet Structure:

| Field Name    | Type                         | Description                                                                      |
|---------------|------------------------------|----------------------------------------------------------------------------------|
| **Packet ID** | `VarInt`                     | `0x02` - This is the packet ID that identifies this as the Login Success packet. |
| **UUID**      | `String (36-character UUID)` | The player's UUID (16 bytes, unique identifier).                                 |
| **username**  | `String`                     | The player's username (UTF-8 encoded string).                                    |

#### Example :
- If the player's **UUID** is `550e8400-e29b-41d4-a716-446655440000` and their **username** is "Steve", the packet data in hexadecimal could look like this:
```
2E 02 24 35 35 30 65 38 34 30 30 2D 65 32 39 62 2D 34 31 64 34 2D 61 37 31 36 2D 34 34 36 36 35 35 34 34 30 30 30 30 05 53 74 65 76 65
```

#### 💡 Explanation:
- `2E`: **Packet Length** (46 bytes).
- `02`: **Packet ID** for **Login Success** (`0x02`).
- `24`: **UUID String Length** (36 characters).
- `35 35 30 65 38 34 30 30 2D 65 32 39 62 2D 34 31 64 34 2D 61 37 31 36 2D 34 34 36 36 35 35 34 34 30 30 30 30`: 
  - The **UUID** (16 bytes) (`550e8400-e29b-41d4-a716-446655440000`).
- `05`: **Username Length** (`5` for `Steve`).
- `53 74 65 76 65`: **UTF-8 Encoded Username** (`Steve`).

#### 📝 Key Notes:
- The **UUID** is in **string format with dashes** (not a raw `16-byte` UUID).
- Both the **UUID** and **Username** are **length-prefixed UTF-8 strings** (using `VarInt` for length).
- **Sending it**: Send through a **TCP socket** to the minecraft client.
---
#### ➢ `Set Compression` packet structure:

| Field Name    | Type      | Description                                                                                |
|---------------|-----------|--------------------------------------------------------------------------------------------|
| **Packet ID** | `VarInt`  | `0x03`  - This is the packet ID that identifies this as the **Set Compression** packet.    | 
| **threshold** | `VarInt`  | The compression threshold value, in bytes.                                                 | 

#### Example :
- If the threshold is set to `256`, the packet data would look like this:
```
03 03 02 01 00
```
#### 💡 Explanation:
- `03`: **Packet Length** (3 bytes total).
- `03`: The **packet ID** for **Set Compression**.
- `02`: VarInt Length of Threshold (since `256` is stored as two bytes in VarInt format).
- `01 00`: 	Compression Threshold (`256` encoded as VarInt).
  <br><br>`256` is the default **threshold** value for minecraft servers.

#### 📝 Key Notes:
- The **threshold** determines when packets should be compressed:
  - If a packet's **uncompressed size** is ≥ `256 bytes`, it gets **compressed using Zlib**.
  - If a packet's **size** is < `256 bytes`, it remains **uncompressed**.
-  **VarInt encoding** is used for the threshold: `256` (`0x100`) is encoded as **two bytes**: `01 00`.


- After this packet is sent, the client must **start decompressing incoming packets** if their **size** exceeds the threshold.
- This packet is sent **during the Login State**, just before switching to Play mode.
- **Sending it**: Send through a **TCP socket** to the minecraft client.
---

## 📦 Packet Reader:

### 📦 `readVarInt()` method :
```java
public static int readVarInt(DataInputStream in) throws IOException {
    int value = 0;
    int position = 0;
    byte currentByte;

    do {
        currentByte = in.readByte();
        value |= (currentByte & 0x7F) << (position++ * 7);

        if (position > 5) {
            throw new IOException("VarInt too big");
        }
    } while ((currentByte & 0x80) != 0);

    return value;
}
```

### 📦 `readString()` method :
```java
public static String readString(DataInputStream in) throws IOException {
    int length = readVarInt(in); // Read string length
    byte[] bytes = new byte[length];
    in.readFully(bytes); // Read the UTF-8 encoded string
    return new String(bytes, "UTF-8"); // Convert bytes to string
}
```

### 📦 `readUUID()` method :
```java
public static UUID readUUID(DataInputStream in) throws IOException {
    long mostSigBits = in.readLong();  // Read first 8 bytes (most significant bits)
    long leastSigBits = in.readLong(); // Read next 8 bytes (least significant bits)
    return new UUID(mostSigBits, leastSigBits);
}
```