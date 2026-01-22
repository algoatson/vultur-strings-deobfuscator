# Vultur String Deobfuscator
Automatic string deobfuscation tool for the Vultur Android banking trojan – Payload #2 (decompiled with jadx)

This tool scans jadx-generated `.java` source files, detects Vultur’s AES-based string obfuscation pattern, decrypts the embedded byte arrays, and replaces them inline with plaintext strings.

## Background
Vultur is an Android banking trojan first observed in March 2021. It is typically delivered via a dropper called Brunhilda, which masquerades as legitimate apps (e.g., McAfee Security). Brunhilda deploys Vultur in multiple payloads:

1. Payload #1 — obtains device registration and initial privileges
2. Payload #2 — provides Accessibility Service capabilities, file manager functionality, and acts as a bridge to payload #3
3. Payload #3 — contains the core trojan logic, including C2 methods and Firebase Cloud Messaging (FCM) command execution

Vultur uses AES encryption for string obfuscation in its payloads, as well as for C2 communication, making reverse engineering challenging. This tool currently targets payload #2, which is where many of Vultur’s Accessibility Service and FCM-related functions are implemented.


## Overview
Recent Vultur samples obfuscate strings using a custom AES routine that
stores encrypted data and the AES key in a single byte array.

After jadx decompilation, these strings typically appear as:
```java
h0.e.a(new byte[]{ ... })
```
This tool targets that pattern specifically.

## How it Works
1. Recursively scans a jadx output directory for `.java` source files
2. Identifies obfuscated string calls using regex
3. Extracts the byte array from each match
4. Splits the array into:
    - Ciphertext
    - AES key (last 16 bytes)
5. Decrypts the Ciphertext
6. Replaces the original obfuscated call with a plaintext Java string literal

All replacements are performed directly in the decompiled source files.

## Usage
### Compile:
```bash
javac JadxAutoDecrypt.java
```

### Run:
```bash
java JadxAutoDecrypt [path to jadx output]
```

If no path is provided, the tool defaults to:
```
./jadx-output
```
> ⚠️ Warning:
> This tool modifies .java files in-place.
> It is strongly recommended to keep a backup of your jadx output directory.

## Sample Coverage
The tool has been tested with multiple Vultur payload #2 samples:

| Package Name           | SHA-256 Hash                              | Description     |
|------------------------|-------------------------------------------|-----------------|
| com.medical.balance    | 1fc81b03703d64339d1417a079720bf0480fece3d017c303d88d18c70c7aabc3 | Vultur payload #2 |
| se.accessibility.app   | 7337a79d832a57531b20b09c2fc17b4257a6d4e93fcaeb961eb7c6a95b071a06 | Vultur payload #2 |
| com.exvpn.fastvpn      | c0f3cb3d837d39aa3abccada0b4ecdb840621a8539519c104b27e2a646d7d50d | Vultur payload #2 |
| jk.powder.tendence     | dc4f24f07d99e4e34d1f50de0535f88ea52cc62bfb520452bdd730b94d6d8c0e | Vultur payload #2 |
| se.talkback.app        | 5724589c46f3e469dc9f048e1e2601b8d7d1bafcc54e3d9460bc0adeeada022d | Vultur payload #2 |
| com.adajio.storm       | 0f2f8adce0f1e1971cba5851e383846b68e5504679d916d7dad10133cc965851 | Vultur payload #2 |


> ⚠️ Compatibility with other Vultur payloads or variants is not guaranteed.
> This tool is intended for malware research and analysis purposes only.

## References
- [NCC Group Analysis on Vultur](https://www.nccgroup.com/research-blog/android-malware-vultur-expands-its-wingspan/)