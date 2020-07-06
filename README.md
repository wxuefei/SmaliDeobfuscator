# SmaliDeobfuscator
A simple smali deobfuscator to process the conflicts of same class and package names.

# Purpose
1) rename the smali files that confilicts with dir<br>
2) rename the smali files that filename have UTF-8 char

# Todo
1) rename the field's name & method's that have UTF-8 char<br>
2) rename the smali files with mapping list

# Notes
1) There has a issue in Mac OSX, the filesystem will convert \[ ʹ \](0xb4cd ) and \[ ʹ \](0xb9cb) to the same UTF8 char \[ ʹ \](0x02b9).

# Changelogs
2020-07-06
1)read real class from smali code. 
2)added patch to baksmali for duplicate filename issue under Mac OSX

   
