## <u>Minecraft 1.8.8 (protocol version 47) :</u>

### Packet Order for connection to server :

| Packet Name           | Client → Server  | Server → Client |
|-----------------------|------------------|-----------------|
| `Login Start¹`        | `0x00`           |   —             |
| `Encryption Request²` | —                |   `0x01`        |
| `Encryption Response²` | `0x01`           |   —             |
| `Login Success`       | —                |   `0x02`        |
| `Set Compression`     | —                |   `0x03`        |

- *¹ : Handshake packet with status `2`.*<br>
- *² : Only send if `online-mode=true` in `server.properties`.*

### Handshake packet structure :

|        Field      |         Type       |                     Notes                  |
|-------------------|--------------------|--------------------------------------------|
| Packet ID         |    VarInt          | Always `0x00` for handshake                |
| Protocol version  |    VarInt          | `47` for minecraft 1.8.8                   |
| Server Address    |    String          | The hostname or IP of the server           |
| Server Port       |    Unsigned Short  | The port number or the server (e.g., '25565') |
| Next State        |    VarInt          | `1` for status, `2` for login               |