CC = gcc -s -O2 -Wall
JAVA = java -cp ".;..;../.."
JAVAC = javac -classpath ".;..;../.."
RUNOBX = runobx
RUNXEX = c:\atari\a800win\atari800win.exe -run
RUNATR = c:\atari\a800win\atari800win.exe
XASM = xasm.exe /p /q

.DELETE_ON_ERROR:  # always

%.exe: %.c
	$(CC) -o $@ $<

%.class: %.java
	$(JAVAC) $<

%.nex: %.asx ../nex.asx ../numendef.asx
	$(XASM) /o:$@ /d:numen=1 $<

%.obx: %.asx
	$(XASM) /d:numen=0 $<
