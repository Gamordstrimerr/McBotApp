# 🔠 `VarInt` type in Java:

#### Description:
```
In Java, a VarInt (Variable-length Integer) is an integer encoding format that reduces the storage
space for numbers by using a variable number of bytes. Smaller numbers take fewer bytes, while larger
numbers take more bytes.
```

## 🤔 How VarInt works ?

#### Explanation:
- VarInt is commonly used in binary protocols, such as **Google Protocol Buffers (protobuf)** and **Minecraft's network protocol**, to efficiently encode integer values.

- Instead of using a fixed number of bytes (e.g., `int` always takes 4 bytes in Java), VarInt uses 1 to 5 bytes depending on the size of the number. The most significant bit (MSB) of each byte acts as a continuation bit:
  - If the MSB is `1`, it means the next byte is also part of the number.
  - If the MSB is `0`, it means this is the last byte of the number.
---
#### VarInt Encoding Rules:
1. Encode the integer in **7-bit chunks**.
2. If there are more bits left, set the **MSB to 1**.
3. If it's the last byte, set the MSB **to 0**.
---
#### Example Encoding:
Let's take `300` as an example:
1. Binary of `300` = `100101100`
2. Split into 7-bit chunks: `0000010 0101100`
3. Encode with MSB:
   - `0101100` → `10101100` (**MSB** `1` because there's more)
   - `0000010` → `00000010` (**MSB** `0` because it's the last byte)
4. **Encoded VarInt:** `10101100 00000010` (Hex: `AC 02`)
---
#### VarInt Implementation in Java:
Here's how you can encode and decode a VarInt in Java:

**Encoding a VarInt:**
```java
public static byte[] encodeVarInt(int value) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    while ((value & ~0x7F) != 0) { // While more than 7 bits remain
        outputStream.write((value & 0x7F) | 0x80); // Write 7 bits + continuation bit
        value >>>= 7; // Shift right to get next 7 bits
    }
    outputStream.write(value); // Write the last byte
    return outputStream.toByteArray();
}
```
**Decoding a VarInt**
```java
public static int decodeVarInt(InputStream inputStream) throws IOException {
    int value = 0;
    int position = 0;
    int currentByte;

    while (true) {
        currentByte = inputStream.read();
        if (currentByte == -1) {
            throw new EOFException("Unexpected end of VarInt");
        }

        value |= (currentByte & 0x7F) << (position * 7); // Extract 7 bits and shift
        if ((currentByte & 0x80) == 0) { // If MSB is 0, it's the last byte
            break;
        }

        position++;
        if (position > 4) { // VarInt is at most 5 bytes long
            throw new IOException("VarInt is too long");
        }
    }

    return value;
}
```