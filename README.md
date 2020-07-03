# SmaliDeobfuscator
A simple smali deobfuscator

# Purpose
1) rename the smali files that have dup name with dir
2) rename the smali files that name have UTF-8 char

# Todo
1) remove the UTF-8 char in field & method name
2) rename the smali files with mapping list

# Notes
1) There has a issue in Mac OSX, the filesystem will convert \[ ʹ \](0xb4cd ) & \[ ʹ \](0xb9ca) to the same UTF8 char 0x02b9 \[ ʹ \], 