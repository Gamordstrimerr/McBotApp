## Minecraft 1.8.8 (protocol version 47) :

### Packet Order for connection to server:

| Packet Name           | Client → Server  | Server → Client |
|-----------------------|------------------|-----------------|
| `Login Start¹`        | `0x00`           |   —             |
| `Encryption Request²` | —                |   `0x01`        |
| `Encryption Response²` | `0x01`           |   —             |
| `Set Compression³`     | —                |   `0x03`        |
| `Login Success`       | —                |   `0x02`        |

- *¹ : Handshake packet with status `2`.*<br>
- *² : Only send if `online-mode=true` in `server.properties`.*
- *³ : If compression is enabled in `server.properties` → `network-compression-threshold=`*

### `Login Start` packet structure:

|    Field Name     |         Type       |                    Description                 |
|-------------------|--------------------|--------------------------------------------|
| Packet ID         |    VarInt          | Always `0x00` for handshake                |
| Protocol version  |    VarInt          | `47` for minecraft 1.8.8                   |
| Server Address    |    String          | The hostname or IP of the server           |
| Server Port       |    Unsigned Short  | The port number or the server (e.g., '25565') |
| Next State        |    VarInt          | `1` for status, `2` for login               |

### `Set Compression` packet structure:

| Field Name| Type | Description                                                                                |
|-----------|------|--------------------------------------------------------------------------------------------|
| Packet ID | `byte` | `0x03`  - This is the packet ID that identifies this as the **Set Compression** packet.    | 
| threshold | VarInt | The compression threshold value, in bytes.                                                 | 

#### Example :
- If the threshold is set to `256`, the packet data would look like this:
``` 
0x03 0x01 0x00 0x00 0x00
```
#### Explanation:
- `0x03`: The **packet ID** for **Set Compression**.
- `0x01 0x00 0x00 0x00`: The **VarInt** encoding of the **threshold** value (`256`).
  <br><br>`256` is the default **threshold** value for minecraft servers.
