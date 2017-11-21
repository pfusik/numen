JAR = jar
JAVAC = javac
RUNJAR = java -jar

.PHONY: run
run: ColorEditor.jar
	$(RUNJAR) $<

ColorEditor.jar: pl/scene/taquart/coledit/ColorEditor.class MANIFEST.MF
	$(JAR) cvfm ColorEditor.jar MANIFEST.MF pl/scene/taquart/*

pl/scene/taquart/coledit/ColorEditor.class: ColorEditor.java pl/scene/taquart/PCXImage.class
	$(JAVAC) -d . $<

pl/scene/taquart/PCXImage.class: PCXImage.java
	$(JAVAC) -d . $<

MANIFEST.MF:
	echo Manifest-Version: 1.0>MANIFEST.MF
	echo Main-Class: pl.scene.taquart.coledit.ColorEditor>>MANIFEST.MF
	echo Created-By: 1.4.0 (Sun Microsystems Inc.)>>MANIFEST.MF

.PHONY: clean
clean:
	$(RM) ColorEditor.jar MANIFEST.MF
	rm -rf pl
