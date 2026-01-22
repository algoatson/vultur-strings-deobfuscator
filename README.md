# Vultur String Deobfuscator
Automatic string deobfuscation tool for **certain Vultur Android malware** samples
decompiled using **jadx**.

This tool scans jadx-generated `.java` source files, detects Vultur’s
AES-based string obfuscation pattern, decrypts the embedded byte arrays,
and replaces them inline with plaintext strings.

---

## Overview
Recent Vultur samples obfuscate strings using a custom AES routine that
stores encrypted data and the AES key in a single byte array.

After jadx decompilation, these strings typically appear as:
```java
h0.e.a(new byte[]{ ... })
```

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
### Compile
```bash
javac JadxAutoDecrypt.java
```

### Run
```bash
java JadxAutoDecrypt [path to jadx output]
```

If not path is provided, the tool defaults to:
```
./jadx-output
```
⚠️ Warning:
This tool modifies .java files in-place.
It is strongly recommended to keep a backup of your jadx output directory.

Tested Samples

This tool has been tested against specific Vultur samples exhibiting the
AES-based string obfuscation described above.

Due to frequent changes in Vultur’s obfuscation techniques between
campaigns, compatibility with all variants is not guaranteed.
