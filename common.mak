CC = gcc -s -O2 -Wall
ifdef COMSPEC
# Windows
JAVACP = ".;..;../.."
else
# Linux or macOS
JAVACP = .:..:../..
endif
JAVA = java -cp $(JAVACP)
JAVAC = javac -classpath $(JAVACP) -encoding utf-8
RUNOBX = runobx
RUNXEX = start
RUNATR = start
XASM = xasm -q

.DELETE_ON_ERROR:  # always

%.exe: %.c
	$(CC) -o $@ $<

%.class: %.java
	$(JAVAC) $<

%.nex: %.asx ../nex.asx ../numendef.asx
	$(XASM) -o $@ -d numen=1 $<

%.obx: %.asx
	$(XASM) -d numen=0 $<
